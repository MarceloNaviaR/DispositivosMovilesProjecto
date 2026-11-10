
package edu.ucb.project.feature.configremota

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope

import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.ConfigUpdateListenerRegistration
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConfigDashboardActivity : ComponentActivity() {

    companion object {
        private const val TAG = "CONFIG_SYNC"
    }

    private val estadoTrabajo = mutableStateOf(
        "Esperando configuracion remota"
    )

    private val sincronizando = mutableStateOf(false)

    private val conectado = mutableStateOf(false)

    private var trabajoInicial: Job? = null

    private var registroListener:
            ConfigUpdateListenerRegistration? = null

    private lateinit var repository: ConfigRemoteRepository

    private val remoteConfig =
        FirebaseRemoteConfig.getInstance()

    private val permisoNotificaciones =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { concedido ->

            if (!concedido) {
                Log.w(
                    TAG,
                    "Permiso de notificaciones denegado"
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = ConfigRemoteRepository(
            applicationContext
        )

        solicitarPermiso()

        // Programar sincronizacion automatica
        // mediante WorkManager cada 15 minutos.
        ConfigScheduler.programarPeriodico(
            applicationContext
        )

        // Cancelar trabajos manuales antiguos.
        ConfigScheduler.cancelarManualAnterior(
            applicationContext
        )

        val dao = ConfigDatabase
            .getDatabase(applicationContext)
            .configDao()

        conectado.value = tieneInternet()

        setContent {

            MaterialTheme {

                val historial by dao
                    .obtenerHistorial()
                    .collectAsState(
                        initial = emptyList()
                    )

                val ultima by dao
                    .observarUltima()
                    .collectAsState(
                        initial = null
                    )

                val estado by estadoTrabajo
                val cargando by sincronizando
                val hayInternet by conectado

                DashboardScreen(
                    ultima = ultima,
                    historial = historial,
                    conectado = hayInternet,
                    estado = estado,
                    sincronizando = cargando
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()

        conectado.value = tieneInternet()

        iniciarListener()
        sincronizarInicial()
    }

    override fun onStop() {

        registroListener?.remove()
        registroListener = null

        Log.d(
            TAG,
            "Listener remoto detenido"
        )

        super.onStop()
    }

    private fun iniciarListener() {

        if (registroListener != null) {
            return
        }

        Log.d(
            TAG,
            "Iniciando escucha de Remote Config"
        )

        registroListener =
            remoteConfig.addOnConfigUpdateListener(
                object : ConfigUpdateListener {

                    override fun onUpdate(
                        configUpdate: ConfigUpdate
                    ) {

                        val recibido =
                            SystemClock.elapsedRealtime()

                        val claves = configUpdate.updatedKeys

                        Log.d(
                            TAG,
                            "LISTENER_RECIBIO_ACTUALIZACION " +
                                    "hora=${System.currentTimeMillis()} " +
                                    "claves=$claves"
                        )

                        val importantes = setOf(
                            "mensaje",
                            "limite",
                            "feature_enabled"
                        )

                        if (claves.none { it in importantes }) {
                            return
                        }

                        lifecycleScope.launch {

                            sincronizando.value = true

                            estadoTrabajo.value =
                                "Cambio detectado. Actualizando..."

                            val inicioProcesamiento =
                                SystemClock.elapsedRealtime()

                            Log.d(
                                TAG,
                                "TIEMPO_DESPACHO_LISTENER=" +
                                        "${inicioProcesamiento - recibido} ms"
                            )

                            try {

                                val cambios = repository
                                    .procesarActualizacionRecibida()

                                estadoTrabajo.value =
                                    if (cambios > 0) {
                                        "Configuracion actualizada"
                                    } else {
                                        "Configuracion verificada"
                                    }

                                Log.d(
                                    TAG,
                                    "TIEMPO_TOTAL_DESDE_EVENTO=" +
                                            "${SystemClock.elapsedRealtime() - recibido} ms"
                                )

                                Log.d(
                                    TAG,
                                    "Actualizacion automatica completada"
                                )

                            } catch (e: CancellationException) {

                                throw e

                            } catch (e: Exception) {

                                estadoTrabajo.value =
                                    "Error: ${
                                        e.message
                                            ?: "No se pudo actualizar"
                                    }"

                                Log.e(
                                    TAG,
                                    "Error procesando cambio remoto",
                                    e
                                )

                            } finally {

                                sincronizando.value = false
                                conectado.value = tieneInternet()
                            }
                        }
                    }

                    override fun onError(
                        error: FirebaseRemoteConfigException
                    ) {

                        Log.e(
                            TAG,
                            "Error del listener Remote Config",
                            error
                        )

                        runOnUiThread {
                            estadoTrabajo.value =
                                "Error de escucha remota: " +
                                        error.message
                        }
                    }
                }
            )
    }

    private fun sincronizarInicial() {

        if (trabajoInicial?.isActive == true) {
            return
        }

        conectado.value = tieneInternet()

        if (!conectado.value) {

            estadoTrabajo.value =
                "Sin conexion: mostrando cache Room"

            return
        }

        trabajoInicial = lifecycleScope.launch {

            sincronizando.value = true

            estadoTrabajo.value =
                "Consultando configuracion inicial..."

            try {

                val cambios = repository.sincronizar()

                estadoTrabajo.value =
                    if (cambios > 0) {
                        "Configuracion sincronizada"
                    } else {
                        "Configuracion al dia. Escuchando cambios..."
                    }

                Log.d(
                    TAG,
                    "Carga inicial finalizada: $cambios"
                )

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                estadoTrabajo.value =
                    "Error inicial: ${
                        e.message ?: "Sin conexion con Firebase"
                    }"

                Log.e(
                    TAG,
                    "Error en carga inicial",
                    e
                )

            } finally {

                sincronizando.value = false
                conectado.value = tieneInternet()
            }
        }
    }

    private fun solicitarPermiso() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            val permiso =
                Manifest.permission.POST_NOTIFICATIONS

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    permiso
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                permisoNotificaciones.launch(permiso)
            }
        }
    }

    private fun tieneInternet(): Boolean {

        val manager = getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val network = manager.activeNetwork
            ?: return false

        val capabilities = manager
            .getNetworkCapabilities(network)
            ?: return false

        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        ) && capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_VALIDATED
        )
    }
}

