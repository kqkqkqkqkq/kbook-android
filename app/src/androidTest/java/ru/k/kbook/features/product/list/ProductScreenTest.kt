package ru.k.kbook.features.product.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.Before
import org.junit.runner.RunWith
import ru.k.kbook.MainActivity
import ru.k.kbook.ui.theme.KbookandroidTheme

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
    }

    @Test
    fun myTest() {
        with(composeTestRule) {
            onNodeWithTag(ProductScreenTag.LOADING).assertIsDisplayed()
        }
    }
}

// ./gradlew :app:connectedDebugAndroidTest