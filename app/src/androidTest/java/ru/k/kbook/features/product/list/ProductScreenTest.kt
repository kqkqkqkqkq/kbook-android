package ru.k.kbook.features.product.list

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
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
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.features.dish.DishScreenTag
import ru.k.kbook.features.dish.create.DishCreateScreenTag
import ru.k.kbook.features.dish.models.DishUiTest
import ru.k.kbook.features.product.create.ProductCreateScreenTag
import ru.k.kbook.features.product.models.ProductUiTest
import ru.k.kbook.features.utils.clearAllProducts
import ru.k.kbook.features.utils.createProduct
import ru.k.kbook.features.utils.createDish
import ru.k.kbook.features.utils.clearAllDishes
import ru.k.kbook.features.utils.navigateToDish
import ru.k.kbook.features.utils.navigateToProduct

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
    fun emptyProducts() {
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
    fun getProductsWithFilterByCategory() {
        val productName = "Тестовый продукт"
        val category = ProductCategory.MEAT

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

            createProduct(
                ProductUiTest(
                    name = productName,
                    category = category,
                )
            )

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

            onNodeWithTag(ProductScreenTag.SCROLLABLE_COLUMN)
                .performScrollToNode(hasTestTag(ProductScreenTag.FILTERS_ROW))

            onNodeWithTag(ProductScreenTag.FILTERS_ROW)
                .performScrollToNode(hasTestTag("${ProductScreenTag.PREFIX}_${category.name}"))

            onNodeWithTag("${ProductScreenTag.PREFIX}_${category.name}").performClick()

            onNodeWithText(productName).assertExists()

            onNodeWithTag("${ProductScreenTag.PREFIX}_${category.name}").performClick()

            onNodeWithTag(ProductScreenTag.FILTERS_ROW)
                .performScrollToNode(hasTestTag("${ProductScreenTag.PREFIX}_${ProductCategory.VEGETABLES.name}"))

            onNodeWithTag("${ProductScreenTag.PREFIX}_${ProductCategory.VEGETABLES.name}").performClick()

            onNodeWithText(productName).assertDoesNotExist()
        }
    }

    @Test
    fun removeProductWhichUsedInDish() {
        val productName = "Тестовый продукт"
        val dishName = "Тестовое блюдо"

        with(composeTestRule) {
            waitUntilNodeCount(hasTestTag(ProductScreenTag.ADD_PRODUCT_FAB), count = 1, timeoutMillis = 5000)
            onNodeWithTag(ProductScreenTag.ADD_PRODUCT_FAB).performClick()

            waitUntilNodeCount(hasTestTag(ProductCreateScreenTag.NAME_INPUT), count = 1, timeoutMillis = 5000)

            createProduct(
                ProductUiTest(
                    name = productName,
                    category = ProductCategory.MEAT,
                    caloricity = 100.0,
                    protein = 10.0,
                    fat = 5.0,
                    carb = 5.0
                )
            )

            navigateToDish()

            waitUntilNodeCount(hasTestTag(DishScreenTag.CREATE_BUTTON), count = 1, timeoutMillis = 5000)
            onNodeWithTag(DishScreenTag.CREATE_BUTTON).performClick()

            waitUntilNodeCount(hasTestTag(DishCreateScreenTag.NAME_INPUT), count = 1, timeoutMillis = 5000)

            createDish(
                DishUiTest(
                    name = dishName,
                    caloricity = 250.0,
                    protein = 15.0,
                    fat = 10.0,
                    carb = 25.0,
                    composition = listOf(listOf(productName, 250.0)),
                    portionSize = 300.0
                ),
            )

            navigateToProduct()

            waitUntilNodeCount(hasTestTag(ProductScreenTag.CONTENT), count = 1, timeoutMillis = 5000)

            onNodeWithText(productName).assertExists()

            navigateToDish()

            waitUntilNodeCount(hasTestTag(DishScreenTag.CREATE_BUTTON), count = 1, timeoutMillis = 5000)

            clearAllDishes()

            navigateToProduct()

            waitUntilNodeCount(hasTestTag(ProductScreenTag.CONTENT), count = 1, timeoutMillis = 5000)

            clearAllProducts()
        }
    }
}

// ./gradlew :app:connectedDebugAndroidTest