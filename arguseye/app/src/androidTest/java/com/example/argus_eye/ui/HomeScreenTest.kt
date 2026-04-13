package com.example.argus_eye.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.argus_eye.data.model.ContactModel
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysWelcomeMessage() {
        composeTestRule.setContent {
            HomeScreen(
                unlabeledPeople = emptyList(),
                isLoading = false,
                error = null,
                user = null,
                onRetry = {},
                onLabel = { _, _ -> },
                onDismiss = {},
                onRefresh = {}
            )
        }

        composeTestRule.onNodeWithText("Welcome User!").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysErrorAndRetryButton() {
        val errorMessage = "Failed to fetch"
        composeTestRule.setContent {
            HomeScreen(
                unlabeledPeople = emptyList(),
                isLoading = false,
                error = errorMessage,
                user = null,
                onRetry = {},
                onLabel = { _, _ -> },
                onDismiss = {},
                onRefresh = {}
            )
        }

        composeTestRule.onNodeWithText("Error: $errorMessage").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun homeCard_saveButtonDisabledWhenNameIsEmpty() {
        val person = ContactModel(id = 1, name = "Unknown")
        composeTestRule.setContent {
            HomeCard(
                person = person,
                onLabel = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("SAVE NAME").assertIsNotEnabled()
    }

    @Test
    fun homeCard_saveButtonEnabledWhenNameIsEntered() {
        val person = ContactModel(id = 1, name = "Unknown")
        composeTestRule.setContent {
            HomeCard(
                person = person,
                onLabel = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Enter name (e.g. Peter)").performTextInput("John")
        composeTestRule.onNodeWithText("SAVE NAME").assertIsEnabled()
    }

    @Test
    fun homeCard_clickingSaveCallsCallback() {
        val person = ContactModel(id = 1, name = "Unknown")
        var capturedName = ""
        composeTestRule.setContent {
            HomeCard(
                person = person,
                onLabel = { capturedName = it },
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Enter name (e.g. Peter)").performTextInput("Alice")
        composeTestRule.onNodeWithText("SAVE NAME").performClick()

        assert(capturedName == "Alice")
    }
}
