package ru.k.kbook.features.dish.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.k.kbook.api.grpc.request.CreateDishRequestDto
import ru.k.kbook.api.grpc.request.ListProductsRequestDto
import ru.k.kbook.api.grpc.schema.DishCategory
import ru.k.kbook.api.grpc.schema.DishFlag
import ru.k.kbook.api.grpc.schema.DishImage
import ru.k.kbook.domain.dish.DishRepository
import ru.k.kbook.domain.product.ProductRepository
import ru.k.kbook.features.dish.components.DishCompositionInput
import ru.k.kbook.features.dish.components.DishFormState
import ru.k.kbook.features.dish.components.bjuSumPer100
import ru.k.kbook.features.dish.components.calculateAutoNutrition
import ru.k.kbook.features.dish.components.computeAvailableFlags
import ru.k.kbook.features.dish.components.parseCategoryMacro
import ru.k.kbook.features.dish.components.toDishProducts
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class DishCreateViewModel @Inject constructor(
    private val dishRepo: DishRepository,
    private val productRepo: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DishFormState(isLoading = true))
    val uiState: StateFlow<DishFormState> = _uiState
    private var nutritionManuallyEdited = false

    init {
        loadProducts()
    }

    fun updateName(value: String) {
        val (cleanName, macroCategory) = parseCategoryMacro(value)
        _uiState.value = _uiState.value.copy(
            name = cleanName,
            category = _uiState.value.category ?: macroCategory,
        )
    }

    fun updateCategory(category: DishCategory?) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updatePortionSize(value: String) {
        _uiState.value = _uiState.value.copy(portionSize = value); recalculateDrafts()
    }

    fun updateCaloricity(value: String) {
        nutritionManuallyEdited = true; _uiState.value = _uiState.value.copy(caloricity = value)
    }

    fun updateProtein(value: String) {
        nutritionManuallyEdited = true; _uiState.value = _uiState.value.copy(protein = value)
    }

    fun updateFat(value: String) {
        nutritionManuallyEdited = true; _uiState.value = _uiState.value.copy(fat = value)
    }

    fun updateCarb(value: String) {
        nutritionManuallyEdited = true; _uiState.value = _uiState.value.copy(carb = value)
    }

    fun updateSelectedProduct(productId: Long?) {
        _uiState.value = _uiState.value.copy(selectedProductId = productId)
    }

    fun updateSelectedQuantity(value: String) {
        _uiState.value = _uiState.value.copy(selectedQuantity = value)
    }

    fun addImage(image: DishImage) {
        _uiState.value = _uiState.value.copy(images = _uiState.value.images + image)
    }

    fun removeImage(image: DishImage) {
        _uiState.value = _uiState.value.copy(images = _uiState.value.images - image)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun toggleFlag(flag: DishFlag) {
        if (flag !in _uiState.value.availableFlags) return
        val flags = _uiState.value.flags.toMutableSet()
        if (!flags.add(flag)) flags.remove(flag)
        _uiState.value = _uiState.value.copy(flags = flags)
    }

    fun addCompositionItem() {
        val state = _uiState.value
        val product = state.products.firstOrNull { it.id == state.selectedProductId } ?: return
        val q = state.selectedQuantity.toDoubleOrNull() ?: return
        if (q < 0) return
        _uiState.value = state.copy(
            composition = state.composition + DishCompositionInput(
                product.id,
                product.name,
                state.selectedQuantity,
            ),
            selectedProductId = null,
            selectedQuantity = "",
        )
        recalculateDrafts()
    }

    fun removeCompositionItem(index: Int) {
        val state = _uiState.value
        if (index !in state.composition.indices) return
        _uiState.value =
            state.copy(composition = state.composition.filterIndexed { i, _ -> i != index })
        recalculateDrafts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            runCatching { productRepo.listProducts(ListProductsRequestDto()) }
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        products = it.products,
                    ); recalculateDrafts()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = it.message ?: "Не удалось загрузить продукты",
                    )
                }
        }
    }

    private fun recalculateDrafts() {
        val state = _uiState.value
        val productsById = state.products.associateBy { it.id }
        val auto = calculateAutoNutrition(state.composition, productsById)
        val availableFlags = computeAvailableFlags(state.composition, productsById)
        val safeFlags = state.flags.intersect(availableFlags)
        _uiState.value = state.copy(
            autoCaloricity = auto.caloricity,
            autoProtein = auto.protein,
            autoFat = auto.fat,
            autoCarb = auto.carb,
            availableFlags = availableFlags,
            flags = safeFlags,
            caloricity = if (nutritionManuallyEdited) state.caloricity else auto.caloricity.round1(),
            protein = if (nutritionManuallyEdited) state.protein else auto.protein.round1(),
            fat = if (nutritionManuallyEdited) state.fat else auto.fat.round1(),
            carb = if (nutritionManuallyEdited) state.carb else auto.carb.round1(),
        )
    }

    fun save(onSuccess: () -> Unit) {
        val state = _uiState.value
        val portion = state.portionSize.toDoubleOrNull()
        val protein = state.protein.toDoubleOrNull()
        val fat = state.fat.toDoubleOrNull()
        val carb = state.carb.toDoubleOrNull()
        val cal = state.caloricity.toDoubleOrNull()
        if (state.name.isBlank() || portion == null || cal == null || protein == null || fat == null || carb == null) {
            _uiState.value = state.copy(error = "Заполните обязательные поля")
            return
        }
        if (bjuSumPer100(portion, protein, fat, carb) > 100.0) {
            _uiState.value = state.copy(error = "Сумма БЖУ на 100 г не может быть больше 100")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            runCatching {
                require(state.images.size <= 5) { "Максимальное количество картинок: 5" }
                require(portion >= 0) { "Размер порции не может быть отрицательным" }
                require(cal >= 0) { "Количество каллорий не может быть отрицательным" }
                require(protein >= 0) { "Количество белков не может быть отрицательным" }
                require(fat >= 0) { "Количество жиров не может быть отрицательным" }
                require(carb >= 0) { "Количество углеводов не может быть отрицательным" }
                dishRepo.createDish(
                    CreateDishRequestDto(
                        name = state.name,
                        images = state.images,
                        composition = toDishProducts(state.composition),
                        portionSize = portion,
                        category = state.category,
                        flags = state.flags.toList(),
                        caloricity = cal,
                        protein = protein,
                        fat = fat,
                        carb = carb,
                    ),
                )
            }.onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSuccess()
            }.onFailure {
                _uiState.value =
                    _uiState.value.copy(isSaving = false, error = it.message ?: "Ошибка создания")
            }
        }
    }
}

private fun Double.round1(): String = (round(this * 10.0) / 10.0).toString()
