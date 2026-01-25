package com.example.yugved4.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

/**
 * Theme Helper Utility
 * Manages dark/light theme preferences and application
 */
object ThemeHelper {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME_MODE = "theme_mode"
    
    // Theme mode constants
    const val THEME_LIGHT = 0
    const val THEME_DARK = 1
    const val THEME_SYSTEM = 2
    
    /**
     * Get SharedPreferences instance
     */
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Save theme preference
     * @param context Application context
     * @param themeMode THEME_LIGHT, THEME_DARK, or THEME_SYSTEM
     */
    fun saveTheme(context: Context, themeMode: Int) {
        getPrefs(context).edit().putInt(KEY_THEME_MODE, themeMode).apply()
    }
    
    /**
     * Get saved theme preference
     * @param context Application context
     * @return Saved theme mode (defaults to THEME_SYSTEM)
     */
    fun getTheme(context: Context): Int {
        return getPrefs(context).getInt(KEY_THEME_MODE, THEME_SYSTEM)
    }
    
    /**
     * Check if dark mode is currently active
     * @param context Application context
     * @return true if dark mode is enabled, false otherwise
     */
    fun isDarkMode(context: Context): Boolean {
        return when (getTheme(context)) {
            THEME_DARK -> true
            THEME_LIGHT -> false
            else -> {
                // THEME_SYSTEM - check system setting
                val nightMode = context.resources.configuration.uiMode and 
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK
                nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }
    }
    
    /**
     * Apply theme to app
     * @param context Application context
     */
    fun applyTheme(context: Context) {
        val themeMode = getTheme(context)
        applyThemeMode(themeMode)
    }
    
    /**
     * Apply specific theme mode
     * @param themeMode THEME_LIGHT, THEME_DARK, or THEME_SYSTEM
     */
    fun applyThemeMode(themeMode: Int) {
        when (themeMode) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
    
    /**
     * Toggle between light and dark mode (ignores system theme)
     * @param context Application context
     */
    fun toggleTheme(context: Context) {
        val currentTheme = getTheme(context)
        val newTheme = if (isDarkMode(context)) THEME_LIGHT else THEME_DARK
        saveTheme(context, newTheme)
        applyThemeMode(newTheme)
    }
}
