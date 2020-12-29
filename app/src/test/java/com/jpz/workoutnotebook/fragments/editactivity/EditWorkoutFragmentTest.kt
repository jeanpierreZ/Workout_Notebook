package com.jpz.workoutnotebook.fragments.editactivity

import android.os.Build
import com.jpz.workoutnotebook.activities.EditActivity
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class EditWorkoutFragmentTest {

    private var editActivity: EditActivity? = null
    private val editWorkoutFragment = EditWorkoutFragment()

    @Before
    fun setUp() {
        editActivity = Robolectric.buildActivity(EditActivity::class.java)
            .create()
            .start()
            .resume()
            .get()
    }

    @After
    fun tearDown() {
        // To avoid the IllegalStateException : A KoinContext is already started
        stopKoin()
    }

    @Test
    fun checkIfWorkoutNameIsEmptyTest() {
        val actual = editWorkoutFragment.checkIfWorkoutNameIsEmpty()
        // Assert true because there are no workoutName, no Workout
        Assert.assertTrue(actual)
    }

    @Test
    fun checkIfExercisesListIsEmptyTest() {
        val actual = editWorkoutFragment.checkIfExercisesListIsEmpty()
        // Assert true because there are no exercisesList, no Workout
        Assert.assertTrue(actual)
    }
}