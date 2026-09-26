package edu.ucb.project.feature.auth.login.presentation.state

data class LoginState(

    val username:String = "",

    val password:String = "",

    val isLoading:Boolean = false,

    val error:String? = null,

    val success:Boolean = false

)