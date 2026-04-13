package ru.k.kbook.config

import io.grpc.ManagedChannel
import io.grpc.okhttp.OkHttpChannelBuilder

class GrpcChannel {
    val channel: ManagedChannel = OkHttpChannelBuilder
        .forAddress(WebConfig.GRPC_HOST, WebConfig.GRPC_BASE_PORT)
        .usePlaintext()
        .build()
}
