package edu.ucb.project.feature.auth.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.ucb.project.feature.auth.login.domain.usecase.LoginUseCase
import edu.ucb.project.feature.auth.login.presentation.state.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class LoginViewModel(
    private val loginUseCase: LoginUseCase
):ViewModel(){



    private val _state =
        MutableStateFlow(LoginState())


    val state =
        _state.asStateFlow()



    private val _effect =
        MutableStateFlow<LoginEffect?>(null)


    val effect =
        _effect.asStateFlow()



    fun onEvent(
        event:LoginEvent
    ){


        when(event){


            is LoginEvent.UsernameChanged -> {

                _state.value =
                    _state.value.copy(
                        username = event.username
                    )

            }



            is LoginEvent.PasswordChanged -> {

                _state.value =
                    _state.value.copy(
                        password = event.password
                    )

            }



            LoginEvent.LoginClicked -> {

                login()

            }


        }


    }



    private fun login(){


        viewModelScope.launch {


            _state.value =
                _state.value.copy(
                    isLoading = true
                )


            val result =
                loginUseCase(
                    _state.value.username,
                    _state.value.password
                )


            if(result != null){


                _effect.value =
                    LoginEffect.NavigateHome



            }else{


                _effect.value =
                    LoginEffect.ShowMessage(
                        "Usuario incorrecto"
                    )


            }



            _state.value =
                _state.value.copy(
                    isLoading = false
                )


        }



    }



}