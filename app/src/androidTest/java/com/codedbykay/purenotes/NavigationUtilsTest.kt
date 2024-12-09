package com.codedbykay.purenotes

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.codedbykay.purenotes.testDispatchers.MainDispatcherRule
import com.codedbykay.purenotes.utils.handleEmptyBackNavigation
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class NavigationUtilsTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createTestNavGraph(navController: TestNavHostController): NavGraph {
        val navigatorProvider = navController.navigatorProvider

        val composeNavigator = ComposeNavigator()
        navigatorProvider.addNavigator(composeNavigator)

        val graph = NavGraph(NavGraphNavigator(navigatorProvider))
        val fallbackDestination =
            ComposeNavigator.Destination(composeNavigator) { /* No content */ }
        fallbackDestination.route = "FallbackRoute"
        fallbackDestination.id = 1

        val someOtherDestination =
            ComposeNavigator.Destination(composeNavigator) { /* No content */ }
        someOtherDestination.route = "SomeOtherRoute"
        someOtherDestination.id = 2

        graph.addDestination(fallbackDestination)
        graph.addDestination(someOtherDestination)
        graph.setStartDestination(2)
        return graph
    }

    @Test
    fun `handleEmptyBackNavigation navigates to fallback when back stack is empty`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val navController = TestNavHostController(context)

        // Run navigation graph setup on the UI thread
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navController.navigatorProvider.addNavigator(NavGraphNavigator(navController.navigatorProvider))
            navController.graph = createTestNavGraph(navController)
        }

        composeTestRule.setContent {
            CompositionLocalProvider {
                handleEmptyBackNavigation(navController, fallbackRoute = "FallbackRoute")
            }
        }

        // Wait for the UI thread to stabilize
        composeTestRule.waitForIdle()

        // Verify the fallback route
        assertEquals(
            "Expected route to be FallbackRoute after handling empty back stack",
            "FallbackRoute",
            navController.currentDestination?.route
        )
    }

    @Test
    fun `handleEmptyBackNavigation pops back stack if not empty`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val navController = TestNavHostController(context)

        // Run navigation graph setup on the UI thread
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navController.navigatorProvider.addNavigator(NavGraphNavigator(navController.navigatorProvider))
            navController.graph = createTestNavGraph(navController)
            navController.navigate("SomeOtherRoute") // Simulate back stack
        }

        composeTestRule.setContent {
            CompositionLocalProvider {
                handleEmptyBackNavigation(navController, fallbackRoute = "FallbackRoute")
            }
        }

        // Wait for the UI thread to stabilize
        composeTestRule.waitForIdle()

        // Verify the back stack was popped
        assertEquals(
            "Expected route to remain SomeOtherRoute when back stack is not empty",
            "SomeOtherRoute",
            navController.currentDestination?.route
        )
    }
}
