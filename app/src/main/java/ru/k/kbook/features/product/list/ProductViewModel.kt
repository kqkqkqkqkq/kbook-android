package ru.k.kbook.features.product.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
)

class ProductViewModel(
    private val repo: ProductRepository = ProductRepositoryImpl(),
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProductListEvent>()
    val events: SharedFlow<ProductListEvent> = _events.asSharedFlow()

    private var originalProducts: List<Product> = emptyList() // Храним оригинальный список
    private var state = ProductListState()

    init {
        load()
    }

    fun onSearchChange(value: String) {
        state = state.copy(searchQuery = value)
        updateList()
    }

    fun onCategoryToggle(value: ProductCategory) {
        val selected = if (value in state.categories) state.categories - value else state.categories + value
        state = state.copy(categories = selected)
        updateList()
    }

    fun onCookingToggle(value: CookingRequired) {
        val selected = if (value in state.cookingRequired) state.cookingRequired - value else state.cookingRequired + value
        state = state.copy(cookingRequired = selected)
        updateList()
    }

    fun onFlagToggle(value: ProductFlag) {
        val selected = if (value in state.flags) state.flags - value else state.flags + value
        state = state.copy(flags = selected)
        updateList()
    }

    fun onSort(value: SortField, direction: SortDirection) {
        state = state.copy(sortBy = value, sortDirection = direction)
        updateList()
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

    private fun updateList() {
        val filteredAndSorted = applyFiltersAndSort()
        state = state.copy(products = filteredAndSorted) // Обновляем отображаемый список
        _uiState.value = ProductListUiState.Data(state)
    }

    private fun sortList(products: List<Product>): List<Product> {
        return when (state.sortBy) {
            SortField.NAME -> products.sortedBy { it.name }
            SortField.CALORICITY -> products.sortedBy { it.caloricity }
            SortField.FAT -> products.sortedBy { it.fat }
            SortField.CARB -> products.sortedBy { it.carb }
            SortField.PROTEIN -> products.sortedBy { it.protein }
        }.let { if (state.sortDirection == SortDirection.DESC) it.reversed() else it }
    }

    private fun filterByCategory(products: List<Product>): List<Product> {
        return if (state.categories.isEmpty()) products
        else products.filter { product -> state.categories.contains(product.category) }
    }

    private fun filterByCookingRequired(products: List<Product>): List<Product> {
        return if (state.cookingRequired.isEmpty()) products
        else products.filter { product -> state.cookingRequired.contains(product.cookingRequired) }
    }

    private fun filterByFlags(products: List<Product>): List<Product> {
        return if (state.flags.isEmpty()) products
        else products.filter { product -> state.flags.all { flag -> product.flags.contains(flag) } }
    }

    private fun searchProducts(products: List<Product>): List<Product> {
        return if (state.searchQuery.isBlank()) products
        else products.filter { product ->
            product.name.contains(state.searchQuery, ignoreCase = true) ||
                    (product.description?.contains(state.searchQuery, ignoreCase = true) == true)
        }
    }

    private fun applyFiltersAndSort(): List<Product> {
        return originalProducts // ← Фильтруем всегда с оригинала
            .let(::filterByCategory)
            .let(::filterByCookingRequired)
            .let(::filterByFlags)
            .let(::searchProducts)
            .let(::sortList)
    }

    private fun load() {
        viewModelScope.launch {
            val request = ListProductsRequestDto()
            runCatching { repo.listProducts(request) }
                .onSuccess { response ->
                    originalProducts = response.products
                    updateList()
                }
                .onFailure {
                    _uiState.value = ProductListUiState.Error(it.message ?: "Load failed")
                }
        }
    }
}