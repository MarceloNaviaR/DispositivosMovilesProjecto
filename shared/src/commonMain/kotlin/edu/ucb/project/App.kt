package edu.ucb.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import edu.ucb.project.navigation.AppNavHost
import edu.ucb.project.roomastruth.presentation.screen.ConfigScreen

@Composable
fun App() {
    MaterialTheme {

        AppNavHost()

    }
}
