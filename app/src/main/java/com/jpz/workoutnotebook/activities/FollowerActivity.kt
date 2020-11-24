package com.jpz.workoutnotebook.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity.Companion.EDIT
import com.jpz.workoutnotebook.activities.MainActivity.Companion.FOLLOW
import com.jpz.workoutnotebook.activities.MainActivity.Companion.SEARCH_FRAGMENT
import com.jpz.workoutnotebook.fragments.followeractivity.FollowerFragment
import com.jpz.workoutnotebook.fragments.followeractivity.SearchFragment
import com.jpz.workoutnotebook.models.User
import kotlinx.android.synthetic.main.toolbar.*

class FollowerActivity : AppCompatActivity(), SearchFragment.FollowListener {

    companion object {
        private val TAG = FollowerActivity::class.java.simpleName
        const val IS_FROM_SEARCH = "IS_FROM_SEARCH"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follower)

        configureToolbar()

        // From MainActivity
        val edit = intent.getStringExtra(EDIT)
        Log.d(TAG, "edit = $edit")

        if (edit == SEARCH_FRAGMENT) {
            displaySearchFragment()
        }

        // The follow from communityFragment
        val follow = intent.getParcelableExtra<User>(FOLLOW)
        Log.d(TAG, "follow = $follow")

        follow?.let { displayFollowerFragment(it, false) }
    }

    //--------------------------------------------------------------------------------------
    // UI

    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        setSupportActionBar(toolbar)
        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //--------------------------------------------------------------------------------------

    private fun displaySearchFragment() {
        val searchFragment = SearchFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.followerActivityContainer, searchFragment)
            .commit()
    }

    private fun displayFollowerFragment(follow: User, isFromSearch: Boolean) {
        val followerFragment = FollowerFragment()
        val bundle = Bundle()
        bundle.putParcelable(FOLLOW, follow)
        bundle.putBoolean(IS_FROM_SEARCH, isFromSearch)
        followerFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.followerActivityContainer, followerFragment)
            .commit()
    }

    //--------------------------------------------------------------------------------------
    // Callback from SearchFragment

    override fun displayFollow(follow: User?) {
        follow?.let { displayFollowerFragment(it, true) }
    }
}