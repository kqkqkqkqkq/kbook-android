package ru.k.kbook.features.product.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.k.kbook.api.grpc.request.GetProductRequestDto
import ru.k.kbook.api.grpc.schema.Product
import ru.k.kbook.domain.product.ProductRepository
import javax.inject.Inject

sealed class ProductDetailUiState {
    data object Loading : ProductDetailUiState()
    data class Data(val product: Product) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repo: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()
    fun load(id: Long) {
        viewModelScope.launch {
            runCatching { repo.getProduct(GetProductRequestDto(id)) }
                .onSuccess {
                    val product = it.product
                    if (product == null) _uiState.value =
                        ProductDetailUiState.Error("Product not found")
                    else _uiState.value = ProductDetailUiState.Data(product)
                }
                .onFailure {
                    _uiState.value = ProductDetailUiState.Error(it.message ?: "Load failed")
                }
        }
    }
}
