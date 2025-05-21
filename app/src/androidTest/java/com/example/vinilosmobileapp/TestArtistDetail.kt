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
class TestArtistDetail {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testArtistDetailView() {
        // Esperar a que se cargue la pantalla principal
        Thread.sleep(3000)

        // Navegar a la sección de artistas
        onView(withId(R.id.navigation_artists))
            .perform(click())

        // Esperar a que se cargue la lista de artistas
        Thread.sleep(3000)

        // Hacer clic en el primer artista de la lista
        onView(withId(R.id.recycler_view_artists))
            .perform(
                androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )

        // Esperar a que se cargue la pantalla de detalle
        Thread.sleep(3000)

        // Verificar que el nombre del artista está visible
        onView(withId(R.id.artistName))
            .check(matches(isDisplayed()))

        // Verificar que la imagen del artista está visible
        onView(withId(R.id.artistImage))
            .check(matches(isDisplayed()))

        // Verificar que la descripción del artista está visible
        onView(withId(R.id.artistDescription))
            .check(matches(isDisplayed()))
    }
}
