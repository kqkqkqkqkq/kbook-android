package ru.k.kbook.features.product.create

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.k.kbook.api.grpc.request.CreateProductRequestDto
import ru.k.kbook.api.grpc.schema.ContentType
import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.ImageInput
import ru.k.kbook.api.grpc.schema.Product
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductImage
import ru.k.kbook.data.ProductRepositoryImpl
import ru.k.kbook.domain.product.ProductRepository
import ru.k.kbook_api.grpc.product.CreateProductRequest
import java.time.Instant
import kotlin.collections.copy

class ProductCreateViewModel(
    private val repository: ProductRepository = ProductRepositoryImpl(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductCreateUiState())
    val uiState: StateFlow<ProductCreateUiState> = _uiState

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun addImageUrl(url: ProductImage) {
//        if (url.isNotBlank()) {
            val updated = _uiState.value.images + url
            // TODO()
            _uiState.value = _uiState.value.copy(images = updated)
//        }
    }

    fun removeImageUrl(url: ProductImage) {
        val updated = _uiState.value.images - url
        _uiState.value = _uiState.value.copy(images = updated)
    }

    fun updateCaloricity(cal: String) {
        _uiState.value = _uiState.value.copy(caloricity = cal)
    }

    fun updateProtein(protein: String) {
        _uiState.value = _uiState.value.copy(protein = protein)
    }

    fun updateFat(fat: String) {
        _uiState.value = _uiState.value.copy(fat = fat)
    }

    fun updateCarb(carb: String) {
        _uiState.value = _uiState.value.copy(carb = carb)
    }

    fun updateDescription(desc: String) {
        _uiState.value = _uiState.value.copy(description = desc)
    }

    fun updateCategory(category: ProductCategory) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updateCookingRequired(cooking: CookingRequired) {
        _uiState.value = _uiState.value.copy(cookingRequired = cooking)
    }

    fun createProduct(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Валидация
            if (_uiState.value.name.isBlank()) {
                _uiState.value = _uiState.value.copy(error = "Name is required", isLoading = false)
                return@launch
            }

            try {
                val product = CreateProductRequestDto(
                    name = _uiState.value.name,
                    images = _uiState.value.images.map {
                        ImageInput(
                            it.url,
                            null,
                            ContentType.URL
                        )
                    },
                    caloricity = _uiState.value.caloricity.toDoubleOrNull() ?: 0.0,
                    protein = _uiState.value.protein.toDoubleOrNull() ?: 0.0,
                    fat = _uiState.value.fat.toDoubleOrNull() ?: 0.0,
                    carb = _uiState.value.carb.toDoubleOrNull() ?: 0.0,
                    description = _uiState.value.description.ifBlank { null },
                    category = _uiState.value.category,
                    cookingRequired = _uiState.value.cookingRequired,
                    flags = emptyList(),
                )
                val response = repository.createProduct(product)
                if (response.success) {
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(error = response.message, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
