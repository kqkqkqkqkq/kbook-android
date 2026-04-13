package ru.k.kbook.api.grpc.request

import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag
import ru.k.kbook.api.grpc.schema.SortDirection
import ru.k.kbook.api.grpc.schema.SortField

data class ListProductsRequestDto(
    val searchQuery: String?,
    val categories: List<ProductCategory>?,
    val cookingRequired: List<CookingRequired>?,
    val flags: List<ProductFlag>?,
    val sortBy: SortField?,
    val sortDirection: SortDirection?,
    val limit: Int? = null,
    val offset: Int? = null,
)
