
package edu.ucb.project.feature.configremota

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
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
        "Esperando sincronizacion"
    )

    private val sincronizando = mutableStateOf(false)

    private val conectado = mutableStateOf(false)

    private var trabajoManual: Job? = null

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

        solicitarPermiso()

        // Mantener sincronizacion automatica.
        ConfigScheduler.programarPeriodico(
            applicationContext
        )

        // Limpiar trabajos manuales antiguos
        // de las versiones anteriores del Dashboard.
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
                    sincronizando = cargando,
                    onSincronizar = {
                        sincronizarManual()
                    }
                )
            }
        }

        // Sincronizar una vez al abrir.
        sincronizarManual()
    }

    private fun sincronizarManual() {

        if (trabajoManual?.isActive == true) {

            Log.d(
                TAG,
                "Ya existe una sincronizacion activa"
            )

            return
        }

        conectado.value = tieneInternet()

        if (!conectado.value) {

            estadoTrabajo.value =
                "Sin conexion: mostrando datos de Room"

            Log.w(
                TAG,
                "Sin conexion disponible"
            )

            return
        }

        trabajoManual = lifecycleScope.launch {

            sincronizando.value = true

            estadoTrabajo.value =
                "Consultando Firebase Remote Config..."

            try {

                Log.d(
                    TAG,
                    "SINCRONIZACION MANUAL INICIADA"
                )

                // El boton ejecuta el repositorio
                // sin esperar una cola de WorkManager.
                val repository = ConfigRemoteRepository(
                    applicationContext
                )

                val cambios = repository.sincronizar()

                estadoTrabajo.value =
                    if (cambios > 0) {

                        "Sincronizacion completa: " +
                                "$cambios version(es) procesada(s)"

                    } else {

                        "Sincronizacion completa: " +
                                "sin cambios nuevos"
                    }

                Log.d(
                    TAG,
                    "Sincronizacion manual finalizada"
                )

                Log.d(
                    TAG,
                    "Cambios procesados: $cambios"
                )

            } catch (e: CancellationException) {

                Log.w(
                    TAG,
                    "Sincronizacion cancelada"
                )

                throw e

            } catch (e: Exception) {

                estadoTrabajo.value =
                    "Error: ${
                        e.message ?: "No se pudo sincronizar"
                    }"

                Log.e(
                    TAG,
                    "ERROR EN SINCRONIZACION MANUAL",
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
    sincronizando: Boolean,
    onSincronizar: () -> Unit
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

        Text(
            text = estado,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = onSincronizar,
            enabled = !sincronizando,
            modifier = Modifier.fillMaxWidth()
        ) {

            if (sincronizando) {

                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text("Sincronizando...")

            } else {

                Text("Sincronizar Remote Config")
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

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

                    Text(
                        "Mensaje: ${ultima.mensaje}"
                    )

                    Text(
                        "Limite: ${ultima.limite}"
                    )

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
                        "Fecha: ${
                            formatearFecha(ultima.timestamp)
                        }"
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

                    Text(
                        "No hay configuracion guardada"
                    )
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

    return formato.format(
        Date(timestamp)
    )
}
