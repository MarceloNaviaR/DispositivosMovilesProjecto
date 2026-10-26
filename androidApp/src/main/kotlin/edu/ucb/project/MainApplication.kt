package edu.ucb.project

import android.app.Application
import edu.ucb.project.di.initKoinAndroid
import edu.ucb.project.feature.notification.AndroidNotificationService

class MainApplication: Application(){

    override fun onCreate(){
        super.onCreate()
        initKoinAndroid(this)

        AndroidNotificationService(this).createNotificationChannel()
    }
}