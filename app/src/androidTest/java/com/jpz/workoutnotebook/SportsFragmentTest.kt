package com.jpz.workoutnotebook

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.jpz.workoutnotebook.activities.MainActivity
import com.jpz.workoutnotebook.fragments.mainactivity.SportsFragment
import com.jpz.workoutnotebook.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class SportsFragmentTest {

    private val myPackage = "com.jpz.workoutnotebook"

    @Throws(IOException::class)
    private fun disableAnimations() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .executeShellCommand("settings put global transition_animation_scale 0")
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .executeShellCommand("settings put global window_animation_scale 0")
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .executeShellCommand("settings put global animator_duration_scale 0")
    }

    @Throws(IOException::class)
    private fun enableAnimations() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .executeShellCommand("settings put global transition_animation_scale 1")
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .executeShellCommand("settings put global window_animation_scale 1")
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .executeShellCommand("settings put global animator_duration_scale 1")
    }

    private fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    private fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    //----------------------------------------------------------------------------------

    @get:Rule
    val activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        disableAnimations()
        registerIdlingResource()
        Intents.init()
    }

    @After
    fun tearDown() {
        enableAnimations()
        unregisterIdlingResource()
        Intents.release()
    }

    @Test
    fun sportsFragmentExercisesButtonTest() {
        launchFragmentInContainer<SportsFragment>(themeResId = R.style.AppTheme)
        Espresso.onView(ViewMatchers.withId(R.id.sportsFragmentExercisesButton))
            .perform(ViewActions.click())
        Intents.intended(IntentMatchers.toPackage(myPackage))
    }

    @Test
    fun sportsFragmentWorkoutsButtonTest() {
        launchFragmentInContainer<SportsFragment>(themeResId = R.style.AppTheme)
        Espresso.onView(ViewMatchers.withId(R.id.sportsFragmentWorkoutsButton))
            .perform(ViewActions.click())
        Intents.intended(IntentMatchers.toPackage(myPackage))
    }

    @Test
    // Only test if a training session is scheduled
    fun sportsFragmentTrainingSessionButtonTest() {
        launchFragmentInContainer<SportsFragment>(themeResId = R.style.AppTheme)
        Espresso.onView(ViewMatchers.withId(R.id.sportsFragmentTrainingSessionButton))
            .perform(ViewActions.click())
        Intents.intended(IntentMatchers.toPackage(myPackage))
    }
}