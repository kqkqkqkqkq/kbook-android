package ru.k.kbook.features.dish.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.k.kbook.api.grpc.request.GetDishRequestDto
import ru.k.kbook.api.grpc.request.ListProductsRequestDto
import ru.k.kbook.api.grpc.request.UpdateDishRequestDto
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
class DishEditViewModel @Inject constructor(
    private val dishRepo: DishRepository,
    private val productRepo: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DishFormState(isLoading = true))
    val uiState: StateFlow<DishFormState> = _uiState
    private var nutritionManuallyEdited = false


    fun load(dishId: Long) {
        viewModelScope.launch {
            runCatching { productRepo.listProducts(ListProductsRequestDto()).products }
                .onSuccess { products ->
                    runCatching { dishRepo.getDish(GetDishRequestDto(dishId)).dish }
                        .onSuccess { dish ->
                            if (dish == null) {
                                _uiState.value =
                                    DishFormState(isLoading = false, error = "Блюдо не найдено")
                                return@onSuccess
                            }
                            _uiState.value = DishFormState(
                                isLoading = false,
                                products = products,
                                name = dish.name,
                                images = dish.images,
                                portionSize = dish.portionSize.toString(),
                                category = dish.category,
                                flags = dish.flags.toSet(),
                                caloricity = dish.caloricity.toString(),
                                protein = dish.protein.toString(),
                                fat = dish.fat.toString(),
                                carb = dish.carb.toString(),
                                composition = dish.composition.map {
                                    DishCompositionInput(
                                        it.productId,
                                        it.productName,
                                        it.quantity.toString(),
                                    )
                                },
                            )
                            recalculateDrafts()
                        }
                        .onFailure {
                            _uiState.value = DishFormState(
                                isLoading = false,
                                error = it.message ?: "Ошибка загрузки",
                            )
                        }
                }
                .onFailure {
                    _uiState.value =
                        DishFormState(isLoading = false, error = it.message ?: "Ошибка загрузки")
                }
        }
    }

    fun updateName(value: String) {
        val (name, macro) = parseCategoryMacro(value); _uiState.value =
            _uiState.value.copy(name = name, category = _uiState.value.category ?: macro)
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
        if (flag !in _uiState.value.availableFlags) return; _uiState.value =
            _uiState.value.copy(flags = _uiState.value.flags.toggle(flag))
    }

    fun addCompositionItem() {
        val s = _uiState.value
        val product = s.products.firstOrNull { it.id == s.selectedProductId } ?: return
        if (s.selectedQuantity.toDoubleOrNull() == null) return
        _uiState.value = s.copy(
            composition = s.composition + DishCompositionInput(
                product.id,
                product.name,
                s.selectedQuantity,
            ),
            selectedProductId = null, selectedQuantity = "",
        )
        recalculateDrafts()
    }

    fun removeCompositionItem(index: Int) {
        val s = _uiState.value
        _uiState.value = s.copy(composition = s.composition.filterIndexed { i, _ -> i != index })
        recalculateDrafts()
    }

    private fun recalculateDrafts() {
        val s = _uiState.value
        val productsById = s.products.associateBy { it.id }
        val auto = calculateAutoNutrition(s.composition, productsById)
        val availableFlags = computeAvailableFlags(s.composition, productsById)
        _uiState.value = s.copy(
            autoCaloricity = auto.caloricity,
            autoProtein = auto.protein,
            autoFat = auto.fat,
            autoCarb = auto.carb,
            availableFlags = availableFlags,
            flags = s.flags.intersect(availableFlags),
            caloricity = if (nutritionManuallyEdited) s.caloricity else auto.caloricity.round1(),
            protein = if (nutritionManuallyEdited) s.protein else auto.protein.round1(),
            fat = if (nutritionManuallyEdited) s.fat else auto.fat.round1(),
            carb = if (nutritionManuallyEdited) s.carb else auto.carb.round1(),
        )
    }

    fun save(dishId: Long, onSuccess: () -> Unit) {
        val s = _uiState.value
        val portion = s.portionSize.toDoubleOrNull()
        val p = s.protein.toDoubleOrNull()
        val f = s.fat.toDoubleOrNull()
        val c = s.carb.toDoubleOrNull()
        val kcal = s.caloricity.toDoubleOrNull()
        if (s.name.isBlank() || portion == null || p == null || f == null || c == null || kcal == null) {
            _uiState.value = s.copy(error = "Заполните обязательные поля"); return
        }
        if (bjuSumPer100(portion, p, f, c) > 100.0) {
            _uiState.value = s.copy(error = "Сумма БЖУ на 100 г не может быть больше 100"); return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            runCatching {
                dishRepo.updateDish(
                    UpdateDishRequestDto(
                        id = dishId,
                        name = s.name,
                        images = s.images,
                        composition = toDishProducts(s.composition),
                        portionSize = portion,
                        category = s.category,
                        flags = s.flags.toList(),
                        caloricity = kcal,
                        protein = p,
                        fat = f,
                        carb = c,
                    ),
                )
            }.onSuccess { _uiState.value = _uiState.value.copy(isSaving = false); onSuccess() }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = it.message ?: "Ошибка сохранения",
                    )
                }
        }
    }
}

private fun Set<DishFlag>.toggle(flag: DishFlag): Set<DishFlag> =
    if (flag in this) this - flag else this + flag

fun Double.round1(): String = (round(this * 10.0) / 10.0).toString()
