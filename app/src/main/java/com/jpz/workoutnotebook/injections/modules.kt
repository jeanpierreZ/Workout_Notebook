package com.jpz.workoutnotebook.injections

import com.jpz.workoutnotebook.viewmodels.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val myModule = module {
    // Instance of UserViewModel
    viewModel { UserViewModel(get()) }
}