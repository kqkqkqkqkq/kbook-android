package ru.k.kbook.data

import android.util.Log
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
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
    override suspend fun createDish(request: CreateDishRequestDto): DishResponseDto {
        Log.i("DishRepository", "createDish called with request: $request")
        return try {
            val response = api.createDish(request)
            Log.i("DishRepository", "createDish success: $response")
            response
        } catch (e: Exception) {
            Log.e("DishRepository", "createDish failed", e)
            throw e
        }
    }

    override suspend fun getDish(request: GetDishRequestDto): DishResponseDto {
        Log.i("DishRepository", "getDish called with request: $request")
        return try {
            val response = api.getDish(request)
            Log.i("DishRepository", "getDish success: $response")
            response
        } catch (e: Exception) {
            Log.e("DishRepository", "getDish failed", e)
            throw e
        }
    }

    override suspend fun updateDish(request: UpdateDishRequestDto): DishResponseDto {
        Log.i("DishRepository", "updateDish called with request: $request")
        return try {
            val response = api.updateDish(request)
            Log.i("DishRepository", "updateDish success: $response")
            response
        } catch (e: Exception) {
            Log.e("DishRepository", "updateDish failed", e)
            throw e
        }
    }

    override suspend fun deleteDish(request: DeleteDishRequestDto): DeleteDishResponseDto {
        Log.i("DishRepository", "deleteDish called with request: $request")
        return try {
            val response = api.deleteDish(request)
            Log.i("DishRepository", "deleteDish success: $response")
            response
        } catch (e: Exception) {
            Log.e("DishRepository", "deleteDish failed", e)
            throw e
        }
    }

    override suspend fun listDishes(request: ListDishesRequestDto): DishListResponseDto {
        Log.i("DishRepository", "listDishes called with request: $request")
        return try {
            val response = api.listDishes(request)
            Log.i("DishRepository", "listDishes success: $response")
            response
        } catch (e: Exception) {
            Log.e("DishRepository", "listDishes failed", e)
            throw e
        }
    }

    override suspend fun validateDish(request: CreateDishRequestDto): ValidateDishResponseDto {
        Log.i("DishRepository", "validateDish called with request: $request")
        return try {
            val response = api.validateDish(request)
            Log.i("DishRepository", "validateDish success: $response")
            response
        } catch (e: Exception) {
            Log.e("DishRepository", "validateDish failed", e)
            throw e
        }
    }
}
