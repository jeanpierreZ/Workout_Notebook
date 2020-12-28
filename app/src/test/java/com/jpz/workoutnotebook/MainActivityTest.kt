package com.jpz.workoutnotebook

import android.content.Intent
import android.os.Build
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.jpz.workoutnotebook.activities.EditActivity
import com.jpz.workoutnotebook.activities.FollowingActivity
import com.jpz.workoutnotebook.activities.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MainActivityTest {

    private var mainActivity: MainActivity? = null
    private val viewPager: ViewPager2? = mainActivity?.findViewById(R.id.mainActivityViewPager)

    @Before
    fun setUp() {
        mainActivity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .start()
            .resume()
            .get()
    }

    @Test
    fun clickFABAddCalendarTest() {
        // Navigate to CalendarFragment
        viewPager?.currentItem = 1
        // Click on FloatingActionButton
        mainActivity?.findViewById<View>(R.id.mainActivityFABAddCalendar)?.performClick()
        // Set expected and actual intents
        val expectedIntent = Intent(mainActivity, EditActivity::class.java)
        val actualIntent: Intent = shadowOf(mainActivity).nextStartedActivity
        assertEquals(expectedIntent.component, actualIntent.component)
        stopKoin() // To avoid the IllegalStateException : A KoinContext is already started
    }

    @Test
    fun clickFABSearchCommunityTest() {
        // Navigate to CommunityFragment
        viewPager?.currentItem = 3
        // Click on FloatingActionButton
        mainActivity?.findViewById<View>(R.id.mainActivityFABSearchCommunity)?.performClick()
        // Set expected and actual intents
        val expectedIntent = Intent(mainActivity, FollowingActivity::class.java)
        val actualIntent: Intent = shadowOf(mainActivity).nextStartedActivity
        assertEquals(expectedIntent.component, actualIntent.component)
        stopKoin() // To avoid the IllegalStateException : A KoinContext is already started
    }

    @Test
    fun clickFABEditProfileTest() {
        // Navigate to ProfileFragment
        viewPager?.currentItem = 4
        // Click on FloatingActionButton
        mainActivity?.findViewById<View>(R.id.mainActivityFABEditProfile)?.performClick()
        // Set expected and actual intents
        val expectedIntent = Intent(mainActivity, EditActivity::class.java)
        val actualIntent: Intent = shadowOf(mainActivity).nextStartedActivity
        assertEquals(expectedIntent.component, actualIntent.component)
        stopKoin() // To avoid the IllegalStateException : A KoinContext is already started
    }
}