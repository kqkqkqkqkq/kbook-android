package ru.k.kbook.features.dish

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
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

@OptIn(ExperimentalCoroutinesApi::class)
class DishViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search change loads data`() = runTest {
        val vm = DishViewModel(repo = FakeDishRepo())
        vm.onSearch("salad")
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.uiState.value is DishListUiState.Data)
    }
}

private class FakeDishRepo : DishRepository {
    override suspend fun createDish(request: CreateDishRequestDto): DishResponseDto = DishResponseDto(null)
    override suspend fun getDish(request: GetDishRequestDto): DishResponseDto = DishResponseDto(null)
    override suspend fun updateDish(request: UpdateDishRequestDto): DishResponseDto = DishResponseDto(null)
    override suspend fun deleteDish(request: DeleteDishRequestDto): DeleteDishResponseDto = DeleteDishResponseDto(true)
    override suspend fun listDishes(request: ListDishesRequestDto): DishListResponseDto = DishListResponseDto(emptyList(), 0)
    override suspend fun validateDish(request: CreateDishRequestDto): ValidateDishResponseDto = ValidateDishResponseDto(true, emptyList(), 0.0, 0.0, 0.0, 0.0, emptyList())
}
