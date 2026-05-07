//package ru.k.kbook.features.product.details
//
//import androidx.compose.ui.test.ExperimentalTestApi
//import androidx.compose.ui.test.assertIsDisplayed
//import androidx.compose.ui.test.hasTestTag
//import androidx.compose.ui.test.junit4.createAndroidComposeRule
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onNodeWithTag
//import androidx.compose.ui.test.onNodeWithText
//import androidx.compose.ui.test.performClick
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import org.junit.Rule
//import org.junit.Test
//import org.junit.Before
//import org.junit.BeforeEach
//import org.junit.runner.RunWith
//import ru.k.kbook.MainActivity
//import ru.k.kbook.features.product.list.ProductScreenTag
//import ru.k.kbook.ui.theme.KbookandroidTheme
//
//@OptIn(ExperimentalTestApi::class)
//@HiltAndroidTest
//@RunWith(AndroidJUnit4::class)
//class ProductDetailsScreenTest {
//
//    @get:Rule(order = 0)
//    var hiltRule = HiltAndroidRule(this)
//
//    @get:Rule(order = 1)
//    val composeTestRule = createAndroidComposeRule<MainActivity>()
//
//    @Before
//    fun init() {
//        hiltRule.inject()
//    }
//
//    @BeforeEach
//    fun beforeEach() {
//
//    }
//
//    @Test
//    fun testDetails() {
//        with(composeTestRule) {
//            waitUntilNodeCount(
//                hasTestTag(ProductScreenTag.ADD_PRODUCT_FAB),
//                count = 1,
//                timeoutMillis = 5000,
//            )
//            onNodeWithTag(ProductScreenTag.ADD_PRODUCT_FAB).performClick()
//        }
//    }
//}
//
//// ./gradlew :app:connectedDebugAndroidTest