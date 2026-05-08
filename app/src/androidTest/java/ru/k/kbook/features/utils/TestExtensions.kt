package ru.k.kbook.features.utils

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import ru.k.kbook.features.product.create.ProductCreateScreenTag
import ru.k.kbook.features.product.list.ProductScreenTag
import ru.k.kbook.features.product.models.ProductUiTest
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextClearance
import ru.k.kbook.api.grpc.schema.DishCategory
import ru.k.kbook.features.dish.DishScreenTag
import ru.k.kbook.features.dish.create.DishCreateScreenTag
import ru.k.kbook.features.dish.models.DishUiTest
import ru.k.kbook.navigation.NavigationTag

/**
 * Must be on the ProductCreateScreen
 */
fun SemanticsNodeInteractionsProvider.createProduct(product: ProductUiTest) {

    onNodeWithTag(ProductCreateScreenTag.NAME_INPUT).performTextInput(product.name)

    onNodeWithTag(ProductCreateScreenTag.CALORICITY_INPUT).performTextInput(product.caloricity.toString())

    onNodeWithTag(ProductCreateScreenTag.PROTEIN_INPUT).performTextInput(product.protein.toString())

    onNodeWithTag(ProductCreateScreenTag.FAT_INPUT).performTextInput(product.fat.toString())

    onNodeWithTag(ProductCreateScreenTag.CARB_INPUT).performTextInput(product.carb.toString())

    onNodeWithTag(ProductCreateScreenTag.CATEGORY_INPUT).performClick()
    onNodeWithTag("${ProductCreateScreenTag.PREFIX}_${product.category.name}").performClick()

    onNodeWithTag(ProductCreateScreenTag.SCROLLABLE_COLUMN)
        .performScrollToNode(hasTestTag(ProductCreateScreenTag.SAVE_BUTTON))

    product.images.forEach { url ->
        onNodeWithTag(ProductCreateScreenTag.URL_INPUT).performTextInput(url)
        onNodeWithTag(ProductCreateScreenTag.ADD_IMAGE_BUTTON).performClick()
    }

    onNodeWithTag(ProductCreateScreenTag.COOKING_REQUIRED_INPUT).performClick()
    onNodeWithTag("${ProductCreateScreenTag.PREFIX}_${product.cookingRequired.name}").performClick()

    product.flags.forEach { flag ->
        onNodeWithTag("${ProductCreateScreenTag.PREFIX}_${flag.name}").performClick()
    }

    onNodeWithTag(ProductCreateScreenTag.SCROLLABLE_COLUMN)
        .performScrollToNode(hasTestTag(ProductCreateScreenTag.SAVE_BUTTON))

    onNodeWithTag(ProductCreateScreenTag.SAVE_BUTTON).performClick()

    Thread.sleep(2_000L)
}

/**
 * Must be on the ProductScreen
 */
fun SemanticsNodeInteractionsProvider.clearAllProducts() {

    try {
        onNodeWithTag(ProductScreenTag.EMPTY).assertExists()
        return
    } catch (e: AssertionError) {

    }

    var hasProducts = true
    var attempts = 0

    while (hasProducts && attempts < 3) {
        try {
            onAllNodesWithTag(ProductScreenTag.DELETE_PRODUCT_BUTTON)[0]
                .performClick()

            attempts++

            Thread.sleep(500)

            try {
                onNodeWithTag(ProductScreenTag.EMPTY).assertExists()
                hasProducts = false
            } catch (e: AssertionError) {
                hasProducts = true
            }
        } catch (e: Exception) {
            hasProducts = false
        }
    }

    Thread.sleep(500)
}


/**
 * Must be on the DishCreateScreen
 */
