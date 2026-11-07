package edu.ucb.project.feature.configremota

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConfigDashboardActivity : ComponentActivity() {

    private val estadoTrabajo = mutableStateOf(
        "Esperando sincronizacion"
    )

    private val permisoNotificaciones =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { concedido ->
            if (!concedido) {
                estadoTrabajo.value =
                    "Notificaciones no autorizadas"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        solicitarPermiso()

        ConfigScheduler.programarPeriodico(
            applicationContext
        )

        val dao = ConfigDatabase
            .getDatabase(applicationContext)
            .configDao()

        WorkManager.getInstance(this)
            .getWorkInfosForUniqueWorkLiveData(
                ConfigScheduler.WORK_UNICO
            )
            .observe(this) { trabajos ->

                val trabajo = trabajos.firstOrNull()

                estadoTrabajo.value = when (trabajo?.state) {
                    WorkInfo.State.RUNNING ->
                        "Sincronizando..."
                    WorkInfo.State.SUCCEEDED ->
                        "Sincronizacion completa"
                    WorkInfo.State.FAILED ->
                        "Error de sincronizacion"
                    WorkInfo.State.ENQUEUED ->
                        "Sincronizacion pendiente"
                    WorkInfo.State.CANCELLED ->
                        "Sincronizacion cancelada"
                    else ->
                        "Esperando sincronizacion"
                }
            }

        setContent {
            MaterialTheme {

                val historial by dao
                    .obtenerHistorial()
                    .collectAsState(initial = emptyList())

                val ultima by dao
                    .observarUltima()
                    .collectAsState(initial = null)

                val estado by estadoTrabajo

                var conectado by remember {
                    mutableStateOf(tieneInternet(this))
                }

                LaunchedEffect(Unit) {
                    conectado = tieneInternet(
                        this@ConfigDashboardActivity
                    )
                }

                DashboardScreen(
                    ultima = ultima,
                    historial = historial,
                    conectado = conectado,
                    estado = estado,
                    onSincronizar = {
                        conectado = tieneInternet(this)

                        if (conectado) {
                            ConfigScheduler.sincronizarAhora(
                                applicationContext
                            )
                        } else {
                            estadoTrabajo.value =
                                "Sin conexion: usando cache local"
                        }
                    }
                )
            }
        }

        ConfigScheduler.sincronizarAhora(
            applicationContext
        )
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

    private fun tieneInternet(context: Context): Boolean {

        val manager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val network = manager.activeNetwork
            ?: return false

        val capabilities = manager
            .getNetworkCapabilities(network)
            ?: return false

        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }
}

@Composable
private fun DashboardScreen(
    ultima: ConfigVersionEntity?,
    historial: List<ConfigVersionEntity>,
    conectado: Boolean,
    estado: String,
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

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (conectado)
                "Estado: Conexion disponible"
            else
                "Estado: Cacheada (sin conexion)",
            color = if (conectado)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = estado,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSincronizar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sincronizar Remote Config")
        }

        Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(12.dp))

                if (ultima != null) {

                    Text("Mensaje: ${ultima.mensaje}")

                    Text("Limite: ${ultima.limite}")

                    Text(
                        "Feature enabled: " +
                                ultima.featureEnabled
                    )

                    Text(
                        "Version RTDB: " +
                                if (ultima.synced)
                                    "${ultima.remoteVersion}"
                                else
                                    "Pendiente"
                    )

                    Text(
                        "Fecha: ${formatearFecha(ultima.timestamp)}"
                    )

                    if (!conectado) {
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

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Historial de versiones",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (historial.isEmpty()) {

            Text("Todavia no hay versiones registradas")

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
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
                                text = if (config.synced)
                                    "Version ${config.remoteVersion}"
                                else
                                    "Version pendiente",
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Text("Mensaje: ${config.mensaje}")

                            Text("Limite: ${config.limite}")

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

private fun formatearFecha(timestamp: Long): String {

    val formato = SimpleDateFormat(
        "dd/MM/yyyy HH:mm:ss",
        Locale.getDefault()
    )

    return formato.format(Date(timestamp))
}
