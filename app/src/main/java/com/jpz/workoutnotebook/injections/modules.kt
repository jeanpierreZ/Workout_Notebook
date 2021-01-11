package com.jpz.workoutnotebook.injections

import com.jpz.workoutnotebook.notifications.Notification
import com.jpz.workoutnotebook.repositories.*
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val moduleUserAuth = module {
    // Instance of UserAuth
    single { UserAuth() }
}

val moduleUserRepository = module {
    // Instance of UserRepository
    single { UserRepository() }
}

val moduleUserStoragePhoto = module {
    // Instance of UserStoragePhoto
    single { UserStoragePhoto() }
}

val moduleUserViewModel = module {
    // Instance of UserViewModel
    viewModel { UserViewModel(get(), get(), get()) }
}

val moduleTrainingSessionRepository = module {
    // Instance of TrainingSessionRepository
    single { TrainingSessionRepository() }
}

val moduleTrainingSessionViewModel = module {
    // Instance of TrainingSessionViewModel
    viewModel { TrainingSessionViewModel(get(), get()) }
}

val moduleWorkoutRepository = module {
    // Instance of WorkoutRepository
    single { WorkoutRepository() }
}

val moduleWorkoutViewModel = module {
    // Instance of WorkoutViewModel
    viewModel { WorkoutViewModel(get(), get(), get()) }
}

val moduleExerciseRepository = module {
    // Instance of ExerciseRepository
    single { ExerciseRepository() }
}

val moduleExerciseViewModel = module {
    // Instance of ExerciseViewModel
    viewModel { ExerciseViewModel(get(), get(), get()) }
}

val moduleFollowRepository = module {
    // Instance of FollowRepository
    single { FollowRepository() }
}

val moduleFollowViewModel = module {
    // Instance of FollowViewModel
    viewModel { FollowViewModel(get(), get()) }
}

val moduleFollowingRepository = module {
    // Instance of FollowingRepository
    single { FollowingRepository() }
}

val moduleFollowingViewModel = module {
    // Instance of FollowingViewModel
    viewModel { FollowingViewModel(get(), get()) }
}

val moduleMyUtils = module {
    // Instance of MyUtils
    single { MyUtils() }
}

val moduleNotification = module {
    // Instance of Notification
    single { Notification() }
}