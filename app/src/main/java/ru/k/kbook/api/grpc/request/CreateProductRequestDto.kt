package ru.k.kbook.api.grpc.request

import ru.k.kbook.api.grpc.schema.ImageInput
import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag

data class CreateProductRequestDto(
    val name: String,
    val images: List<ImageInput>,
    val caloricity: Double,
    val protein: Double,
    val fat: Double,
    val carb: Double,
    val description: String?,
    val category: ProductCategory,
    val cookingRequired: CookingRequired,
    val flags: List<ProductFlag>
)
