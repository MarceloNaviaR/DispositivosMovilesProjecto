package edu.ucb.project

import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.lifecycle.lifecycleScope

import com.develoop.logs.LogApi
import com.google.protobuf.ByteString

import edu.ucb.project.feature.logs.data.datasource.LogsRemoteDataSource
import edu.ucb.project.scheduler.LogScheduler

import edu.ucb.project.feature.tareas.ActivityJobs
import edu.ucb.project.feature.configremota.ConfigDashboardActivity

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        LogScheduler(applicationContext)
            .schedulePeriodicalUpload()

        probarEnvioGrpc()

        setContent {
            MaterialTheme {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // MENÚ PRINCIPAL
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {

                        // BOTÓN TAREAS
                        Button(
                            onClick = {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        ActivityJobs::class.java
                                    )
                                )
                            }
                        ) {
                            Text("Abrir tareas — E50-5")
                        }

                        // BOTÓN DASHBOARD
                        Button(
                            onClick = {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        ConfigDashboardActivity::class.java
                                    )
                                )
                            }
                        ) {
                            Text("Abrir Dashboard — E100-2")
                        }

                        // BOTÓN CLIMA ANIMADO
                        Button(
                            onClick = {
                                Log.d(
                                    "WEATHER_NAV",
                                    "Abriendo WeatherActivity"
                                )

                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        WeatherActivity::class.java
                                    )
                                )
                            }
                        ) {
                            Text("🌤 Ver clima animado")
                        }
                    }

                    // LOGIN Y NAVEGACIÓN ORIGINAL
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        App()
                    }
                }
            }
        }
    }

    // FUNCIÓN ORIGINAL DE ENVÍO GRPC
    private fun probarEnvioGrpc() {

        lifecycleScope.launch {

            var dataSource: LogsRemoteDataSource? = null

            try {
                val client = LogsRemoteDataSource(
                    host = "10.0.2.2",
                    port = 9090
                )

                dataSource = client

                val now = System.currentTimeMillis()

                val logData = LogApi.LogData.newBuilder()
                    .setAndroidId(
                        ByteString.copyFromUtf8(
                            "android_prueba"
                        )
                    )
                    .setAppInstanceId(
                        ByteString.copyFromUtf8(
                            "instance_001"
                        )
                    )
                    .setLogLevel(
                        LogApi.ELogLevel.LEVEL_ERROR
                    )
                    .setMessage(
                        "Prueba desde la aplicación Android"
                    )
                    .setStackTrace(
                        "Traza de prueba"
                    )
                    .setServerTimeStamp(now)
                    .setMobileTimeStamp(now)
                    .setVersionCode(1)
                    .setUserId("user_42")
                    .build()

                val request = LogApi.LogRequest.newBuilder()
                    .addLogs(logData)
                    .build()

                val response = client.send(request)

                Log.i(
                    "GRPC_LOGS",
                    "Respuesta del servidor: ${response.resultCode}"
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                Log.e(
                    "GRPC_LOGS",
                    "Error al enviar el log",
                    e
                )

            } finally {
                dataSource?.shutdown()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
