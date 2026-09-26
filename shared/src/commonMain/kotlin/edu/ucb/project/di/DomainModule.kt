package edu.ucb.project.di
import edu.ucb.project.feature.auth.login.domain.usecase.LoginUseCase
import org.koin.dsl.module


val domainModule = module {


    single {

        LoginUseCase(
            get()
        )

    }


}