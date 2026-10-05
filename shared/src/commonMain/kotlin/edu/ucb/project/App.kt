package edu.ucb.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import edu.ucb.project.navigation.AppNavHost

@Composable
fun App() {
    MaterialTheme {
        AppNavHost()

    }
}
