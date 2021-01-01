package com.jpz.workoutnotebook.fragments.followingactivity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.transition.TransitionInflater
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.FollowingActivity
import com.jpz.workoutnotebook.fragments.BaseProfileFragment
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.FollowViewModel
import com.jpz.workoutnotebook.viewmodels.FollowingViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class FollowingFragment : BaseProfileFragment() {

    companion object {
        private val TAG = FollowingFragment::class.java.simpleName
        private const val START_DELAY = 500L
    }

    // Firebase Firestore and utils
    private val followViewModel: FollowViewModel by viewModel()
    private val followingViewModel: FollowingViewModel by viewModel()
    private val myUtils: MyUtils by inject()

    private var callback: FollowerListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Start Animation if the version > Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition =
                TransitionInflater.from(requireContext())
                    .inflateTransition(android.R.transition.slide_left)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Make searchView gone
        val searchView: SearchView =
            (context as FollowingActivity).findViewById(R.id.toolbarSearchView)
        if (searchView.isVisible) {
            searchView.visibility = View.GONE
        }

        // Disable EditText and counter
        binding.baseProfileFragmentNickname.editText?.isEnabled = false
        binding.baseProfileFragmentNickname.isCounterEnabled = false
        binding.baseProfileFragmentName.editText?.isEnabled = false
        binding.baseProfileFragmentName.isCounterEnabled = false
        binding.baseProfileFragmentFirstName.editText?.isEnabled = false
        binding.baseProfileFragmentFirstName.isCounterEnabled = false
        binding.baseProfileFragmentAge.editText?.isEnabled = false
        binding.baseProfileFragmentAge.isCounterEnabled = false
        binding.baseProfileFragmentSports.editText?.isEnabled = false
        binding.baseProfileFragmentSports.isCounterEnabled = false

        // Disable FloatingActionButton Save
        binding.includedLayout.fabSave.visibility = View.GONE

        // Get current user
        userId = userAuth.getCurrentUser()?.uid
        Log.d(TAG, "uid = $userId")

        val following = arguments?.getParcelable<User>(FollowingActivity.FOLLOWING)
        Log.d(TAG, "following = $following")
        binding.user = following

        // Make FloatingActionButton Historical visible
        myUtils.scaleViewAnimation(binding.baseProfileFragmentFABHistorical, START_DELAY)
        binding.baseProfileFragmentFABHistorical.visibility = View.VISIBLE
        binding.baseProfileFragmentFABHistorical.setOnClickListener {
            following?.let { callback?.consultHistorical(it) }
        }

        val isFollowed = arguments?.getBoolean(FollowingActivity.IS_FOLLOWED)

        if (isFollowed != null && isFollowed) {
            // People already followed, make FloatingActionButton NoFollow visible
            myUtils.scaleViewAnimation(binding.baseProfileFragmentFABNoFollow, START_DELAY)
            binding.baseProfileFragmentFABNoFollow.visibility = View.VISIBLE
            binding.baseProfileFragmentFABNoFollow.setOnClickListener {
                userId?.let { following?.let { followed -> noLongerFollow(it, followed) } }
            }
        } else {
            // Make FloatingActionButton Follow visible
            myUtils.scaleViewAnimation(binding.baseProfileFragmentFABFollow, START_DELAY)
            binding.baseProfileFragmentFABFollow.visibility = View.VISIBLE
            binding.baseProfileFragmentFABFollow.setOnClickListener {
                userId?.let { following?.let { followed -> addAPersonToFollow(it, followed) } }
            }
        }
    }

    //----------------------------------------------------------------------------------
    // Methods to create or delete a person to follow

    private fun addAPersonToFollow(userId: String, followed: User) {
        followViewModel.getListOfPeopleFollowed(userId)
            .get()
            .addOnSuccessListener { documents ->
                // Check if the person to follow is already added
                for (document in documents) {
                    if (document.id == followed.userId) {
                        // Inform the user that the person is already followed
                        myUtils.showSnackBar(
                            binding.baseProfileFragmentCoordinatorLayout,
                            R.string.person_already_followed
                        )
                        return@addOnSuccessListener
                    }
                }
                // Else add the person
                followViewModel.follow(userId, followed.userId)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot written with id: ${followed.userId}")
                        // Add the user as a follower in followings collection
                        addUserAsFollower(userId, followed)
                    }
            }
    }

    private fun addUserAsFollower(userId: String, followed: User) {
        // Add the user as a follower in followings collection
        followingViewModel.addFollower(userId, followed.userId)
            .addOnSuccessListener {
                // Inform the user
                myUtils.showSnackBar(
                    binding.baseProfileFragmentCoordinatorLayout, R.string.person_to_follow_added
                )
                closeFragment()
            }
    }

    private fun noLongerFollow(userId: String, followed: User) {
        followViewModel.noLongerFollow(userId, followed.userId)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                // Inform the user
                myUtils.showSnackBar(
                    binding.baseProfileFragmentCoordinatorLayout, R.string.person_no_longer_followed
                )
                closeFragment()
            }
    }

    private fun closeFragment() {
        activity?.let {
            myUtils.closeFragment(binding.baseProfileFragmentProgressBar, it)
            binding.baseProfileFragmentFABFollow.isEnabled = false
            binding.baseProfileFragmentFABHistorical.isEnabled = false
        }
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity when click on historical button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the methods that creating callback after being attached to parent activity
        callbackToParentActivity()
    }

    // Declare our interface and methods that will be implemented by any container activity
    interface FollowerListener {
        fun consultHistorical(followed: User)
    }

    // Create callback to parent activity
    private fun callbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callback = activity as FollowerListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement FollowerListener")
        }
    }
}