package edu.ucb.project.feature.logs.data.datasource

import com.develoop.logs.LogApi
import com.develoop.logs.LogServiceGrpcKt
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import java.util.concurrent.TimeUnit

class LogsRemoteDataSource(
    host: String,
    port: Int,
    plaintext: Boolean = true
) {
    private val channel: ManagedChannel =
        ManagedChannelBuilder
            .forAddress(host, port)
            .apply {
                if (plaintext) {
                    usePlaintext()
                } else {
                    useTransportSecurity()
                }
            }
            .build()

    private val stub =
        LogServiceGrpcKt.LogServiceCoroutineStub(channel)

    suspend fun send(
        request: LogApi.LogRequest
    ): LogApi.LogResponse {
        return stub
            .withDeadlineAfter(10, TimeUnit.SECONDS)
            .send(request)
    }

    fun shutdown() {
        channel.shutdownNow()
    }
}