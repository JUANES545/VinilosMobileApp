package com.example.vinilosmobileapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.vinilosmobileapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val topLevelDestinations = setOf(
        R.id.homeFragment,
        R.id.artistFragment,
        R.id.favoritesFragment,
        R.id.profileFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set default night mode to light
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Bottom navigation setup
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        // Change title and show/hide back arrow
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.topAppBar.title = when (destination.id) {
                R.id.homeFragment -> getString(R.string.title_vinilos)
                R.id.artistFragment -> getString(R.string.title_artists)
                R.id.favoritesFragment -> getString(R.string.title_favorites)
                R.id.profileFragment -> getString(R.string.title_profile)
                else -> getString(R.string.app_name)
            }

            // Show back arrow if NOT on a top-level destination
            val showBackArrow = destination.id !in topLevelDestinations
            supportActionBar?.setDisplayHomeAsUpEnabled(showBackArrow)

            if (showBackArrow) {
                binding.topAppBar.setNavigationOnClickListener {
                    navController.navigateUp()
                }
            } else {
                binding.topAppBar.navigationIcon = null
            }
        }
    }
}
