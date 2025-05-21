package com.example.vinilosmobileapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vinilosmobileapp.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestAlbumComment {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testAddCommentToAlbum() {
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

        // Hacer clic en el botón de agregar comentario
        onView(withId(R.id.buttonAddComment))
            .perform(click())

        // Esperar a que se muestre el diálogo
        Thread.sleep(1000)

        // Ingresar el comentario
        onView(withId(R.id.inputComment))
            .perform(typeText("Excelente álbum"), closeSoftKeyboard())

        // Seleccionar un autor (asumiendo que hay una lista desplegable)
        onView(withId(R.id.inputAuthor))
            .perform(click())
        onView(withText("Melómano Anónimo"))
            .perform(click())

        // Confirmar la adición del comentario
        onView(withText("Agregar"))
            .perform(click())

        // Esperar a que se actualice la lista de comentarios
        Thread.sleep(3000)

        // Verificar que el nuevo comentario aparece en la lista
        onView(withText("Excelente álbum"))
            .check(matches(isDisplayed()))
    }
}
