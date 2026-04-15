package ru.k.kbook.features.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.k.kbook.api.grpc.request.DeleteProductRequestDto
import ru.k.kbook.api.grpc.request.ListProductsRequestDto
import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.Product
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag
import ru.k.kbook.api.grpc.schema.SortDirection
import ru.k.kbook.api.grpc.schema.SortField
import ru.k.kbook.data.ProductRepositoryImpl
import ru.k.kbook.domain.product.ProductRepository

sealed class ProductListUiState {
    data object Loading : ProductListUiState()
    data class Data(val value: ProductListState) : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
}

sealed class ProductListEvent {
    data class Info(val message: String) : ProductListEvent()
    data class Error(val message: String) : ProductListEvent()
}

data class ProductListState(
    val searchQuery: String = "",
    val categories: List<ProductCategory> = emptyList(),
    val cookingRequired: List<CookingRequired> = emptyList(),
    val flags: List<ProductFlag> = emptyList(),
    val sortBy: SortField = SortField.NAME,
    val sortDirection: SortDirection = SortDirection.ASC,
    val products: List<Product> = emptyList(),
    val refreshing: Boolean = false,
)

class ProductViewModel(
    private val repo: ProductRepository = ProductRepositoryImpl(),
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProductListEvent>()
    val events: SharedFlow<ProductListEvent> = _events.asSharedFlow()

    private var state = ProductListState()
    private var isInitialLoad = true

    init {
        load()
    }

    fun onSearchChange(value: String) {
        state = state.copy(searchQuery = value)
        load()
    }

    fun onCategoryToggle(value: ProductCategory) {
        val selected = if (value in state.categories) state.categories - value else state.categories + value
        state = state.copy(categories = selected)
        load()
    }

    fun onCookingToggle(value: CookingRequired) {
        val selected = if (value in state.cookingRequired) state.cookingRequired - value else state.cookingRequired + value
        state = state.copy(cookingRequired = selected)
        load()
    }

    fun onFlagToggle(value: ProductFlag) {
        val selected = if (value in state.flags) state.flags - value else state.flags + value
        state = state.copy(flags = selected)
        load()
    }

    fun onSort(value: SortField, direction: SortDirection) {
        state = state.copy(sortBy = value, sortDirection = direction)
        load()
    }

    fun refresh() {
        state = state.copy(refreshing = true)
        load()
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            runCatching { repo.deleteProduct(DeleteProductRequestDto(id)) }
                .onSuccess {
                    if (!it.success && it.usedInDishes.isNotEmpty()) {
                        _events.emit(ProductListEvent.Info("Used in dishes: ${it.usedInDishes.joinToString()}"))
                    } else {
                        load()
                    }
                }
                .onFailure { _events.emit(ProductListEvent.Error(it.message ?: "Delete failed")) }
        }
    }

    private fun load() {
        viewModelScope.launch {
            if (isInitialLoad) {
                _uiState.value = ProductListUiState.Loading
            } else {
                _uiState.update { current ->
                    if (current is ProductListUiState.Data) {
                        ProductListUiState.Data(state.copy(refreshing = true))
                    } else {
                        current
                    }
                }
            }
            val request = ListProductsRequestDto(
                searchQuery = state.searchQuery.ifBlank { null },
                categories = state.categories,
                cookingRequired = state.cookingRequired,
                flags = state.flags,
                sortBy = state.sortBy,
                sortDirection = state.sortDirection,
            )
            runCatching { repo.listProducts(request) }
                .onSuccess {
                    state = state.copy(products = it.products, refreshing = false)
                    _uiState.value = ProductListUiState.Data(state)
                    isInitialLoad = false
                }
                .onFailure {
                    state = state.copy(refreshing = false)
                    _uiState.value = ProductListUiState.Error(it.message ?: "Load failed")
                    isInitialLoad = false
                }
        }
    }
}
