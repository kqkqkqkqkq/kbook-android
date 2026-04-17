package ru.k.kbook.navigation

import kotlinx.serialization.Serializable
import ru.k.kbook.api.grpc.schema.Product

@Serializable
sealed class Screen {
    @Serializable data object Products : Screen()
    @Serializable data object ProductCreate : Screen()
    @Serializable data class ProductEdit(val productId: Long) : Screen()
    @Serializable data class ProductDetails(val productId: Long) : Screen()
    @Serializable data object Dishes : Screen()
    @Serializable data object DishCreate : Screen()
    @Serializable data class DishDetail(val dishId: Long) : Screen()
}
