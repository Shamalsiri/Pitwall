package com.ssiriwardana.pitwall.presentation.ui

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ssiriwardana.pitwall.domain.model.Driver
import com.ssiriwardana.pitwall.presentation.ui.screens.DriverScreen
import com.ssiriwardana.pitwall.presentation.ui.state.DriverUIState
import com.ssiriwardana.pitwall.presentation.ui.theme.PitwallTheme
import com.ssiriwardana.pitwall.presentation.viewModel.DriverViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RunWith(AndroidJUnit4::class)
class DriverScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel: DriverViewModel = mockk()
    private val uiStateFlow = MutableStateFlow(DriverUIState())

    init {
        every{mockViewModel.uiState} returns uiStateFlow
    }

    @Test
    fun validateProgressIndicationWhenLoading(){
        uiStateFlow.value = DriverUIState(isLoading = true)

        composeTestRule.setContent {
            PitwallTheme {
                DriverScreen(viewModel = mockViewModel)
            }
        }

        val indeterminateProgressMatcher = SemanticsMatcher("Has indeterminate progress") { node ->
            val rangeInfo = node.config.getOrNull(SemanticsProperties.ProgressBarRangeInfo)
            rangeInfo != null && rangeInfo.current == 0f && rangeInfo.range.start == 0f && rangeInfo.range.endInclusive == 0f
        }

        composeTestRule
            .onNode(indeterminateProgressMatcher)
            .assertExists()
    }

    @Test
    fun driversAreDisplayedInGrid(){
        val drivers = listOf(
            createTestDriver("VER", "Max", "Verstappen"),
            createTestDriver("RIC", "Daniel", "Riccardo"),
            createTestDriver("HAM", "Lewis", "Hamilton")
        )

        uiStateFlow.value = DriverUIState(drivers = drivers)

        composeTestRule.setContent {
            PitwallTheme {
                DriverScreen(viewModel = mockViewModel)
            }
        }

        composeTestRule.onNodeWithText("VER").assertIsDisplayed()
        composeTestRule.onNodeWithText("RIC").assertIsDisplayed()
        composeTestRule.onNodeWithText("HAM").assertIsDisplayed()
        composeTestRule.onNodeWithText("Max").assertIsDisplayed()
        composeTestRule.onNodeWithText("Daniel").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lewis").assertIsDisplayed()
        composeTestRule.onNodeWithText("Verstappen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Riccardo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hamilton").assertIsDisplayed()
    }

    @Test
    fun validateSearchFieldToggle() {
        uiStateFlow.value = DriverUIState()

        composeTestRule.setContent {
            PitwallTheme {
                DriverScreen(viewModel = mockViewModel)
            }
        }

        composeTestRule.onNodeWithText("").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Search").performClick()
        composeTestRule.onNodeWithText("Search drivers... ").assertIsDisplayed()

    }

    @Test
    fun validateErrorStateDisplaysRetry() {
        uiStateFlow.value = DriverUIState(
            error = "Network Error",
            drivers = emptyList()
        )

        composeTestRule.setContent {
            PitwallTheme {
                DriverScreen(viewModel = mockViewModel)
            }
        }
        composeTestRule.onNodeWithText("Error: Network Error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()

    }

    @Test
    fun validateInvalidSearchMessage() {
        val drivers = listOf(
            createTestDriver("VER", "Max", "Verstappen"),
            createTestDriver("RIC", "Daniel", "Riccardo"),
            createTestDriver("HAM", "Lewis", "Hamilton")
        )

        uiStateFlow.value = DriverUIState(
            drivers = drivers,
            searchQuery = "senna",
            filteredDrivers = emptyList()
        )

        composeTestRule.setContent {
            PitwallTheme {
                DriverScreen(viewModel = mockViewModel)
            }
        }

        composeTestRule.onNodeWithText("No drivers found matching \"senna\"").assertIsDisplayed()

    }

    private fun createTestDriver(
        code: String, firstName: String, lastName: String,
        teamName: String = "Red Bull Racing", wikiUrl: String? = null,
        number: String = "1"
    ) = Driver(
        id = code.lowercase(),
        permanentNumber = number,
        code = code,
        firstName = firstName,
        lastName = lastName,
        fullName = "$firstName $lastName",
        dateOfBirth = LocalDate.parse("8/8/1996", DateTimeFormatter.ofPattern("M/d/yyyy")),
        nationality = "Dutch",
        teamName = teamName,
        teamColor = "3671C6",
        headshotUrl = null,
        wikiUrl = null
    )
}