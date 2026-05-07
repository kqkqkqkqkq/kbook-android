package ru.k.kbook.features.product.utils

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import ru.k.kbook.features.product.create.ProductCreateScreenTag
import ru.k.kbook.features.product.list.ProductScreenTag
import ru.k.kbook.features.product.models.ProductUiTest

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

    onNodeWithTag(ProductCreateScreenTag.COOKING_REQUIRED_INPUT).performClick()
    onNodeWithTag("${ProductCreateScreenTag.PREFIX}_${product.cookingRequired.name}").performClick()

    product.flags.forEach { flag ->
        onNodeWithTag("${ProductCreateScreenTag.PREFIX}_${flag.name}").performClick()
    }

    onNodeWithTag(ProductCreateScreenTag.SAVE_BUTTON).performClick()

    Thread.sleep(2_000L)
}

fun SemanticsNodeInteractionsProvider.clearAllProducts() {

    try {
        onNodeWithTag(ProductScreenTag.EMPTY).assertExists()
        return
    } catch (e: AssertionError) {

    }

    var hasProducts = true
    var attempts = 0
    val maxAttempts = 50

    while (hasProducts && attempts < maxAttempts) {
        try {
            onAllNodesWithTag(ProductScreenTag.DELETE_PRODUCT_BUTTON)[0]
                .performClick()

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
        attempts++
    }

    Thread.sleep(500)
}