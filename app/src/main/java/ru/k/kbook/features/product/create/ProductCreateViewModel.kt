package ru.k.kbook.features.product.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.k.kbook.api.grpc.request.CreateProductRequestDto
import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.ImageInput
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag
import ru.k.kbook.api.grpc.schema.ProductImage
import ru.k.kbook.domain.product.ProductRepository
import javax.inject.Inject

@HiltViewModel
class ProductCreateViewModel @Inject constructor(
    private val repository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductCreateUiState())
    val uiState: StateFlow<ProductCreateUiState> = _uiState

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun addImageUrl(url: ProductImage) {
        val updated = _uiState.value.images + url
        _uiState.value = _uiState.value.copy(images = updated)
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

    fun updateFlags(flag: ProductFlag) {
        val flags = _uiState.value.flags
        if (flag in flags) {
            _uiState.value = _uiState.value.copy(flags = flags - flag)
        } else {
            _uiState.value = _uiState.value.copy(flags = flags + flag)
        }
    }

    fun createProduct(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            println("Create product: ${_uiState.value}")

            try {
                val product = CreateProductRequestDto(
                    name = _uiState.value.name,
                    images = _uiState.value.images.map {
                        ImageInput(
                            id = it.id,
                            url = it.url?.takeIf(String::isNotBlank),
                            image = it.image,
                            contentType = it.contentType,
                        )
                    },
                    caloricity = _uiState.value.caloricity.toDoubleOrNull() ?: 0.0,
                    protein = _uiState.value.protein.toDoubleOrNull() ?: 0.0,
                    fat = _uiState.value.fat.toDoubleOrNull() ?: 0.0,
                    carb = _uiState.value.carb.toDoubleOrNull() ?: 0.0,
                    description = _uiState.value.description.ifBlank { null },
                    category = _uiState.value.category,
                    cookingRequired = _uiState.value.cookingRequired,
                    flags = _uiState.value.flags,
                )
                val response = repository.createProduct(product)
                if (response.success) {
                    onSuccess()
                    Log.e("PRODUCT_CREATE", "success")
                } else {
                    _uiState.value =
                        _uiState.value.copy(error = response.message, isLoading = false)
                    Log.e("PRODUCT_CREATE", response.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
                Log.e("PRODUCT_CREATE", e.toString())
                e.printStackTrace()
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
