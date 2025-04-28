package com.example.vinilosmobileapp
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers.allOf
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testNavigationAndTopBarTitle() {
        // Verificar que dentro del TopAppBar hay un TextView con el título "Vinilos"
        onView(
            allOf(
                isDescendantOfA(withId(R.id.topAppBar)), // esté dentro del TopAppBar
                withText("Vinilos"),                     // el texto esperado
                isAssignableFrom(android.widget.TextView::class.java) // sea un TextView
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToArtists_updatesTopBarTitle() {
        onView(withId(R.id.bottomNavigationView))
            .perform(clickOnItem(R.id.artistFragment))
        onView(withId(R.id.topAppBar))
            .check(matches(hasDescendant(withText("Artistas"))))
    }

    @Test
    fun navigateToFavorites_updatesTopBarTitle() {
        onView(withId(R.id.bottomNavigationView))
            .perform(clickOnItem(R.id.favoritesFragment))
        onView(withId(R.id.topAppBar))
            .check(matches(hasDescendant(withText("Favoritos"))))
    }

    @Test
    fun navigateToProfile_updatesTopBarTitle() {
        onView(withId(R.id.bottomNavigationView))
            .perform(clickOnItem(R.id.profileFragment))
        onView(withId(R.id.topAppBar))
            .check(matches(hasDescendant(withText("Perfil"))))
    }

    private fun clickOnItem(itemId: Int) = object : ViewAction {
        override fun getConstraints() = isAssignableFrom(com.google.android.material.bottomnavigation.BottomNavigationView::class.java)

        override fun getDescription() = "Click on BottomNavigationView item with id: $itemId"

        override fun perform(uiController: androidx.test.espresso.UiController?, view: android.view.View?) {
            val bottomNavigationView = view as com.google.android.material.bottomnavigation.BottomNavigationView
            bottomNavigationView.selectedItemId = itemId
        }
    }


}