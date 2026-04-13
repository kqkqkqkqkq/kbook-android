package ru.k.kbook.data

import ru.k.kbook.api.grpc.ProductApi
import ru.k.kbook.api.grpc.ProductApiImpl
import ru.k.kbook.api.grpc.request.CreateProductRequestDto
import ru.k.kbook.api.grpc.request.DeleteProductRequestDto
import ru.k.kbook.api.grpc.request.GetProductRequestDto
import ru.k.kbook.api.grpc.request.GetProductsForDishRequestDto
import ru.k.kbook.api.grpc.request.ListProductsRequestDto
import ru.k.kbook.api.grpc.request.UpdateProductRequestDto
import ru.k.kbook.api.grpc.response.DeleteProductResponseDto
import ru.k.kbook.api.grpc.response.ListProductsResponseDto
import ru.k.kbook.api.grpc.response.ProductResponseDto
import ru.k.kbook.domain.product.ProductRepository

class ProductRepositoryImpl(
    private val productApi: ProductApi = ProductApiImpl()
) : ProductRepository {
    override suspend fun createProduct(product: CreateProductRequestDto): ProductResponseDto {
        return productApi.createProduct(product)
    }

    override suspend fun getProduct(id: GetProductRequestDto): ProductResponseDto {
        return productApi.getProduct(id)
    }

    override suspend fun updateProduct(product: UpdateProductRequestDto): ProductResponseDto {
        return productApi.updateProduct(product)
    }

    override suspend fun deleteProduct(id: DeleteProductRequestDto): DeleteProductResponseDto {
        return productApi.deleteProduct(id)
    }

    override suspend fun listProducts(params: ListProductsRequestDto): ListProductsResponseDto {
        return productApi.listProducts(params)
    }

    override suspend fun getProductsForDish(params: GetProductsForDishRequestDto): ListProductsResponseDto {
        return productApi.getProductsForDish(params)
    }
}
