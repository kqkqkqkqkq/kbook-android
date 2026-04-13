package ru.k.kbook.features.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    var searchQuery: String? = null
        private set

    val categories: List<ProductCategory>? = null
    val cookingRequired: List<CookingRequired>? = null
    val flags: List<ProductFlag>? = null
    val sortBy: SortField = SortField.NAME
    val sortDirection: SortDirection = SortDirection.DESC

    private val _products = MutableStateFlow(emptyList<Product>())
    val products = _products.asStateFlow()

    init {
        getProducts()
    }

    fun getProducts() {
        viewModelScope.launch {
            try {
                val req = ListProductsRequestDto(searchQuery, categories, cookingRequired, flags, sortBy, sortDirection);
                _products.emit(productRepository.listProducts(req).products)
            } catch (e: Exception) {
                Log.e("ProductViewModel", e.message.toString())
            }
        }
    }
}
