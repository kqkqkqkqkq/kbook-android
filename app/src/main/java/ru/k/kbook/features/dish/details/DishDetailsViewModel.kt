package ru.k.kbook.features.dish.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.k.kbook.api.grpc.request.GetDishRequestDto
import ru.k.kbook.api.grpc.request.GetProductRequestDto
import ru.k.kbook.api.grpc.schema.Dish
import ru.k.kbook.data.DishRepositoryImpl
import ru.k.kbook.data.ProductRepositoryImpl
import ru.k.kbook.domain.dish.DishRepository
import ru.k.kbook.domain.product.ProductRepository
import ru.k.kbook.features.dish.components.ProductWithQuantity
import javax.inject.Inject
import kotlin.code

sealed class DishDetailsUiState {
    data object Loading : DishDetailsUiState()
    data class Error(val message: String) : DishDetailsUiState()
    data class Data(val dish: Dish,val products: List<ProductWithQuantity>) : DishDetailsUiState()
}

@HiltViewModel
class DishDetailsViewModel @Inject constructor (
    private val dishRepo: DishRepository,
    private val productRepo: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<DishDetailsUiState>(DishDetailsUiState.Loading)
    val uiState: StateFlow<DishDetailsUiState> = _uiState.asStateFlow()

    fun load(dishId: Long) {
        if (dishId <= 0L) {
            _uiState.value = DishDetailsUiState.Error("Некорректный id блюда: $dishId")
            return
        }
        viewModelScope.launch {
            runCatching { dishRepo.getDish(GetDishRequestDto(dishId)).dish }
                .onSuccess { dish ->
                    if (dish == null) {
                        _uiState.value = DishDetailsUiState.Error("Блюдо не найдено")
                        return@onSuccess
                    }

                    val productWithQuantityList = mutableListOf<ProductWithQuantity>()
                    for (comp in dish.composition) {
                        try {
                            val productResponse = productRepo.getProduct(GetProductRequestDto(comp.productId))
                            if (productResponse.product != null) {
                                productWithQuantityList.add(
                                    ProductWithQuantity(productResponse.product, comp.quantity)
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("DishDetailsVM", "Failed to load product ${comp.productId}", e)
                        }
                    }

                    _uiState.value = DishDetailsUiState.Data(dish, productWithQuantityList)
                }
                .onFailure {
                    val message = when (it) {
                        is StatusException -> "Ошибка сервера (${it.status.code}): ${it.status.description ?: "без описания"}"
                        is StatusRuntimeException -> "Ошибка сервера (${it.status.code}): ${it.status.description ?: "без описания"}"
                        else -> it.message ?: "Ошибка загрузки"
                    }
                    _uiState.value = DishDetailsUiState.Error(message)
                }
        }
    }
}
