package ru.k.kbook.api.grpc.request

data class GetProductsForDishRequestDto(
    val productIds: List<Long>,
)
