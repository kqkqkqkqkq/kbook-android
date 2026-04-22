package ru.k.kbook.api.grpc

import jakarta.inject.Inject
import ru.k.kbook.api.grpc.request.CreateDishRequestDto
import ru.k.kbook.api.grpc.request.DeleteDishRequestDto
import ru.k.kbook.api.grpc.request.GetDishRequestDto
import ru.k.kbook.api.grpc.request.ListDishesRequestDto
import ru.k.kbook.api.grpc.request.UpdateDishRequestDto
import ru.k.kbook.api.grpc.response.DeleteDishResponseDto
import ru.k.kbook.api.grpc.response.DishListResponseDto
import ru.k.kbook.api.grpc.response.DishResponseDto
import ru.k.kbook.api.grpc.response.ValidateDishResponseDto
import ru.k.kbook.config.GrpcChannel
import ru.k.kbook.data.dish.mapper.toGrpc
import ru.k.kbook.data.dish.mapper.toKotlin
import ru.k.kbook.data.dish.mapper.toResponse
import ru.k.kbook_api.grpc.dish.DishServiceGrpcKt
import javax.inject.Singleton
import kotlin.getValue

@Singleton
class DishApiImpl @Inject constructor (
    private val channel: GrpcChannel,
) : DishApi {
    private val stub by lazy { DishServiceGrpcKt.DishServiceCoroutineStub(channel.channel) }

    override suspend fun createDish(request: CreateDishRequestDto): DishResponseDto =
        stub.createDish(request.toGrpc()).toResponse()

    override suspend fun getDish(request: GetDishRequestDto): DishResponseDto =
        stub.getDish(request.toGrpc()).toResponse()

    override suspend fun updateDish(request: UpdateDishRequestDto): DishResponseDto =
        stub.updateDish(request.toGrpc()).toResponse()

    override suspend fun deleteDish(request: DeleteDishRequestDto): DeleteDishResponseDto =
        stub.deleteDish(request.toGrpc()).toKotlin()

    override suspend fun listDishes(request: ListDishesRequestDto): DishListResponseDto =
        stub.listDishes(request.toGrpc()).toKotlin()

    override suspend fun validateDish(request: CreateDishRequestDto): ValidateDishResponseDto =
        stub.validateDish(request.toGrpc()).toKotlin()
}
