package com.aimodelaggregator

import android.app.Application
import com.aimodelaggregator.di.AppContainer
import com.aimodelaggregator.di.DefaultAppContainer

class AIModelAggregatorApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
