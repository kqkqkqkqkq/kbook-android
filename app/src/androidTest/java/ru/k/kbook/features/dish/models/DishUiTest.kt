package ru.k.kbook.features.dish.models

import ru.k.kbook.api.grpc.schema.DishCategory
import ru.k.kbook.api.grpc.schema.DishFlag
import ru.k.kbook.api.grpc.schema.DishImage
import ru.k.kbook.api.grpc.schema.DishProduct
import java.util.Collections.emptyList

data class DishUiTest(
    val name: String = "Test dish",
    val images: List<String> = emptyList(),
    val caloricity: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carb: Double = 0.0,
    val composition: List<List<Any>> = emptyList(),
    val portionSize: Double = 0.0,
    val category: DishCategory = DishCategory.FIRST,
    val flags: List<DishFlag> = emptyList(),
)
