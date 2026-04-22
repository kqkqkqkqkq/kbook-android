package ru.k.kbook.config

import io.grpc.ManagedChannel
import io.grpc.okhttp.OkHttpChannelBuilder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrpcChannel @Inject constructor() {
    val channel: ManagedChannel = OkHttpChannelBuilder
        .forAddress(WebConfig.GRPC_HOST, WebConfig.GRPC_BASE_PORT)
        .usePlaintext()
        .build()
}
