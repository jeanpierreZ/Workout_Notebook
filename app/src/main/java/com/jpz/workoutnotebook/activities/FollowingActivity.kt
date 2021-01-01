package com.jpz.workoutnotebook.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity.Companion.EDIT
import com.jpz.workoutnotebook.activities.MainActivity.Companion.SEARCH_FRAGMENT
import com.jpz.workoutnotebook.databinding.ActivityFollowingBinding
import com.jpz.workoutnotebook.fragments.followingactivity.FollowingFragment
import com.jpz.workoutnotebook.fragments.followingactivity.SearchFragment
import com.jpz.workoutnotebook.models.User


class FollowingActivity : AppCompatActivity(), SearchFragment.FollowListener,
    FollowingFragment.FollowerListener {

    companion object {
        private val TAG = FollowingActivity::class.java.simpleName
        const val IS_FOLLOWED = "IS_FOLLOWED"
        const val HISTORICAL_FROM_FOLLOWER = "HISTORICAL_FROM_FOLLOWER"
        const val FOLLOWING = "FOLLOWING"
    }

    private lateinit var binding: ActivityFollowingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        configureToolbar()

        // From MainActivity
        val edit = intent.getStringExtra(EDIT)
        Log.d(TAG, "edit = $edit")

        if (edit == SEARCH_FRAGMENT) {
            displaySearchFragment()
        }

        val isFollowed = intent.getBooleanExtra(MainActivity.IS_FOLLOWED, false)
        // The followed people or the follower from communityFragment
        val follow = intent.getParcelableExtra<User>(MainActivity.FOLLOW)
        Log.d(TAG, "follow = $follow")
        follow?.let { displayFollowingFragment(it, isFollowed) }
    }

    //--------------------------------------------------------------------------------------
    // UI

    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        setSupportActionBar(binding.includedLayout.toolbar)
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

    private fun displayFollowingFragment(follow: User, isFollowed: Boolean) {
        val followingFragment = FollowingFragment()
        val bundle = Bundle()
        bundle.putParcelable(FOLLOWING, follow)
        bundle.putBoolean(IS_FOLLOWED, isFollowed)
        followingFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.followerActivityContainer, followingFragment)
            .commit()
    }

    private fun displayFollowingFragmentFromSearch(follow: User, viewClicked: View?) {
        val followingFragment = FollowingFragment()
        val bundle = Bundle()
        val isFollowed = false
        bundle.putParcelable(FOLLOWING, follow)
        bundle.putBoolean(IS_FOLLOWED, isFollowed)
        followingFragment.arguments = bundle

        // Start Animation if the version > Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (viewClicked != null) {
                supportFragmentManager.commit {
                    addSharedElement(
                        viewClicked, getString(R.string.animation_profile_photo_list_to_detail)
                    )
                    replace(R.id.followerActivityContainer, followingFragment)
                    addToBackStack(TAG)
                }
            }
        } else {
            supportFragmentManager.commit {
                replace(R.id.followerActivityContainer, followingFragment)
            }
        }
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
    override fun displayFollow(follow: User?, viewClicked: View?) {
        follow?.let { displayFollowingFragmentFromSearch(it, viewClicked) }
    }

    // Callback from FollowerFragment
    override fun consultHistorical(followed: User) {
        startEditActivityForHistorical(followed)
    }
}