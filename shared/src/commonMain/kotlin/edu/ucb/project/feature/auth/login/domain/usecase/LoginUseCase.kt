package edu.ucb.project.feature.auth.login.domain.usecase

import edu.ucb.project.feature.auth.login.domain.model.User
import edu.ucb.project.feature.auth.login.domain.repository.AuthRepository


class LoginUseCase(
    private val repository: AuthRepository
){


    suspend operator fun invoke(
        username:String,
        password:String
    ): User? {


        return repository.login(
            username,
            password
        )

    }

}