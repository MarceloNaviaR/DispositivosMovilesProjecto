package edu.ucb.project.di

import edu.ucb.project.feature.auth.login.presentation.viewmodel.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val presentationModule = module {


    viewModel {

        LoginViewModel(
            get()
        )

    }


}