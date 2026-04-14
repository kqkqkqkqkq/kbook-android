package ru.k.kbook.api.grpc

import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDateTime
import ru.k.kbook.api.grpc.request.CreateProductRequestDto
import ru.k.kbook.api.grpc.request.DeleteProductRequestDto
import ru.k.kbook.api.grpc.request.GetProductRequestDto
import ru.k.kbook.api.grpc.request.GetProductsForDishRequestDto
import ru.k.kbook.api.grpc.request.ListProductsRequestDto
import ru.k.kbook.api.grpc.request.UpdateProductRequestDto
import ru.k.kbook.api.grpc.response.DeleteProductResponseDto
import ru.k.kbook.api.grpc.response.ListProductsResponseDto
import ru.k.kbook.api.grpc.response.ProductResponseDto
import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.Product
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag
import ru.k.kbook.api.grpc.schema.SortDirection
import ru.k.kbook.api.grpc.schema.SortField
import java.time.Instant
import java.util.Date
import java.util.UUID

class ProductApiMock : ProductApi {

    private val products = mutableListOf<Product>()

    init {
        (1..20).forEach { i ->
            products.add(
                Product(
                    id = i.toLong(),
                    name = "Продукт $i",
                    category = ProductCategory.VEGETABLES,
                    cookingRequired = CookingRequired.READY_TO_EAT,
                    flags = listOf(
                        ProductFlag.SUGAR_FREE,
                        ProductFlag.GLUTEN_FREE
                    ).takeIf { i % 3 == 0 } ?: emptyList(),
                    caloricity = 12.0,
                    protein = (0..20).random().toDouble(),
                    fat = (0..30).random().toDouble(),
                    carb = (5..60).random().toDouble(),
                    createdAt = Instant.now(),
                    updatedAt = Instant.now(),
                    images = emptyList(),
                    description = "Описание продукта",
                )
            )
        }
    }

    override suspend fun createProduct(product: CreateProductRequestDto): ProductResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun getProduct(id: GetProductRequestDto): ProductResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun updateProduct(product: UpdateProductRequestDto): ProductResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(id: DeleteProductRequestDto): DeleteProductResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun listProducts(params: ListProductsRequestDto): ListProductsResponseDto {
        var filtered = products.asSequence()

        if (!params.searchQuery.isNullOrBlank()) {
            filtered = filtered.filter { it.name.contains(params.searchQuery, ignoreCase = true) }
        }

        if (!params.categories.isNullOrEmpty()) {
            filtered = filtered.filter { it.category in params.categories }
        }

        if (!params.cookingRequired.isNullOrEmpty()) {
            filtered = filtered.filter { it.cookingRequired in params.cookingRequired }
        }

        if (!params.flags.isNullOrEmpty()) {
            filtered = filtered.filter { it.flags.any { flag -> flag in params.flags } }
        }

        val sorted = when (params.sortBy) {
            SortField.NAME -> filtered.sortedBy { it.name }
            SortField.CALORICITY -> filtered.sortedBy { it.caloricity }
            SortField.CARB -> filtered.sortedBy { it.carb}
            SortField.FAT -> filtered.sortedBy { it.fat }
            SortField.PROTEIN -> filtered
            null -> filtered
        }

        val directionSorted = if (params.sortDirection == SortDirection.ASC) sorted else sorted.sortedByDescending { it.createdAt }

        val result = directionSorted.toList()
        val totalCount = result.size.toLong()

        return ListProductsResponseDto(
            products = result,
            totalCount = totalCount,
            success = true,
            message = ""
        )
    }

    override suspend fun getProductsForDish(params: GetProductsForDishRequestDto): ListProductsResponseDto {
        TODO("Not yet implemented")
    }
}
