package edu.ucb.project.feature.auth.login.presentation.state
sealed class LoginEffect {


    object NavigateHome:LoginEffect()


    data class ShowMessage(
        val message:String
    ):LoginEffect()


}