package ru.k.kbook.api.grpc.response

import ru.k.kbook.api.grpc.schema.DishFlag

data class ValidateDishResponseDto(
    val valid: Boolean,
    val errors: List<String>,
    val calculatedCaloricity: Double,
    val calculatedProtein: Double,
    val calculatedFat: Double,
    val calculatedCarb: Double,
    val availableFlags: List<DishFlag>,
)
