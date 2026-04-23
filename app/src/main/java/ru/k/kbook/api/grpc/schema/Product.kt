package ru.k.kbook.api.grpc.schema

import java.time.Instant

data class Product(
    val id: Long,
    val name: String,
    val images: List<ProductImage>,
    val caloricity: Double,
    val protein: Double,
    val fat: Double,
    val carb: Double,
    val description: String?,
    val category: ProductCategory,
    val cookingRequired: CookingRequired,
    val flags: List<ProductFlag>,
    val createdAt: Instant,
    val updatedAt: Instant,
)