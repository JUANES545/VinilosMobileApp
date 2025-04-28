package com.example.vinilosmobileapp
import androidx.compose.ui.test.isRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestAlbumView {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)
    @Test
    fun clickCreateButton_opensCreateScreen() {
        onView(withId(R.id.fab_create_album))
            .perform(click())

        Thread.sleep(5000)
        //Validaciones de algunos componentes al darle click
        onView(withId(R.id.input_album_name_layout))
            .check(matches(isDisplayed()))

        onView(withId(R.id.input_album_year_layout))
            .check(matches(isDisplayed()))

        onView(withId(R.id.button_create_album))
            .check(matches(isDisplayed()))

    }


}