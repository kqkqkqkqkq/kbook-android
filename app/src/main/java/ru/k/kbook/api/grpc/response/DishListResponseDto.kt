package ru.k.kbook.api.grpc.response

import ru.k.kbook.api.grpc.schema.Dish

data class DishListResponseDto(
    val dishes: List<Dish>,
    val total: Int,
)
