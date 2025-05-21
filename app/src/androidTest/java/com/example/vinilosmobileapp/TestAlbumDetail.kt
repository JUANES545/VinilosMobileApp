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
class TestAlbumDetail {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testAlbumDetailView() {
        // Esperar a que se cargue la lista de álbumes
        Thread.sleep(3000)

        // Hacer clic en el primer álbum de la lista
        onView(withId(R.id.recycler_view_albums))
            .perform(
                androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )

        // Esperar a que se cargue la pantalla de detalle
        Thread.sleep(3000)

        // Verificar que el título del álbum está visible
        onView(withId(R.id.albumTitle))
            .check(matches(isDisplayed()))

        // Verificar que la imagen del álbum está visible
        onView(withId(R.id.albumImage))
            .check(matches(isDisplayed()))

        // Verificar que la descripción del álbum está visible
        onView(withId(R.id.albumDescription))
            .check(matches(isDisplayed()))
    }
}
