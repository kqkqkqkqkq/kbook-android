package ru.k.kbook.api.grpc.schema

data class DishProduct(
    val productId: Long,
    val productName: String,
    val quantity: Double,
)
