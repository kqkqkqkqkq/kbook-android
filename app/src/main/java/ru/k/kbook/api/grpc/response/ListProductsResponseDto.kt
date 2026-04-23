package ru.k.kbook.api.grpc.response

import ru.k.kbook.api.grpc.schema.Product

data class ListProductsResponseDto(
    val products: List<Product>,
    val totalCount: Long,
    val success: Boolean,
    val message: String,
)