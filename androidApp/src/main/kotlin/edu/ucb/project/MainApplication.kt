package edu.ucb.project


import android.app.Application
import edu.ucb.project.di.initKoinAndroid


class MainApplication: Application(){


    override fun onCreate(){

        super.onCreate()

        initKoinAndroid(this)

    }

}