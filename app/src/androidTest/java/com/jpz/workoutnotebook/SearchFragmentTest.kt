package com.jpz.workoutnotebook

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jpz.workoutnotebook.activities.FollowingActivity
import com.jpz.workoutnotebook.fragments.followingactivity.SearchFragment
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {

    @Test
    fun searchViewTest() {
        val searchFragment = SearchFragment()
        ActivityScenario.launch(FollowingActivity::class.java)
            .onActivity {
                it.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.followerActivityContainer, searchFragment)
                    .commit()
            }
        onView(withId(R.id.toolbarSearchView)).check(matches(isDisplayed()))
    }
}