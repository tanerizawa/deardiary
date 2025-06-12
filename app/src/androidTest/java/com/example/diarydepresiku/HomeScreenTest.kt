package com.example.diarydepresiku

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun defaultArticlesShownOnHome() {
        composeRule.onNodeWithText("Mindfulness Basics").assertIsDisplayed()
    }
}
