package ru.k.kbook.api.grpc.schema

data class DishImage(
    val id: Long,
    val url: String?,
    val image: ByteArray?,
    val contentType: String,
)
