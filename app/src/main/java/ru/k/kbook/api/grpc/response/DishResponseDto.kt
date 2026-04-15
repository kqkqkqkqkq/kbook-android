package ru.k.kbook.api.grpc.response

import ru.k.kbook.api.grpc.schema.Dish

data class DishResponseDto(
    val dish: Dish?,
)
