package edu.ucb.project.feature.auth.login.domain.repository

import edu.ucb.project.feature.auth.login.domain.model.User


interface AuthRepository {


    suspend fun login(
        username:String,
        password:String
    ): User?

}