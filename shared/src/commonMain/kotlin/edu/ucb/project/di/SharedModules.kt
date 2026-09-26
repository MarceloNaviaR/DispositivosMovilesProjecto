package edu.ucb.project.di

import org.koin.core.module.Module


fun sharedModules(): List<Module> {

    return listOf(
        dataModule,
        domainModule,
        presentationModule
    )

}