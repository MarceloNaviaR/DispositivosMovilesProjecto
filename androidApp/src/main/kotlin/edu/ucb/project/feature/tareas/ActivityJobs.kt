package edu.ucb.project.feature.tareas

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ListenerRegistration

class ActivityJobs : ComponentActivity() {

    private lateinit var repository: RepositoryJobs
    private lateinit var preferencias: NotificationPreferences
    private lateinit var notificador: NotificationJobs

    private var listener: ListenerRegistration? = null

    private var tareas by mutableStateOf<List<jobs>>(emptyList())
    private var titulo by mutableStateOf("")
    private var error by mutableStateOf<String?>(null)
    private var cargando by mutableStateOf(true)
    private var guardando by mutableStateOf(false)
    private var silenciadas by mutableStateOf(false)
    private var permisoConcedido by mutableStateOf(false)

    private val solicitarPermiso = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permisoConcedido = concedido

        if (!concedido) {
            error = "Sin permiso no se mostrarán notificaciones"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencias = NotificationPreferences(this)
        notificador = NotificationJobs(this)
        repository = RepositoryJobs()

        silenciadas = preferencias.estanSilenciadas()
        actualizarEstadoPermiso()

        listener = repository.observar(
            onLista = {
                tareas = it
                cargando = false
                error = null
            },
            onNueva = {
                notificador.mostrarNuevaTarea(it)
            },
            onError = {
                error = it
                cargando = false
            }
        )

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PantallaTareas()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarEstadoPermiso()
    }

    private fun actualizarEstadoPermiso() {
        permisoConcedido =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    checkSelfPermission(
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
    }

    private fun pedirPermiso() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            solicitarPermiso.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    private fun cambiarSilencio() {
        silenciadas = !silenciadas
        preferencias.establecerSilenciadas(silenciadas)
    }

    private fun agregarTarea() {
        guardando = true
        error = null

        repository.agregar(
            titulo = titulo,
            onExito = {
                titulo = ""
                guardando = false
            },
            onError = {
                error = it
                guardando = false
            }
        )
    }

    @Composable
    private fun PantallaTareas() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tareas — E50-5",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = if (silenciadas) {
                    "Notificaciones silenciadas"
                } else {
                    "Avisos de tareas activados en la app"
                }
            )

            OutlinedButton(
                onClick = { cambiarSilencio() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (silenciadas) {
                        "Activar notificaciones"
                    } else {
                        "Silenciar notificaciones"
                    }
                )
            }

            if (!permisoConcedido) {
                Button(
                    onClick = { pedirPermiso() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Permitir notificaciones en Android")
                }
            }

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la tarea") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { agregarTarea() },
                enabled = titulo.isNotBlank() && !guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (guardando) "Guardando..." else "Agregar tarea"
                )
            }

            error?.let { mensaje ->
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (cargando) {
                CircularProgressIndicator()
            }

            Text("Total: ${tareas.size}")

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = tareas,
                    key = { it.id }
                ) { tarea ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = tarea.titulo,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = if (tarea.completada) {
                                    "Completada"
                                } else {
                                    "Pendiente"
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        listener?.remove()
        listener = null
        super.onDestroy()
    }
}