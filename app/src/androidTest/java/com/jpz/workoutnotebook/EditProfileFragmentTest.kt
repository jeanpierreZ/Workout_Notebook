package com.jpz.workoutnotebook

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jpz.workoutnotebook.activities.EditActivity
import com.jpz.workoutnotebook.fragments.editactivity.EditProfileFragment
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class EditProfileFragmentTest {

    private val myPackage = "com.jpz.workoutnotebook"

    @get:Rule
    val activityRule: ActivityScenarioRule<EditActivity> =
        ActivityScenarioRule(EditActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun baseProfileFragmentPhotoTest() {
        launchFragmentInContainer<EditProfileFragment>(themeResId = R.style.AppTheme)
        Espresso.onView(ViewMatchers.withId(R.id.baseProfileFragmentPhoto))
            .perform(ViewActions.click())
        Intents.intended(IntentMatchers.toPackage(myPackage))
    }
}