fun SemanticsNodeInteractionsProvider.createDish(dish: DishUiTest, auto: Boolean = true) {

    onNodeWithTag(DishCreateScreenTag.NAME_INPUT).performTextInput(dish.name)

    if (auto) {
        onNodeWithTag(DishCreateScreenTag.CALORICITY_INPUT).performTextClearance()
        onNodeWithTag(DishCreateScreenTag.CALORICITY_INPUT).performTextInput(dish.caloricity.toString())
        onNodeWithTag(DishCreateScreenTag.PROTEIN_INPUT).performTextClearance()
        onNodeWithTag(DishCreateScreenTag.PROTEIN_INPUT).performTextInput(dish.protein.toString())
        onNodeWithTag(DishCreateScreenTag.FAT_INPUT).performTextClearance()
        onNodeWithTag(DishCreateScreenTag.FAT_INPUT).performTextInput(dish.fat.toString())
        onNodeWithTag(DishCreateScreenTag.CARB_INPUT).performTextClearance()
        onNodeWithTag(DishCreateScreenTag.CARB_INPUT).performTextInput(dish.carb.toString())
    }

    onNodeWithTag(DishCreateScreenTag.SIZE_INPUT).performTextInput(dish.portionSize.toString())

    onNodeWithTag(DishCreateScreenTag.SCROLLABLE_COLUMN)
        .performScrollToNode(hasTestTag(DishCreateScreenTag.ADD_COMPOSITION_BUTTON))

    dish.composition.forEach { p ->
        onNodeWithTag(DishCreateScreenTag.ADD_COMPOSITION_BUTTON).performClick()
        onNodeWithTag("${DishCreateScreenTag.PREFIX}_${p[0]}").performClick()
        onNodeWithTag(DishCreateScreenTag.WEIGHT_INPUT).performTextInput(p[1].toString())
        onNodeWithTag(DishCreateScreenTag.ADD_COMPOSITION_SUBMIT_BUTTON).performClick()
    }

    onNodeWithTag(DishCreateScreenTag.SCROLLABLE_COLUMN)
        .performScrollToNode(hasTestTag(DishCreateScreenTag.ADD_IMAGE_BUTTON))

    dish.images.forEach { url ->
        onNodeWithTag(DishCreateScreenTag.URL_INPUT).performTextInput(url)
        onNodeWithTag(DishCreateScreenTag.ADD_IMAGE_BUTTON).performClick()
    }

    onNodeWithTag(DishCreateScreenTag.SCROLLABLE_COLUMN)
        .performScrollToNode(hasTestTag(DishCreateScreenTag.NAME_INPUT))

    onNodeWithTag(DishCreateScreenTag.CATEGORY_INPUT, useUnmergedTree = true).performClick()
    onNodeWithTag("${DishCreateScreenTag.PREFIX}_${dish.category.name}").performClick()

    onNodeWithTag(DishCreateScreenTag.SCROLLABLE_COLUMN)
        .performScrollToNode(hasTestTag(DishCreateScreenTag.SAVE_BUTTON))

    dish.flags.forEach { flag ->
        onNodeWithTag("${DishCreateScreenTag.PREFIX}_${flag.name}").performClick()
    }

    onNodeWithTag(DishCreateScreenTag.SAVE_BUTTON).performClick()

    Thread.sleep(2_000L)
}

/**
 * Must be on the DishCreateScreen
 */
fun SemanticsNodeInteractionsProvider.clearAllDishes() {
    while (true) {
        try {
            val nodes = onAllNodesWithTag(DishScreenTag.CARD_DELETE_BUTTON)
            if (nodes.fetchSemanticsNodes().isEmpty()) {
                break
            }
            nodes[0].performClick()
            Thread.sleep(500)
        } catch (e: Exception) {
            break
        }
    }
    Thread.sleep(500)
}

fun SemanticsNodeInteractionsProvider.navigateToProduct() {
    onNodeWithTag(NavigationTag.PRODUCTS).performClick()
}

fun SemanticsNodeInteractionsProvider.navigateToDish() {
    onNodeWithTag(NavigationTag.DISHES).performClick()
}
