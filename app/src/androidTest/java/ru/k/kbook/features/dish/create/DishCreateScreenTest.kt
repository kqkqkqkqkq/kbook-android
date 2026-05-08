package ru.k.kbook.features.dish.create

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
import ru.k.kbook.api.grpc.schema.DishCategory
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.features.dish.DishScreenTag
import ru.k.kbook.features.dish.models.DishUiTest
import ru.k.kbook.features.product.create.ProductCreateScreenTag
import ru.k.kbook.features.product.list.ProductScreenTag
import ru.k.kbook.features.product.models.ProductUiTest
import ru.k.kbook.navigation.NavigationTag
import ru.k.kbook.features.utils.clearAllProducts
import ru.k.kbook.features.utils.createProduct
import ru.k.kbook.features.utils.createDish
import ru.k.kbook.features.utils.clearAllDishes
import ru.k.kbook.features.utils.navigateToDish
import ru.k.kbook.features.utils.navigateToProduct

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DishCreateScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val testProductName = "Test product for dish"

    @Before
    fun before() {
        hiltRule.inject()
        with(composeTestRule) {
            waitUntilNodeCount(
                hasTestTag(ProductScreenTag.ADD_PRODUCT_FAB),
                count = 1,
                timeoutMillis = 15_000,
            )

            clearAllProducts()

            onNodeWithTag(ProductScreenTag.ADD_PRODUCT_FAB).performClick()

            waitUntilNodeCount(
                hasTestTag(ProductCreateScreenTag.NAME_INPUT),
                count = 1,
                timeoutMillis = 5000,
            )

            createProduct(
                ProductUiTest(
                    name = testProductName,
                    caloricity = 100.0,
                    protein = 10.0,
                    fat = 5.0,
                    carb = 5.0,
                    category = ProductCategory.MEAT
                )
            )

            waitUntilNodeCount(
                hasTestTag(ProductScreenTag.CONTENT),
                count = 1,
                timeoutMillis = 10_000,
            )

            navigateToDish()

            waitUntilNodeCount(
                hasTestTag(DishScreenTag.CREATE_BUTTON),
                count = 1,
                timeoutMillis = 10_000,
            )

            clearAllDishes()

            onNodeWithTag(DishScreenTag.CREATE_BUTTON).performClick()

            waitUntilNodeCount(
                hasTestTag(DishCreateScreenTag.NAME_INPUT),
                count = 1,
                timeoutMillis = 5_000,
            )
        }
    }

    @After
    fun after() {
        with(composeTestRule) {
            waitUntilNodeCount(
                hasTestTag(DishScreenTag.CONTENT),
                count = 1,
                timeoutMillis = 5000,
            )

            clearAllDishes()

            navigateToProduct()

            waitUntilNodeCount(
                hasTestTag(ProductScreenTag.ADD_PRODUCT_FAB),
                count = 1,
                timeoutMillis = 10_000,
            )

            clearAllProducts()
        }
    }

    @Test
    fun createCorrectDishWithAutoCalculate() {
        with(composeTestRule) {
            val dishName = "Борщ тестовый"

            with(composeTestRule) {
                waitUntilNodeCount(
                    hasTestTag(DishCreateScreenTag.NAME_INPUT),
                    count = 1,
                    timeoutMillis = 15_000,
                )

                createDish(
                    DishUiTest(
                        name = dishName,
                        category = DishCategory.FIRST,
                        caloricity = 250.0,
                        protein = 15.0,
                        fat = 10.0,
                        carb = 25.0,
                        composition = listOf(listOf(testProductName, 250.0)),
                        images = listOf("https://test.com/dish.jpg"),
                        portionSize = 300.0
                    )
                )

                waitUntilNodeCount(
                    hasText(dishName),
                    count = 1,
                    timeoutMillis = 10_000,
                )

                onNodeWithText(dishName).assertExists()
            }
        }
    }

    @Test
    fun checkMacrosRemovingFromTheTitle() {
        with(composeTestRule) {
            val dishName = "Борщ тестовый !первое"

            with(composeTestRule) {
                waitUntilNodeCount(
                    hasTestTag(DishCreateScreenTag.NAME_INPUT),
                    count = 1,
                    timeoutMillis = 15_000,
                )

                createDish(
                    DishUiTest(
                        name = dishName,
                        caloricity = 250.0,
                        protein = 15.0,
                        fat = 10.0,
                        carb = 25.0,
                        composition = listOf(listOf(testProductName, 250.0)),
                        images = listOf("https://test.com/dish.jpg"),
                        portionSize = 300.0
                    )
                )

                waitUntilNodeCount(
                    hasText("Борщ тестовый"),
                    count = 1,
                    timeoutMillis = 10_000,
                )

                onNodeWithText("Борщ тестовый").assertExists()
            }
        }
    }

    @Test
    fun createCorrectDishWithCalculateByHand() {
        with(composeTestRule) {
            val dishName = "Борщ тестовый"

            with(composeTestRule) {
                waitUntilNodeCount(
                    hasTestTag(DishCreateScreenTag.NAME_INPUT),
                    count = 1,
                    timeoutMillis = 15_000,
                )

                createDish(
                    DishUiTest(
                        name = dishName,
                        category = DishCategory.FIRST,
                        caloricity = 250.0,
                        protein = 15.0,
                        fat = 10.0,
                        carb = 25.0,
                        composition = listOf(listOf(testProductName, 250.0)),
                        images = listOf("https://test.com/dish.jpg"),
                        portionSize = 300.0
                    ), false
                )

                waitUntilNodeCount(
                    hasText(dishName),
                    count = 1,
                    timeoutMillis = 10_000,
                )

                onNodeWithText(dishName).assertExists()
            }
        }
    }

    @Test
    fun createDishWithMoreThanFiveImages() {
        with(composeTestRule) {
            val dishName = "Борщ тестовый"
            val url = "https://test.com/dish.jpg"

            with(composeTestRule) {
                waitUntilNodeCount(
                    hasTestTag(DishCreateScreenTag.NAME_INPUT),
                    count = 1,
                    timeoutMillis = 15_000,
                )

                createDish(
                    DishUiTest(
                        name = dishName,
                        category = DishCategory.FIRST,
                        caloricity = 250.0,
                        protein = 15.0,
                        fat = 10.0,
                        carb = 25.0,
                        composition = listOf(listOf(testProductName, 250.0)),
                        images = listOf(url, url, url, url, url, url),
                        portionSize = 300.0
                    ), true
                )

                onNodeWithTag(DishCreateScreenTag.SNACKBAR).assertExists()
                onNodeWithTag(DishCreateScreenTag.BACK_BUTTON).performClick()
            }
        }
    }
}
