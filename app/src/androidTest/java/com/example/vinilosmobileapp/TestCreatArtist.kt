package com.example.vinilosmobileapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

class TestCreatArtist {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun clickCreateButton_opensCreateScreen() {


        onView(withId(R.id.bottomNavigationView))
            .perform(click())
        onView(withId(R.id.artistFragment))
            .perform(click())

        onView(withId(R.id.fab_add_artist))
            .perform(click())

        Thread.sleep(2000)
        onView(withId(R.id.etName)).perform(click(), replaceText("Artista de prueba"), closeSoftKeyboard())
        Thread.sleep(2000)
        onView(withId(R.id.etBirth)).perform(replaceText("1980-05-10"), closeSoftKeyboard())
        Thread.sleep(2000)
        onView(withId(R.id.etDesc)).perform(click(), replaceText("Descripción de prueba"), closeSoftKeyboard())
        Thread.sleep(2000)
        onView(withId(R.id.image_upload_container)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.btnCreateArtist)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.artistFragment)).check(matches(isDisplayed()))
        Thread.sleep(2000)

    }
}