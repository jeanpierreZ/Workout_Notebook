package com.jpz.workoutnotebook.application

import android.app.Application
import com.jpz.workoutnotebook.injections.moduleMyUtils
import com.jpz.workoutnotebook.injections.moduleUserAuth
import com.jpz.workoutnotebook.injections.moduleUserHelper
import com.jpz.workoutnotebook.injections.moduleUserViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KoinApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // start Koin
        startKoin {
            // Android context
            androidContext(this@KoinApplication)
            // modules
            modules(listOf(moduleUserAuth, moduleUserHelper, moduleUserViewModel, moduleMyUtils))
        }
    }
}