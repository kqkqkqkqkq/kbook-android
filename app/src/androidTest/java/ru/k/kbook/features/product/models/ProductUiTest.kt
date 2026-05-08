package ru.k.kbook.features.product.models

import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.Product
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag
import java.util.Collections.emptyList

data class ProductUiTest(
    val name: String = "Test",
    val caloricity: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carb: Double = 0.0,
    val category: ProductCategory = ProductCategory.MEAT,
    val cookingRequired: CookingRequired = CookingRequired.REQUIRES_COOKING,
    val flags: List<ProductFlag> = emptyList(),
    val images: List<String> = emptyList(),
    val description: String? = null,
)
