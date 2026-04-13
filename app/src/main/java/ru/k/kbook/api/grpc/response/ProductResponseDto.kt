package ru.k.kbook.api.grpc.response

import ru.k.kbook.api.grpc.schema.Product

data class ProductResponseDto(
    val product: Product?,
    val success: Boolean,
    val message: String
)