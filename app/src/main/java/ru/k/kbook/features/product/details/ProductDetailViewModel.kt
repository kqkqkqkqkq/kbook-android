package ru.k.kbook.features.product.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.k.kbook.api.grpc.request.GetProductRequestDto
import ru.k.kbook.api.grpc.schema.Product
import ru.k.kbook.data.ProductRepositoryImpl
import ru.k.kbook.domain.product.ProductRepository

sealed class ProductDetailUiState {
    data object Loading : ProductDetailUiState()
    data class Data(val product: Product) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}

class ProductDetailViewModel(
    private val id: Long,
    private val repo: ProductRepository = ProductRepositoryImpl(),
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()
    fun load() {
        viewModelScope.launch {
            runCatching { repo.getProduct(GetProductRequestDto(id)) }
                .onSuccess {
                    val product = it.product
                    if (product == null) _uiState.value = ProductDetailUiState.Error("Product not found")
                    else _uiState.value = ProductDetailUiState.Data(product)
                }
                .onFailure { _uiState.value = ProductDetailUiState.Error(it.message ?: "Load failed") }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class ProductDetailVmFactory(
    private val id: Long,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ProductDetailViewModel(id) as T
}
