package ru.k.kbook.features.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.k.kbook.api.grpc.request.ListProductsRequestDto
import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.Product
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag
import ru.k.kbook.api.grpc.schema.SortDirection
import ru.k.kbook.api.grpc.schema.SortField
import ru.k.kbook.data.ProductRepositoryImpl
import ru.k.kbook.domain.product.ProductRepository

class ProductViewModel(
    private val productRepository: ProductRepository = ProductRepositoryImpl(),
) : ViewModel() {

    private val _state = MutableStateFlow(ProductScreenState())
    val state = _state.asStateFlow()

    init {
        getProducts()
    }

    private fun getProducts() {
        viewModelScope.launch {
            try {
                val req = ListProductsRequestDto(
                    _state.value.searchQuery,
                    _state.value.categories,
                    _state.value.cookingRequired,
                    _state.value.flags,
                    _state.value.sortBy,
                    _state.value.sortDirection,
                )
                _state.update { it.copy(error = null) }
                _state.update { it.copy(products = productRepository.listProducts(req).products) }
            } catch (e: Exception) {
                Log.e("ProductViewModel", e.message ?: "Unknown exception")
                _state.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun updateState(newState: ProductScreenState) {
        viewModelScope.launch {
            _state.emit(newState)
        }
    }
}

data class ProductScreenState(
    val searchQuery: String? = null,
    val categories: List<ProductCategory> = emptyList(),
    val cookingRequired: List<CookingRequired> = emptyList(),
    val flags: List<ProductFlag> = emptyList(),
    val sortBy: SortField = SortField.NAME,
    val sortDirection: SortDirection = SortDirection.DESC,
    val products: List<Product> = emptyList(),
    val error: String? = null,
)
