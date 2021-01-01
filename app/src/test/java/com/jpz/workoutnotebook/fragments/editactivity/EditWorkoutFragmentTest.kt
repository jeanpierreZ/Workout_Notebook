package com.jpz.workoutnotebook.fragments.editactivity

import android.os.Build
import com.jpz.workoutnotebook.activities.EditActivity
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Workout
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class EditWorkoutFragmentTest {

    private var editActivity: EditActivity? = null
    private val editCalendarFragment = EditCalendarFragment()

    private val mockedEditWorkoutFragment = Mockito.mock(EditWorkoutFragment::class.java)
    private val mockedWorkout = Mockito.mock(Workout::class.java)

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
        Mockito.`when`(mockedWorkout.workoutName).thenReturn("workoutName")
        val actual = mockedEditWorkoutFragment.checkIfWorkoutNameIsEmpty()
        Assert.assertFalse(actual)
    }

    @Test
    fun checkIfExercisesListIsEmptyTest() {
        Mockito.`when`(mockedWorkout.exercisesList).thenReturn(arrayListOf(Exercise("id")))
        val actual = mockedEditWorkoutFragment.checkIfExercisesListIsEmpty()
        Assert.assertFalse(actual)
    }
}