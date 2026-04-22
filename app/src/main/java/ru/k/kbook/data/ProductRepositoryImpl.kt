package ru.k.kbook.data

import android.util.Log
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor (
    private val productApi: ProductApi,
) : ProductRepository {
    override suspend fun createProduct(product: CreateProductRequestDto): ProductResponseDto {
        Log.i("[Repository create]", "$product")
        val newProduct = productApi.createProduct(product)
        Log.i("[Repository created]", "$newProduct")
        return newProduct
    }

    override suspend fun getProduct(id: GetProductRequestDto): ProductResponseDto {
        val product = productApi.getProduct(id)
        Log.i("[Repository get]", "$product")
        return product
    }

    override suspend fun updateProduct(product: UpdateProductRequestDto): ProductResponseDto {
        Log.i("[Repository update]", "$product")
        return productApi.updateProduct(product)
    }

    override suspend fun deleteProduct(id: DeleteProductRequestDto): DeleteProductResponseDto {
        Log.i("[Repository delete]", "$id")
        return productApi.deleteProduct(id)
    }

    override suspend fun listProducts(params: ListProductsRequestDto): ListProductsResponseDto {
        Log.i("[Repository getAll]", "$params")
        val products = productApi.listProducts(params)
        Log.i("[Repository getAll products]", "${products.products.size}")
        return products
    }

    override suspend fun getProductsForDish(params: GetProductsForDishRequestDto): ListProductsResponseDto {
        return productApi.getProductsForDish(params)
    }
}
