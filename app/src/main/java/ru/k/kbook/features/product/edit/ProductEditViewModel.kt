package ru.k.kbook.features.product.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.k.kbook.api.grpc.request.GetProductRequestDto
import ru.k.kbook.api.grpc.request.UpdateProductRequestDto
import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.ImageInput
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag
import ru.k.kbook.api.grpc.schema.ProductImage
import ru.k.kbook.data.ProductRepositoryImpl
import ru.k.kbook.domain.product.ProductRepository

// TODO("нельзя удалить картинку из продукта при редактировании, одна остается обязательно")
class ProductEditViewModel(
    private val productId: Long,
    private val repository: ProductRepository = ProductRepositoryImpl(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductEditUiState())
    val uiState: StateFlow<ProductEditUiState> = _uiState

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            runCatching {
                repository.getProduct(GetProductRequestDto(productId))
            }.onSuccess { response ->
                if (!response.success) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = response.message.ifBlank { "Failed to load product" },
                    )
                    return@onSuccess
                }
                val product = response.product
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    name = product?.name ?: "",
                    images = product?.images ?: emptyList(),
                    caloricity = product?.caloricity.toString(),
                    protein = product?.protein.toString(),
                    fat = product?.fat.toString(),
                    carb = product?.carb.toString(),
                    description = product?.description.orEmpty(),
                    category = product?.category ?: ProductCategory.VEGETABLES,
                    cookingRequired = product?.cookingRequired ?: CookingRequired.REQUIRES_COOKING,
                    flags = product?.flags ?: emptyList(),
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = err.message ?: "Failed to load product",
                )
            }
        }
    }

    fun updateName(name: String) = update { it.copy(name = name) }
    fun updateCaloricity(value: String) = update { it.copy(caloricity = value) }
    fun updateProtein(value: String) = update { it.copy(protein = value) }
    fun updateFat(value: String) = update { it.copy(fat = value) }
    fun updateCarb(value: String) = update { it.copy(carb = value) }
    fun updateDescription(value: String) = update { it.copy(description = value) }
    fun updateCategory(value: ProductCategory) = update { it.copy(category = value) }
    fun updateCookingRequired(value: CookingRequired) = update { it.copy(cookingRequired = value) }

    fun toggleFlag(flag: ProductFlag) {
        update {
            val next = if (flag in it.flags) it.flags - flag else it.flags + flag
            it.copy(flags = next)
        }
    }

    fun addImage(image: ProductImage) = update { it.copy(images = it.images + image) }
    fun removeImage(image: ProductImage) = update { it.copy(images = it.images - image) }

    fun save(onSuccess: () -> Unit) {
        val current = _uiState.value
        println("Current product: $current")

        viewModelScope.launch {
            update { it.copy(isSaving = true, error = null) }
            runCatching {
                repository.updateProduct(
                    UpdateProductRequestDto(
                        id = productId,
                        name = current.name,
                        images = current.images.map {
                            ImageInput(
                                id = it.id,
                                url = it.url?.takeIf(String::isNotBlank),
                                image = it.image,
                                contentType = it.contentType
                            )
                        },
                        caloricity = current.caloricity.toDoubleOrNull(),
                        protein = current.protein.toDoubleOrNull(),
                        fat = current.fat.toDoubleOrNull(),
                        carb = current.carb.toDoubleOrNull(),
                        description = current.description,
                        category = current.category,
                        cookingRequired = current.cookingRequired,
                        flags = current.flags,
                    ),
                )
            }.onSuccess { response ->
                if (response.success) {
                    onSuccess()
                } else {
                    update { it.copy(isSaving = false, error = response.message) }
                    println(response.message)
                }
            }.onFailure { err ->
                update { it.copy(isSaving = false, error = err.message ?: "Failed to update product") }
                err.printStackTrace()
            }
        }
    }

    fun clearError() = update { it.copy(error = null) }

    private fun update(reducer: (ProductEditUiState) -> ProductEditUiState) {
        _uiState.value = reducer(_uiState.value)
    }
}

class ProductEditVmFactory(
    private val productId: Long,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ProductEditViewModel(productId) as T
}
