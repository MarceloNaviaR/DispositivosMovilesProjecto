package edu.ucb.project.di

import edu.ucb.project.core.database.AppDatabase
import edu.ucb.project.roomastruth.data.dao.ConfigDao
import edu.ucb.project.roomastruth.data.datasource.ConfigLocalDataSource
import edu.ucb.project.roomastruth.data.repository.ConfigRepositoryImpl
import edu.ucb.project.roomastruth.domain.repository.ConfigRepository
import edu.ucb.project.roomastruth.domain.usecase.AddConfigUseCase
import edu.ucb.project.roomastruth.domain.usecase.DeleteAllConfigsUseCase
import edu.ucb.project.roomastruth.domain.usecase.DeleteConfigUseCase
import edu.ucb.project.roomastruth.domain.usecase.ObserveConfigsUseCase
import edu.ucb.project.roomastruth.presentation.state.ConfigViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val configModule = module {

    single<ConfigDao> { get<AppDatabase>().configDao() }

    single { ConfigLocalDataSource(get()) }

    single<ConfigRepository> { ConfigRepositoryImpl(get()) }

    single { ObserveConfigsUseCase(get()) }
    single { AddConfigUseCase(get()) }
    single { DeleteConfigUseCase(get()) }
    single { DeleteAllConfigsUseCase(get()) }

    viewModel { ConfigViewModel(get(), get(), get(), get()) }
}