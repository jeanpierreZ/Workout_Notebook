package com.jpz.workoutnotebook.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity.Companion.EDIT
import com.jpz.workoutnotebook.activities.MainActivity.Companion.SEARCH_FRAGMENT
import com.jpz.workoutnotebook.fragments.followingactivity.FollowingFragment
import com.jpz.workoutnotebook.fragments.followingactivity.SearchFragment
import com.jpz.workoutnotebook.models.User
import kotlinx.android.synthetic.main.toolbar.*

class FollowingActivity : AppCompatActivity(), SearchFragment.FollowListener,
    FollowingFragment.FollowerListener {

    companion object {
        private val TAG = FollowingActivity::class.java.simpleName
        const val TO_FOLLOW = "toFollow"
        const val HISTORICAL_FROM_FOLLOWER = "HISTORICAL_FROM_FOLLOWER"
        const val FOLLOWING = "FOLLOWING"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following)

        configureToolbar()

        // From MainActivity
        val edit = intent.getStringExtra(EDIT)
        Log.d(TAG, "edit = $edit")

        if (edit == SEARCH_FRAGMENT) {
            displaySearchFragment()
        }

        // The followed from communityFragment
        val followed = intent.getParcelableExtra<User>(MainActivity.FOLLOWED)
        Log.d(TAG, "followed = $followed")
        followed?.let { displayFollowingFragment(it, false) }

        // The follower from communityFragment
        val follower = intent.getParcelableExtra<User>(MainActivity.FOLLOWER)
        Log.d(TAG, "follower = $follower")
        follower?.let { displayFollowingFragment(it, true) }
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

    private fun displayFollowingFragment(follow: User, toFollow: Boolean) {
        val followerFragment = FollowingFragment()
        val bundle = Bundle()
        bundle.putParcelable(FOLLOWING, follow)
        bundle.putBoolean(TO_FOLLOW, toFollow)
        followerFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.followerActivityContainer, followerFragment)
            .commit()
    }

    //--------------------------------------------------------------------------------------

    private fun startEditActivityForHistorical(followed: User) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra(EDIT, HISTORICAL_FROM_FOLLOWER)
        intent.putExtra(FOLLOWING, followed)
        startActivity(intent)
    }

    //--------------------------------------------------------------------------------------

    // Callback from SearchFragment
    override fun displayFollow(follow: User?) {
        follow?.let { displayFollowingFragment(it, true) }
    }

    // Callback from FollowerFragment
    override fun consultHistorical(followed: User) {
        startEditActivityForHistorical(followed)
    }
}