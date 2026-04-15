package ru.k.kbook.api.grpc.request

import ru.k.kbook.api.grpc.schema.DishCategory
import ru.k.kbook.api.grpc.schema.DishFlag
import ru.k.kbook.api.grpc.schema.DishImage
import ru.k.kbook.api.grpc.schema.DishProduct

data class CreateDishRequestDto(
    val name: String,
    val images: List<DishImage>,
    val composition: List<DishProduct>,
    val portionSize: Double,
    val category: DishCategory?,
    val flags: List<DishFlag>,
    val caloricity: Double?,
    val protein: Double?,
    val fat: Double?,
    val carb: Double?,
)
