package com.example.vinilosmobileapp
import androidx.compose.ui.test.isRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestValErrorAlbum {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)
    @Test
    fun clickCreateAlbum_ViewError() {
        onView(withId(R.id.fab_create_album))
            .perform(click())

        Thread.sleep(3000)

        onView(withId(R.id.button_create_album))
            .perform(click())

        Thread.sleep(3000)

        onView(withId(R.id.input_album_year))
            .equals(matches(hasErrorText("Este campo es obligatorio")))

        onView(withId(R.id.dropdown_genre_layout))
            .equals(matches(hasErrorText("Selecciona un género")))

        onView(withId(R.id.dropdown_artist_layout))
            .equals(matches(hasErrorText("Selecciona un sello discográfico")))


    }


}


