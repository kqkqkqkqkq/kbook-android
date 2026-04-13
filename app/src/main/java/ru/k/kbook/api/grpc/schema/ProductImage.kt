package ru.k.kbook.api.grpc.schema

data class ProductImage(
    val id: Long,
    val url: String?,
    val image: ByteArray?,
    val contentType: ContentType
)