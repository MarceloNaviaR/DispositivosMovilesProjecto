package edu.ucb.project.di

import org.koin.core.module.Module


fun sharedModules(): List<Module> {

    return listOf(
        platformModule(),
        dataModule,
        domainModule,
        presentationModule
    )

}