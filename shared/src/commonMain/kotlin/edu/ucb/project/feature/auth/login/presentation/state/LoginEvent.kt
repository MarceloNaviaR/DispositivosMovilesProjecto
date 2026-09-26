package edu.ucb.project.feature.auth.login.presentation.state
sealed class LoginEvent {


    data class UsernameChanged(
        val username:String
    ):LoginEvent()



    data class PasswordChanged(
        val password:String
    ):LoginEvent()



    object LoginClicked:LoginEvent()

}