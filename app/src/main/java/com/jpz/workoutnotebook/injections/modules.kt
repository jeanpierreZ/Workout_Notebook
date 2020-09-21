package com.jpz.workoutnotebook.injections

import com.jpz.workoutnotebook.api.*
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.ExerciseViewModel
import com.jpz.workoutnotebook.viewmodels.SetViewModel
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import com.jpz.workoutnotebook.viewmodels.WorkoutViewModel
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

val moduleUserStoragePhoto = module {
    // Instance of UserStoragePhoto
    single { UserStoragePhoto() }
}

val moduleUserViewModel = module {
    // Instance of UserViewModel
    viewModel { UserViewModel(get()) }
}

val moduleWorkoutHelper = module {
    // Instance of WorkoutHelper
    single { WorkoutHelper() }
}

val moduleWorkoutViewModel = module {
    // Instance of WorkoutViewModel
    viewModel { WorkoutViewModel(get()) }
}

val moduleExerciseHelper = module {
    // Instance of ExerciseHelper
    single { ExerciseHelper() }
}

val moduleExerciseViewModel = module {
    // Instance of ExerciseViewModel
    viewModel { ExerciseViewModel(get()) }
}

val moduleSetHelper = module {
    // Instance of SetHelper
    single { SetHelper() }
}

val moduleSetViewModel = module {
    // Instance of SetViewModel
    viewModel { SetViewModel(get()) }
}

val moduleMyUtils = module {
    // Instance of MyUtils
    single { MyUtils() }
}