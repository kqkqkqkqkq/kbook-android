package ru.k.kbook.api.grpc.request

import ru.k.kbook.api.grpc.schema.DishCategory
import ru.k.kbook.api.grpc.schema.DishFlag

data class ListDishesRequestDto(
    val searchQuery: String?,
    val categories: List<DishCategory>?,
    val flags: List<DishFlag>?,
    val limit: Int? = null,
    val offset: Int? = null,
)
