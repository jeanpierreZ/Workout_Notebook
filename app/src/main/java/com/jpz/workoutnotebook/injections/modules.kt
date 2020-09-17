package com.jpz.workoutnotebook.injections

import com.jpz.workoutnotebook.api.UserAuth
import com.jpz.workoutnotebook.api.UserHelper
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val moduleUserAuth = module {
    // Instance of UserAuth
    single { UserAuth() }
}

val moduleUserHelper = module {
    // Instance of UserHelper
    single { UserHelper() }
}

val moduleUserViewModel = module {
    // Instance of UserViewModel
    viewModel { UserViewModel(get()) }
}

val moduleMyUtils = module {
    // Instance of MyUtils
    single { MyUtils() }
}