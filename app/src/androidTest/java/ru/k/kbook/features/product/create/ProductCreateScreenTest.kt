package ru.k.kbook.features.product.create

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
import ru.k.kbook.features.product.list.ProductScreenTag
import ru.k.kbook.features.product.models.ProductUiTest
import ru.k.kbook.features.utils.clearAllProducts
import ru.k.kbook.features.utils.createProduct

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ProductCreateScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun before() {
        hiltRule.inject()
        with(composeTestRule) {
            clearAllProducts()

            waitUntilNodeCount(
                hasTestTag(ProductScreenTag.ADD_PRODUCT_FAB),
                count = 1,
                timeoutMillis = 5000,
            )

            onNodeWithTag(ProductScreenTag.ADD_PRODUCT_FAB).performClick()
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
    fun createProductWithCorrectData() {
        val productName = "Тестовый продукт"

        with(composeTestRule) {
            waitUntilNodeCount(
                hasTestTag(ProductCreateScreenTag.NAME_INPUT),
                count = 1,
                timeoutMillis = 5000,
            )

            createProduct(ProductUiTest(name = productName))

            waitUntilNodeCount(
                hasText(productName),
                count = 1,
                timeoutMillis = 10_000,
            )

            onNodeWithText(productName).assertExists()
        }
    }

    @Test
    @Ignore("Временно отключен")
    fun createProductWithOneSymbolName() {
        val productName = "A"

        with(composeTestRule) {
            waitUntilNodeCount(
                hasTestTag(ProductCreateScreenTag.NAME_INPUT),
                count = 1,
                timeoutMillis = 5000,
            )

            createProduct(ProductUiTest(name = productName))

            waitUntilNodeCount(
                hasTestTag(ProductCreateScreenTag.SNACKBAR),
                count = 1,
                timeoutMillis = 10_000,
            )

            onNodeWithTag(ProductCreateScreenTag.SNACKBAR).assertExists()

            onNodeWithTag(ProductCreateScreenTag.BACK_BUTTON).performClick()
        }
    }

    @Test
    @Ignore("Временно отключен")
    fun createProductWithNegativeCaloricity() {
        val productName = "Test product"

        with(composeTestRule) {
            waitUntilNodeCount(
                hasTestTag(ProductCreateScreenTag.NAME_INPUT),
                count = 1,
                timeoutMillis = 5000,
            )

            createProduct(
                ProductUiTest(
                    name = productName,
                    caloricity = -100.0,
                )
            )

            waitUntilNodeCount(
                hasTestTag(ProductCreateScreenTag.SNACKBAR),
                count = 1,
                timeoutMillis = 10_000,
            )

            onNodeWithTag(ProductCreateScreenTag.SNACKBAR).assertExists()

            onNodeWithTag(ProductCreateScreenTag.BACK_BUTTON).performClick()
        }
    }

    @Test
    @Ignore("Временно отключен")
    fun createProductWithHighBju() {
        val productName = "Test product"

        with(composeTestRule) {
            waitUntilNodeCount(
                hasTestTag(ProductCreateScreenTag.NAME_INPUT),
                count = 1,
                timeoutMillis = 5000,
            )

            createProduct(
                ProductUiTest(
                    name = productName,
                    protein = 50.0,
                    fat = 50.0,
                    carb = 50.0,
                )
            )

            waitUntilNodeCount(
                hasTestTag(ProductCreateScreenTag.SNACKBAR),
                count = 1,
                timeoutMillis = 10_000,
            )

            onNodeWithTag(ProductCreateScreenTag.SNACKBAR).assertExists()

            onNodeWithTag(ProductCreateScreenTag.BACK_BUTTON).performClick()
        }
    }

    @Test
    @Ignore("Временно отключен")
    fun createProductWithMoreThanFivePhotos() {
        val productName = "Test product"
        val url = "https://test.com"

        with(composeTestRule) {
            waitUntilNodeCount(
                hasTestTag(ProductCreateScreenTag.NAME_INPUT),
                count = 1,
                timeoutMillis = 5000,
            )

            createProduct(ProductUiTest(name = productName, images = listOf(url, url, url, url, url, url)))

            waitUntilNodeCount(
                hasTestTag(ProductCreateScreenTag.SNACKBAR),
                count = 1,
                timeoutMillis = 10_000,
            )

            onNodeWithTag(ProductCreateScreenTag.SNACKBAR).assertExists()

            onNodeWithTag(ProductCreateScreenTag.BACK_BUTTON).performClick()
        }
    }

    @Test
    @Ignore("Временно отключен")
    fun createProductWithFivePhotos() {
        val productName = "Test product"
        val url = "https://test.com"

        with(composeTestRule) {
            waitUntilNodeCount(
                hasTestTag(ProductCreateScreenTag.NAME_INPUT),
                count = 1,
                timeoutMillis = 5000,
            )

            createProduct(ProductUiTest(name = productName, images = listOf(url, url, url, url, url)))

            waitUntilNodeCount(
                hasText(productName),
                count = 1,
                timeoutMillis = 10_000,
            )

            onNodeWithText(productName).assertExists()
        }
    }
}
