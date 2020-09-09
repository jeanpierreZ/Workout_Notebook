package com.jpz.workoutnotebook.injections

import android.app.Application

class Injection {

    companion object {
        fun provideViewModelFactory(application: Application): ViewModelFactory {
            return ViewModelFactory(application)
        }
    }
}