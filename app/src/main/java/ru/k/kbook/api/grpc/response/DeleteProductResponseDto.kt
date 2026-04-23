package ru.k.kbook.api.grpc.response

data class DeleteProductResponseDto(
    val success: Boolean,
    val message: String,
    val usedInDishes: List<String>,
)