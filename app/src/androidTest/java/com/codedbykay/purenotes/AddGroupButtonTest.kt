package com.codedbykay.purenotes

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.codedbykay.purenotes.R
import com.codedbykay.purenotes.components.AddGroupButton
import com.codedbykay.purenotes.viewModels.ToDoGroupViewModel
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class AddGroupButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addButton_displaysDialog_onClick() {
        // Mock the ViewModel
        val mockViewModel = mockk<ToDoGroupViewModel>(relaxed = true)

        // Set content
        composeTestRule.setContent {
            AddGroupButton(toDoGroupViewModel = mockViewModel)
        }

        // Assert "Add" button is displayed
        composeTestRule.onNodeWithContentDescription("Add Group").assertIsDisplayed()

        // Click the "Add" button
        composeTestRule.onNodeWithContentDescription("Add Group").performClick()

        // Assert the dialog appears
        composeTestRule.onNodeWithText("Add Group").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun addGroupButton_addsGroup_andClosesDialog() {
        // Mock the ViewModel
        val mockViewModel = mockk<ToDoGroupViewModel>(relaxed = true)

        // Set content
        composeTestRule.setContent {
            AddGroupButton(toDoGroupViewModel = mockViewModel)
        }

        // Click the "Add" button
        composeTestRule.onNodeWithContentDescription("Add Group").performClick()

        // Enter group name
        val inputField = composeTestRule.onNode(hasText("Enter group name") or hasSetTextAction())
        inputField.performTextInput("Test Group")

        // Click "Add"
        composeTestRule.onNodeWithText("Add").performClick()

        // Verify the ViewModel's addGroup method was called with correct parameters
        verify {
            mockViewModel.addGroup(name = "Test Group", createdAt = any())
        }

        // Assert dialog is dismissed
        composeTestRule.onNodeWithText("Add Group").assertDoesNotExist()
    }

    @Test
    fun addGroupButton_dismissesDialog_onCancel() {
        // Mock the ViewModel
        val mockViewModel = mockk<ToDoGroupViewModel>(relaxed = true)

        // Set content
        composeTestRule.setContent {
            AddGroupButton(toDoGroupViewModel = mockViewModel)
        }

        // Click the "Add" button
        composeTestRule.onNodeWithContentDescription("Add Group").performClick()

        // Click "Cancel"
        composeTestRule.onNodeWithText("Cancel").performClick()

        // Assert dialog is dismissed
        composeTestRule.onNodeWithText("Add Group").assertDoesNotExist()
    }
}
