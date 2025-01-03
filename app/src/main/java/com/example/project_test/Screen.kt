package com.example.project_test

sealed class Screen(val route: String) {
    // Each screen inherits from the Screen class
    data object Settings : Screen(route = "settings_screen")
    data object Favorites : Screen(route = "favorites_screen")
    data object Demo : Screen(route = "demo_screen")

    data object Demo2 : Screen(route = "demo_screen2")

    data object Demo3 : Screen(route = "demo_screen3")

    //...

    // Build the args into a string
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}