@Composable
private fun DashboardScreen(
    ultima: ConfigVersionEntity?,
    historial: List<ConfigVersionEntity>,
    conectado: Boolean,
    estado: String,
    sincronizando: Boolean
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
    ) {

        Text(
            text = "E100-2",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Dashboard de configuracion remota",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = if (conectado) {
                "Estado: Conexion disponible"
            } else {
                "Estado: Cacheada (sin conexion)"
            },
            color = if (conectado) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (sincronizando) {

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        Text(
            text = estado,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // No hay boton de sincronizacion.
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Configuracion actual",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                if (ultima != null) {

                    Text("Mensaje: ${ultima.mensaje}")

                    Text("Limite: ${ultima.limite}")

                    Text(
                        "Feature enabled: ${ultima.featureEnabled}"
                    )

                    Text(
                        "Version RTDB: " +
                                if (ultima.synced) {
                                    "${ultima.remoteVersion}"
                                } else {
                                    "Pendiente"
                                }
                    )

                    Text(
                        "Fecha: ${formatearFecha(ultima.timestamp)}"
                    )

                    if (!conectado) {

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            "Mostrando ultima version cacheada",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                } else {

                    Text("No hay configuracion guardada")
                }
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "Historial de versiones",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (historial.isEmpty()) {

            Text(
                "Todavia no hay versiones registradas"
            )

        } else {

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {

                items(
                    items = historial,
                    key = { it.id }
                ) { config ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {

                            Text(
                                text = if (config.synced) {
                                    "Version ${config.remoteVersion}"
                                } else {
                                    "Version pendiente"
                                },
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                "Mensaje: ${config.mensaje}"
                            )

                            Text(
                                "Limite: ${config.limite}"
                            )

                            Text(
                                "Flag: ${config.featureEnabled}"
                            )

                            Text(
                                "Fecha: ${
                                    formatearFecha(config.timestamp)
                                }"
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatearFecha(
    timestamp: Long
): String {

    val formato = SimpleDateFormat(
        "dd/MM/yyyy HH:mm:ss",
        Locale.getDefault()
    )

    return formato.format(Date(timestamp))
}
