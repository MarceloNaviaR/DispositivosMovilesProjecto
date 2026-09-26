package edu.ucb.project.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import org.koin.compose.viewmodel.koinViewModel
import edu.ucb.project.feature.auth.login.presentation.viewmodel.LoginViewModel
import edu.ucb.project.feature.auth.login.presentation.screen.LoginScreen



@Composable
fun AppNavHost() {


    val navController = rememberNavController()



    NavHost(
        navController = navController,
        startDestination = NavRoute.Login
    ) {



        composable<NavRoute.Login> {


            val viewModel = koinViewModel<LoginViewModel>()


            LoginScreen(

                navController = navController,

                viewModel = viewModel

            )


        }

    }

}