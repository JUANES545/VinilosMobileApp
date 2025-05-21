package com.example.vinilosmobileapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vinilosmobileapp.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestAlbumCreate {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testAlbumCreateView() {
        // Hacer clic en el botón de creación de álbum
        onView(withId(R.id.fab_create_album))
            .perform(click())

        // Esperar a que se cargue la pantalla de creación
        Thread.sleep(3000)

        // Verificar que el campo de nombre del álbum está visible
        onView(withId(R.id.input_album_name_layout))
            .check(matches(isDisplayed()))

        // Verificar que el campo de año del álbum está visible
        onView(withId(R.id.input_album_year_layout))
            .check(matches(isDisplayed()))

        // Verificar que el botón de creación está visible
        onView(withId(R.id.button_create_album))
            .check(matches(isDisplayed()))
    }
}
