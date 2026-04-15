package ru.k.kbook.domain.dish

import ru.k.kbook.api.grpc.request.CreateDishRequestDto
import ru.k.kbook.api.grpc.request.DeleteDishRequestDto
import ru.k.kbook.api.grpc.request.GetDishRequestDto
import ru.k.kbook.api.grpc.request.ListDishesRequestDto
import ru.k.kbook.api.grpc.request.UpdateDishRequestDto
import ru.k.kbook.api.grpc.response.DeleteDishResponseDto
import ru.k.kbook.api.grpc.response.DishListResponseDto
import ru.k.kbook.api.grpc.response.DishResponseDto
import ru.k.kbook.api.grpc.response.ValidateDishResponseDto

interface DishRepository {
    suspend fun createDish(request: CreateDishRequestDto): DishResponseDto
    suspend fun getDish(request: GetDishRequestDto): DishResponseDto
    suspend fun updateDish(request: UpdateDishRequestDto): DishResponseDto
    suspend fun deleteDish(request: DeleteDishRequestDto): DeleteDishResponseDto
    suspend fun listDishes(request: ListDishesRequestDto): DishListResponseDto
    suspend fun validateDish(request: CreateDishRequestDto): ValidateDishResponseDto
}
