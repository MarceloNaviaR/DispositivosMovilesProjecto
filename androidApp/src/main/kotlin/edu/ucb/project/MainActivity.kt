package edu.ucb.project
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.develoop.logs.LogApi
import com.google.protobuf.ByteString
import edu.ucb.project.feature.logs.data.datasource.LogsRemoteDataSource
import edu.ucb.project.scheduler.LogScheduler
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import edu.ucb.project.feature.tareas.ActivityJobs

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
                    Button(
                        onClick = {
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    ActivityJobs::class.java
                                )
                            )
                        },
                        modifier = Modifier.padding(
                            top = 32.dp,
                            bottom = 24.dp
                        )
                    ) {
                        Text("Abrir tareas — E50-5")
                    }

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
                        ByteString.copyFromUtf8("android_prueba")
                    )
                    .setAppInstanceId(
                        ByteString.copyFromUtf8("instance_001")
                    )
                    .setLogLevel(
                        LogApi.ELogLevel.LEVEL_ERROR
                    )
                    .setMessage(
                        "Prueba desde la aplicación Android"
                    )
                    .setStackTrace("Traza de prueba")
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