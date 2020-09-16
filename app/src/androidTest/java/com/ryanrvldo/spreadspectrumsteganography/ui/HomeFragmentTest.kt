package com.ryanrvldo.spreadspectrumsteganography.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.ryanrvldo.spreadspectrumsteganography.R
import com.ryanrvldo.spreadspectrumsteganography.helper.ImageViewHasDrawableMatcher.hasDrawable
import com.ryanrvldo.spreadspectrumsteganography.helper.initNavController
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @Test
    fun test_fragment_view() {
        initNavController<HomeFragment>()

        onView(withId(R.id.img_banner))
            .check(matches(hasDrawable()))
        onView(withId(R.id.tv_title))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.app_title)))
        onView(withId(R.id.tv_subtitle))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.app_subtitle)))
        onView(withId(R.id.btn_begin))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.let_begin)))
        onView(withId(R.id.btn_help))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.need_help_click_here)))
    }

    @Test
    fun test_navigate_to_embeddingFragment() {
        val navController = initNavController<HomeFragment>()

        onView(withId(R.id.btn_begin))
            .check(matches(isDisplayed()))
            .perform(click())

        val backStack = navController.backStack
        val currentDestination = backStack.last()
        assertThat(currentDestination.destination.id).isEqualTo(R.id.nav_embedding)
    }

    @Test
    fun test_navigate_to_helpFragment() {
        val navController = initNavController<HomeFragment>()

        onView(withId(R.id.btn_help))
            .check(matches(isDisplayed()))
            .perform(click())

        val backStack = navController.backStack
        val currentDestination = backStack.last()
        assertThat(currentDestination.destination.id).isEqualTo(R.id.nav_help)
    }
}