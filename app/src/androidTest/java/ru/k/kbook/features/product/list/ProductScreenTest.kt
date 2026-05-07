package ru.k.kbook.features.product.list

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
import org.junit.runner.RunWith
import ru.k.kbook.MainActivity
import ru.k.kbook.features.product.create.ProductCreateScreenTag
import ru.k.kbook.features.product.models.ProductUiTest
import ru.k.kbook.features.product.utils.clearAllProducts
import ru.k.kbook.features.product.utils.createProduct

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ProductScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
        with(composeTestRule) {
            waitUntilNodeCount(
                hasTestTag(ProductScreenTag.CONTENT),
                count = 1,
                timeoutMillis = 5000,
            )
            clearAllProducts()
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
    fun testEmptyProducts() {
        with(composeTestRule) {
            waitUntilNodeCount(
                hasTestTag(ProductScreenTag.CONTENT),
                count = 1,
                timeoutMillis = 5000,
            )

            onNodeWithTag(ProductScreenTag.EMPTY).assertExists()
        }
    }


    @Test
    fun testFilters() {
        val productName = "Тестовый продукт"

        with(composeTestRule) {
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

            waitUntilNodeCount(
                hasTestTag(ProductScreenTag.ADD_PRODUCT_FAB),
                count = 1,
                timeoutMillis = 10_000,
            )

            waitUntilNodeCount(
                hasText(productName),
                count = 1,
                timeoutMillis = 10_000,
            )

            onNodeWithText(productName).assertExists()
        }
    }
}

// ./gradlew :app:connectedDebugAndroidTest