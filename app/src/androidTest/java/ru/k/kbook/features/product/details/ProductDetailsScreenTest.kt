package ru.k.kbook.features.product.details

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.Before
import org.junit.After
import org.junit.Ignore
import org.junit.runner.RunWith
import ru.k.kbook.MainActivity
import ru.k.kbook.features.product.create.ProductCreateScreenTag
import ru.k.kbook.features.product.edit.ProductEditScreenTag
import ru.k.kbook.features.product.list.ProductScreenTag
import ru.k.kbook.features.product.models.ProductUiTest
import ru.k.kbook.features.utils.clearAllProducts
import ru.k.kbook.features.utils.createProduct

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ProductDetailsScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun before() {
        hiltRule.inject()
        with(composeTestRule) {
            clearAllProducts()
            val productName = "Тестовый продукт"
            waitUntilNodeCount(
                hasTestTag(ProductScreenTag.ADD_PRODUCT_FAB),
                count = 1,
                timeoutMillis = 5000,
            )
            onNodeWithTag(ProductScreenTag.ADD_PRODUCT_FAB).performClick()
            waitUntilNodeCount(
                hasTestTag(ProductCreateScreenTag.NAME_INPUT),
                count = 1,
                timeoutMillis = 5000,
            )
            createProduct(ProductUiTest(name = productName))
        }
    }

    @After
    fun after() {
        with(composeTestRule) {
            waitUntilNodeCount(
                hasTestTag(ProductScreenTag.CONTENT),
                count = 1,
                timeoutMillis = 5000,
            )
            clearAllProducts()
        }
    }

    @Test
    @Ignore("Временно отключен")
    fun displayedUpdatedDateAfterUpdate() {
        with(composeTestRule) {
            onAllNodesWithTag(ProductScreenTag.PRODUCT_CARD)[0].performClick()
            waitUntilNodeCount(
                hasTestTag(ProductDetailScreenTag.TITLE),
                count = 1,
                timeoutMillis = 5000,
            )
            onNodeWithTag(ProductDetailScreenTag.UPDATE_DATE).isNotDisplayed()
            onNodeWithTag(ProductDetailScreenTag.CREATE_DATE).isDisplayed()
            onNodeWithTag(ProductDetailScreenTag.EDIT_BUTTON).performClick()
            waitUntilNodeCount(
                hasTestTag(ProductEditScreenTag.NAME_INPUT),
                count = 1,
                timeoutMillis = 5000,
            )
            onNodeWithTag(ProductEditScreenTag.NAME_INPUT).performTextInput("Edited name")
            onNodeWithTag(ProductEditScreenTag.SCROLLABLE_COLUMN)
                .performScrollToNode(hasTestTag(ProductEditScreenTag.SAVE_BUTTON))
            onNodeWithTag(ProductEditScreenTag.SAVE_BUTTON).performClick()
            Thread.sleep(2000L)
            onNodeWithTag(ProductDetailScreenTag.UPDATE_DATE).isDisplayed()
            onNodeWithTag(ProductDetailScreenTag.CREATE_DATE).isDisplayed()
            onNodeWithTag(ProductDetailScreenTag.BACK_BUTTON).performClick()
        }
    }
}
