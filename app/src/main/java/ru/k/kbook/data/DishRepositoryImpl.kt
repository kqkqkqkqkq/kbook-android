package ru.k.kbook.data

import ru.k.kbook.api.grpc.DishApi
import ru.k.kbook.api.grpc.DishApiImpl
import ru.k.kbook.api.grpc.request.CreateDishRequestDto
import ru.k.kbook.api.grpc.request.DeleteDishRequestDto
import ru.k.kbook.api.grpc.request.GetDishRequestDto
import ru.k.kbook.api.grpc.request.ListDishesRequestDto
import ru.k.kbook.api.grpc.request.UpdateDishRequestDto
import ru.k.kbook.api.grpc.response.DeleteDishResponseDto
import ru.k.kbook.api.grpc.response.DishListResponseDto
import ru.k.kbook.api.grpc.response.DishResponseDto
import ru.k.kbook.api.grpc.response.ValidateDishResponseDto
import ru.k.kbook.domain.dish.DishRepository

class DishRepositoryImpl(
    private val api: DishApi = DishApiImpl(),
) : DishRepository {
    override suspend fun createDish(request: CreateDishRequestDto): DishResponseDto = api.createDish(request)
    override suspend fun getDish(request: GetDishRequestDto): DishResponseDto = api.getDish(request)
    override suspend fun updateDish(request: UpdateDishRequestDto): DishResponseDto = api.updateDish(request)
    override suspend fun deleteDish(request: DeleteDishRequestDto): DeleteDishResponseDto = api.deleteDish(request)
    override suspend fun listDishes(request: ListDishesRequestDto): DishListResponseDto = api.listDishes(request)
    override suspend fun validateDish(request: CreateDishRequestDto): ValidateDishResponseDto = api.validateDish(request)
}
