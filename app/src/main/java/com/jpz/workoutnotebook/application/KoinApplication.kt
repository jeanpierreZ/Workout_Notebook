package com.jpz.workoutnotebook.application

import android.app.Application
import com.jpz.workoutnotebook.injections.*
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
            modules(
                listOf(
                    moduleUserAuth,
                    moduleUserHelper,
                    moduleUserStoragePhoto,
                    moduleUserViewModel,
                    moduleWorkoutHelper,
                    moduleWorkoutViewModel,
                    moduleExerciseHelper,
                    moduleExerciseViewModel,
                    moduleMyUtils
                )
            )
        }
    }
}