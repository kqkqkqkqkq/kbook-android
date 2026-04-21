package ru.k.kbook.features.dish.components

import ru.k.kbook.api.grpc.schema.DishCategory
import ru.k.kbook.api.grpc.schema.DishFlag
import ru.k.kbook.api.grpc.schema.DishImage
import ru.k.kbook.api.grpc.schema.DishProduct
import ru.k.kbook.api.grpc.schema.Product
import ru.k.kbook.api.grpc.schema.ProductFlag

data class DishCompositionInput(
    val productId: Long,
    val productName: String,
    val quantity: String,
)

data class DishFormState(
    val name: String = "",
    val images: List<DishImage> = emptyList(),
    val composition: List<DishCompositionInput> = emptyList(),
    val portionSize: String = "",
    val category: DishCategory? = null,
    val flags: Set<DishFlag> = emptySet(),
    val caloricity: String = "",
    val protein: String = "",
    val fat: String = "",
    val carb: String = "",
    val autoCaloricity: Double = 0.0,
    val autoProtein: Double = 0.0,
    val autoFat: Double = 0.0,
    val autoCarb: Double = 0.0,
    val availableFlags: Set<DishFlag> = emptySet(),
    val selectedProductId: Long? = null,
    val selectedQuantity: String = "",
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
)

fun parseCategoryMacro(name: String): Pair<String, DishCategory?> {
    val macros = listOf(
        "!десерт" to DishCategory.DESSERT,
        "!первое" to DishCategory.FIRST,
        "!второе" to DishCategory.SECOND,
        "!напиток" to DishCategory.DRINK,
        "!салат" to DishCategory.SALAD,
        "!суп" to DishCategory.SOUP,
        "!перекус" to DishCategory.SNACK,
    )
    val found = macros
        .map { it.first to name.indexOf(it.first, ignoreCase = true) }
        .filter { it.second >= 0 }
        .minByOrNull { it.second }
        ?: return name to null
    val cleaned = name.replaceRange(found.second, found.second + found.first.length, "").trim()
    val category = macros.first { it.first == found.first }.second
    return cleaned to category
}

fun calculateAutoNutrition(
    composition: List<DishCompositionInput>,
    productsById: Map<Long, Product>,
): Quadruple {
    var kcal = 0.0
    var protein = 0.0
    var fat = 0.0
    var carb = 0.0
    composition.forEach { item ->
        val product = productsById[item.productId] ?: return@forEach
        val qty = item.quantity.toDoubleOrNull() ?: return@forEach
        val factor = qty / 100.0
        kcal += product.caloricity * factor
        protein += product.protein * factor
        fat += product.fat * factor
        carb += product.carb * factor
    }
    return Quadruple(kcal, protein, fat, carb)
}

fun computeAvailableFlags(composition: List<DishCompositionInput>, productsById: Map<Long, Product>): Set<DishFlag> {
    if (composition.isEmpty()) return emptySet()
    val products = composition.mapNotNull { productsById[it.productId] }
    if (products.size != composition.size) return emptySet()
    val result = mutableSetOf<DishFlag>()
    if (products.all { ProductFlag.VEGAN in it.flags }) result += DishFlag.VEGAN
    if (products.all { ProductFlag.GLUTEN_FREE in it.flags }) result += DishFlag.GLUTEN_FREE
    if (products.all { ProductFlag.SUGAR_FREE in it.flags }) result += DishFlag.SUGAR_FREE
    return result
}

data class Quadruple(
    val caloricity: Double,
    val protein: Double,
    val fat: Double,
    val carb: Double,
)

fun bjuSumPer100(portionSize: Double, protein: Double, fat: Double, carb: Double): Double {
    if (portionSize <= 0.0) return protein + fat + carb
    return (protein + fat + carb) / portionSize * 100.0
}

fun toDishProducts(items: List<DishCompositionInput>): List<DishProduct> = items.mapNotNull {
    val qty = it.quantity.toDoubleOrNull() ?: return@mapNotNull null
    DishProduct(productId = it.productId, productName = it.productName, quantity = qty)
}
