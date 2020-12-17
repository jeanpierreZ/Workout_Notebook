package com.jpz.workoutnotebook.fragments.followingactivity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.transition.TransitionInflater
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.FollowingActivity
import com.jpz.workoutnotebook.fragments.BaseProfileFragment
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.FollowViewModel
import com.jpz.workoutnotebook.viewmodels.FollowingViewModel
import kotlinx.android.synthetic.main.fragment_base_profile.*
import kotlinx.android.synthetic.main.fragment_sports.*
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

        // Disable EditText and counter
        baseProfileFragmentNickname.editText?.isEnabled = false
        baseProfileFragmentNickname.isCounterEnabled = false
        baseProfileFragmentName.editText?.isEnabled = false
        baseProfileFragmentName.isCounterEnabled = false
        baseProfileFragmentFirstName.editText?.isEnabled = false
        baseProfileFragmentFirstName.isCounterEnabled = false
        baseProfileFragmentAge.editText?.isEnabled = false
        baseProfileFragmentAge.isCounterEnabled = false
        baseProfileFragmentSports.editText?.isEnabled = false
        baseProfileFragmentSports.isCounterEnabled = false

        // Disable FloatingActionButton Save
        baseProfileFragmentFABSave.isEnabled = false
        baseProfileFragmentFABSave.visibility = View.GONE

        // Get current user
        userId = userAuth.getCurrentUser()?.uid
        Log.d(TAG, "uid = $userId")

        val following = arguments?.getParcelable<User>(FollowingActivity.FOLLOWING)
        Log.d(TAG, "following = $following")
        binding.user = following

        // Make FloatingActionButton Historical visible
        myUtils.scaleViewAnimation(baseProfileFragmentFABHistorical, START_DELAY)
        baseProfileFragmentFABHistorical.visibility = View.VISIBLE
        baseProfileFragmentFABHistorical.setOnClickListener {
            following?.let { callback?.consultHistorical(it) }
        }

        val isFollowed = arguments?.getBoolean(FollowingActivity.IS_FOLLOWED)

        if (isFollowed != null && isFollowed) {
            // People already followed, make FloatingActionButton NoFollow visible
            myUtils.scaleViewAnimation(baseProfileFragmentFABNoFollow, START_DELAY)
            baseProfileFragmentFABNoFollow.visibility = View.VISIBLE
            baseProfileFragmentFABNoFollow.setOnClickListener {
                userId?.let { following?.let { followed -> noLongerFollow(it, followed) } }
            }
        } else {
            // Make FloatingActionButton Follow visible
            myUtils.scaleViewAnimation(baseProfileFragmentFABFollow, START_DELAY)
            baseProfileFragmentFABFollow.visibility = View.VISIBLE
            baseProfileFragmentFABFollow.setOnClickListener {
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
                            baseProfileFragmentCoordinatorLayout, R.string.person_already_followed
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
                    baseProfileFragmentCoordinatorLayout, R.string.person_to_follow_added
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
                    baseProfileFragmentCoordinatorLayout, R.string.person_no_longer_followed
                )
                closeFragment()
            }
    }

    private fun closeFragment() {
        activity?.let { myUtils.closeFragment(baseProfileFragmentProgressBar, it) }
        baseProfileFragmentFABFollow?.isEnabled = false
        baseProfileFragmentFABHistorical?.isEnabled = false
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