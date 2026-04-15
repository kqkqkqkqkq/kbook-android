package ru.k.kbook.features.product

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

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {
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
    fun `search change triggers data state`() = runTest {
        val vm = ProductViewModel(repo = FakeProductRepo())
        vm.onSearchChange("milk")
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.uiState.value is ProductListUiState.Data)
    }
}

private class FakeProductRepo : ProductRepository {
    override suspend fun createProduct(product: CreateProductRequestDto): ProductResponseDto = ProductResponseDto(null, true, "")
    override suspend fun getProduct(id: GetProductRequestDto): ProductResponseDto = ProductResponseDto(null, true, "")
    override suspend fun updateProduct(product: UpdateProductRequestDto): ProductResponseDto = ProductResponseDto(null, true, "")
    override suspend fun deleteProduct(id: DeleteProductRequestDto): DeleteProductResponseDto = DeleteProductResponseDto(true, "", emptyList())
    override suspend fun listProducts(params: ListProductsRequestDto): ListProductsResponseDto = ListProductsResponseDto(emptyList(), 0, true, "")
    override suspend fun getProductsForDish(params: GetProductsForDishRequestDto): ListProductsResponseDto = ListProductsResponseDto(emptyList(), 0, true, "")
}
