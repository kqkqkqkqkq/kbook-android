package ru.k.kbook.api.grpc.schema

import java.time.Instant

data class Dish(
    val id: Long,
    val name: String,
    val images: List<DishImage>,
    val caloricity: Double,
    val protein: Double,
    val fat: Double,
    val carb: Double,
    val composition: List<DishProduct>,
    val portionSize: Double,
    val category: DishCategory,
    val flags: List<DishFlag>,
    val createdAt: Instant,
    val updatedAt: Instant,
)
