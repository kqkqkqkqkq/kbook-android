package ru.k.kbook.api.grpc.request

import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag
import ru.k.kbook.api.grpc.schema.SortDirection
import ru.k.kbook.api.grpc.schema.SortField

data class ListProductsRequestDto(
    val searchQuery: String? = null,
    val categories: List<ProductCategory>? = null,
    val cookingRequired: List<CookingRequired>? = null,
    val flags: List<ProductFlag>? = null,
    val sortBy: SortField? = null,
    val sortDirection: SortDirection? = null,
    val limit: Int? = null,
    val offset: Int? = null,
)
