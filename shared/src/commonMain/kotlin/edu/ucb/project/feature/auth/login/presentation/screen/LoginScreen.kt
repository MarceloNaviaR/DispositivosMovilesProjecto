package edu.ucb.project.feature.auth.login.presentation.screen


import androidx.compose.runtime.*
import androidx.navigation.NavHostController

import edu.ucb.project.feature.auth.login.presentation.composable.LoginContent
import edu.ucb.project.feature.auth.login.presentation.state.LoginEvent
import edu.ucb.project.feature.auth.login.presentation.state.LoginEffect
import edu.ucb.project.feature.auth.login.presentation.viewmodel.LoginViewModel

import edu.ucb.project.navigation.NavRoute



@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel
){


    val state by viewModel.state.collectAsState()



    LoginContent(


        username = state.username,


        password = state.password,


        onUsernameChange = {

            viewModel.onEvent(
                LoginEvent.UsernameChanged(it)
            )

        },


        onPasswordChange = {

            viewModel.onEvent(
                LoginEvent.PasswordChanged(it)
            )

        },


        onLoginClick = {

            viewModel.onEvent(
                LoginEvent.LoginClicked
            )

        }


    )



    LaunchedEffect(Unit) {


        viewModel.effect.collect { effect ->



            when(effect){

                null -> {
                    // No hacer nada
                }


                LoginEffect.NavigateHome -> {

                    navController.navigate(
                        NavRoute.Profile
                    )

                }


                is LoginEffect.ShowMessage -> {

                    println(effect.message)

                }

            }



        }



    }


}