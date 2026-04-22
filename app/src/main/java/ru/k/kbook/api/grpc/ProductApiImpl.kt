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
import ru.k.kbook.config.GrpcChannel
import ru.k.kbook.data.product.mapper.toGrpc
import ru.k.kbook.data.product.mapper.toKotlin
import ru.k.kbook_api.grpc.product.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductApiImpl @Inject constructor (
    private val channel: GrpcChannel,
) : ProductApi {
    private val stub by lazy {
        ProductServiceGrpcKt.ProductServiceCoroutineStub(channel.channel)
    }

    override suspend fun createProduct(product: CreateProductRequestDto): ProductResponseDto {
        val request = product.toGrpc()
        val response = stub.createProduct(request)
        return response.toKotlin()
    }

    override suspend fun getProduct(id: GetProductRequestDto): ProductResponseDto {
        val request = id.toGrpc()
        val response = stub.getProduct(request)
        return response.toKotlin()
    }

    override suspend fun updateProduct(product: UpdateProductRequestDto): ProductResponseDto {
        val request = product.toGrpc()
        val response = stub.updateProduct(request)
        return response.toKotlin()
    }

    override suspend fun deleteProduct(id: DeleteProductRequestDto): DeleteProductResponseDto {
        val request = id.toGrpc()
        val response = stub.deleteProduct(request)
        return response.toKotlin()
    }

    override suspend fun listProducts(params: ListProductsRequestDto): ListProductsResponseDto {
        val request = params.toGrpc()
        val response = stub.listProducts(request)
        return response.toKotlin()
    }

    override suspend fun getProductsForDish(params: GetProductsForDishRequestDto): ListProductsResponseDto {
        val request = params.toGrpc()
        val response = stub.getProductsForDish(request)
        return response.toKotlin()
    }
}
