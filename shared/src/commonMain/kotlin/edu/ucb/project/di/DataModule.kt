package edu.ucb.project.di

import edu.ucb.project.feature.auth.login.domain.repository.AuthRepository
import org.koin.dsl.module


val dataModule = module {


    single<AuthRepository>{

        object: AuthRepository {


            override suspend fun login(
                username:String,
                password:String
            ) = null


        }

    }


}