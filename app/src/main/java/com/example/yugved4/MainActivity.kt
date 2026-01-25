package com.example.yugved4

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.yugved4.database.DatabaseHelper
import com.example.yugved4.databinding.ActivityMainBinding
import com.example.yugved4.utils.AuthHelper
import com.example.yugved4.utils.ThemeHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved theme before super.onCreate
        ThemeHelper.applyTheme(this)
        
        super.onCreate(savedInstanceState)
        
        // Initialize database helper
        dbHelper = DatabaseHelper(this)
        
        // Check authentication status
        if (!AuthHelper.isUserSignedIn()) {
            // User not signed in - redirect to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        // Check if user has completed profile
        if (!dbHelper.hasUserProfile()) {
            // User signed in but incomplete profile - redirect to onboarding
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
        
        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Navigation
        setupNavigation()
    }

    private fun setupNavigation() {
        // Get the NavHostFragment and NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Link BottomNavigationView with NavController
        binding.bottomNavigation.setupWithNavController(navController)
    }
}