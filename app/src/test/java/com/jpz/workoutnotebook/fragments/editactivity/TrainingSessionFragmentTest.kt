package com.jpz.workoutnotebook.fragments.editactivity

import android.os.Build
import android.widget.Button
import com.jpz.workoutnotebook.R
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
class TrainingSessionFragmentTest {

    private var editActivity: EditActivity? = null

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
        stopKoin()
    }

    @Test
    fun trainingSessionFragmentOnBackPressedTest() {
        val trainingSessionFragment = TrainingSessionFragment()
        val goButton =
            trainingSessionFragment.view?.findViewById<Button>(R.id.trainingSessionFragmentGo)
        goButton?.isEnabled = true
        editActivity?.onBackPressed()
        // Go button is enabled, the training session has not started, so finish the current activity
        editActivity?.isFinishing?.let { Assert.assertTrue(it) }
    }
}