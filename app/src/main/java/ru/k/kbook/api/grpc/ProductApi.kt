package ru.k.kbook.api.grpc

import ru.k.kbook.api.grpc.request.CreateProductRequestDto
import ru.k.kbook.api.grpc.request.DeleteProductRequestDto
import ru.k.kbook.api.grpc.request.GetProductRequestDto
import ru.k.kbook.api.grpc.request.GetProductsForDishRequestDto
import ru.k.kbook.api.grpc.request.ListProductsRequestDto
import ru.k.kbook.api.grpc.request.UpdateProductRequestDto
import ru.k.kbook.api.grpc.response.DeleteProductResponseDto
import ru.k.kbook.api.grpc.response.ListProductsResponseDto
import ru.k.kbook.api.grpc.response.ProductResponseDto

interface ProductApi {
    suspend fun createProduct(product: CreateProductRequestDto): ProductResponseDto
    suspend fun getProduct(id: GetProductRequestDto): ProductResponseDto
    suspend fun updateProduct(product: UpdateProductRequestDto): ProductResponseDto
    suspend fun deleteProduct(id: DeleteProductRequestDto): DeleteProductResponseDto
    suspend fun listProducts(params: ListProductsRequestDto): ListProductsResponseDto
    suspend fun getProductsForDish(params: GetProductsForDishRequestDto): ListProductsResponseDto
}
