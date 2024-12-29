package com.example.project_test.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Manages switching from light mode to dark mode
object ThemeManager {
    var colorTheme by mutableStateOf(false)
        private set

    // Initialises the color theme from persistent storage
    fun intiColorTheme(context: Context) {
        val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        colorTheme = sharedPreferences.getBoolean("colorTheme", false)
    }

    // Toggle color theme and update persistent variable
    fun toggleTheme(context: Context) {
        colorTheme = !colorTheme

        val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("colorTheme", colorTheme)
        editor.apply()
    }
}