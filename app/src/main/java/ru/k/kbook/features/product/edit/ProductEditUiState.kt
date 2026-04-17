package ru.k.kbook.features.product.edit

import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag
import ru.k.kbook.api.grpc.schema.ProductImage

// TODO("fix images uploading")
data class ProductEditUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val name: String = "",
    val images: List<ProductImage> = emptyList(),
    val caloricity: String = "",
    val protein: String = "",
    val fat: String = "",
    val carb: String = "",
    val description: String = "",
    val category: ProductCategory = ProductCategory.MEAT,
    val cookingRequired: CookingRequired = CookingRequired.REQUIRES_COOKING,
    val flags: List<ProductFlag> = emptyList(),
    val error: String? = null,
)
