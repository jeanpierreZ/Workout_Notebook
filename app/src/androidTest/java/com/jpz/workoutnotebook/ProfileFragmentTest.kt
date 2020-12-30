package com.jpz.workoutnotebook

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.viewpager2.widget.ViewPager2
import com.jpz.workoutnotebook.activities.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest {

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

    //----------------------------------------------------------------------------------

    @Before
    fun setUp() {
        disableAnimations()
    }

    @After
    fun tearDown() {
        enableAnimations()
    }

    @Test
    fun menuDisconnectTest() {
        ActivityScenario.launch(MainActivity::class.java)
            .onActivity {
                val viewPager = it.findViewById<ViewPager2>(R.id.mainActivityViewPager)
                // Navigate to ProfileFragment
                viewPager.currentItem = 4
            }
        // Click on the icon in the toolbar
        Espresso.onView(withId(R.id.menu_disconnect)).perform(click())
        // check if the message in the alertDialog is displayed
        Espresso.onView(ViewMatchers.withText(R.string.disconnect))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}