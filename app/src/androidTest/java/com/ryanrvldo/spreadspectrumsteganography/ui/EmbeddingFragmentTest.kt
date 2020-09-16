package com.ryanrvldo.spreadspectrumsteganography.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.ryanrvldo.spreadspectrumsteganography.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class EmbeddingFragmentTest {


    @get:Rule
    val activityRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    @Test
    fun test_fragment_view() {
        onView(withId(R.id.btn_begin))
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withId(R.id.et_message))
            .check(matches(isDisplayed()))
            .check(matches(withHint(R.string.select_your_message)))
        onView(withId(R.id.btn_choose_message))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.choose_message)))
        onView(withId(R.id.tv_file_title))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.audio_file)))
        onView(withId(R.id.et_file_path))
            .check(matches(isDisplayed()))

        onView(withId(R.id.tv_key_title))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.key)))
        onView(withId(R.id.et_key_a))
            .check(matches(isDisplayed()))
            .check(matches(withHint(R.string.a)))
        onView(withId(R.id.et_key_b))
            .check(matches(isDisplayed()))
            .check(matches(withHint(R.string.b)))
        onView(withId(R.id.et_key_c0))
            .check(matches(isDisplayed()))
            .check(matches(withHint(R.string.c0)))
        onView(withId(R.id.et_key_x0))
            .check(matches(isDisplayed()))
            .check(matches(withHint(R.string.x0)))

        onView(withId(R.id.btn_random_key))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.random)))
        onView(withId(R.id.btn_embed))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.process)))
    }

    @Test
    fun test_navigate_to_HomeFragment() {
        onView(withId(R.id.btn_begin))
            .check(matches(isDisplayed()))
            .perform(click())
        pressBack()
        onView(withId(R.id.tv_title))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.app_title)))
    }
}