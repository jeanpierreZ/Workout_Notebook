package com.jpz.workoutnotebook.injections

import com.jpz.workoutnotebook.repositories.*
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.ExerciseViewModel
import com.jpz.workoutnotebook.viewmodels.TrainingSessionViewModel
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
    single { UserRepository() }
}

val moduleUserStoragePhoto = module {
    // Instance of UserStoragePhoto
    single { UserStoragePhoto() }
}

val moduleUserViewModel = module {
    // Instance of UserViewModel
    viewModel { UserViewModel(get()) }
}

val moduleTrainingSessionHelper = module {
    // Instance of TrainingHelper
    single { TrainingSessionRepository() }
}

val moduleTrainingSessionViewModel = module {
    // Instance of TrainingViewModel
    viewModel { TrainingSessionViewModel(get()) }
}

val moduleWorkoutHelper = module {
    // Instance of WorkoutHelper
    single { WorkoutRepository() }
}

val moduleWorkoutViewModel = module {
    // Instance of WorkoutViewModel
    viewModel { WorkoutViewModel(get()) }
}

val moduleExerciseHelper = module {
    // Instance of ExerciseHelper
    single { ExerciseRepository() }
}

val moduleExerciseViewModel = module {
    // Instance of ExerciseViewModel
    viewModel { ExerciseViewModel(get()) }
}

val moduleMyUtils = module {
    // Instance of MyUtils
    single { MyUtils() }
}