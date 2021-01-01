package com.jpz.workoutnotebook.fragments.editactivity

import android.os.Build
import com.jpz.workoutnotebook.activities.EditActivity
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Series
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
class EditExerciseFragmentTest {

    private var editActivity: EditActivity? = null
    private val mockedEditExerciseFragment = Mockito.mock(EditExerciseFragment::class.java)
    private val mockedExercise = Mockito.mock(Exercise::class.java)

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
    fun checkIfExerciseNameIsEmptyTest() {
        Mockito.`when`(mockedExercise.exerciseName).thenReturn("exerciseName")
        val actual = mockedEditExerciseFragment.checkIfExerciseNameIsEmpty()
        Assert.assertFalse(actual)
    }

    @Test
    fun checkIfSeriesListIsEmptyTest() {
        Mockito.`when`(mockedExercise.seriesList).thenReturn(arrayListOf(Series("id")))
        val actual = mockedEditExerciseFragment.checkIfSeriesListIsEmpty()
        Assert.assertFalse(actual)
    }
}