package com.example.coffeedemo1

import android.app.Application
import android.content.res.Configuration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onConfigurationChanged (newConfig : Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }
}