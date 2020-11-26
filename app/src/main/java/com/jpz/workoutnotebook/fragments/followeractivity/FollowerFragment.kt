package com.jpz.workoutnotebook.fragments.followeractivity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.FollowerActivity.Companion.IS_FROM_SEARCH
import com.jpz.workoutnotebook.activities.MainActivity.Companion.FOLLOW
import com.jpz.workoutnotebook.fragments.BaseProfileFragment
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.FollowViewModel
import kotlinx.android.synthetic.main.fragment_base_profile.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class FollowerFragment : BaseProfileFragment() {

    companion object {
        private val TAG = FollowerFragment::class.java.simpleName
    }

    // Firebase Firestore and utils
    private val followViewModel: FollowViewModel by viewModel()
    private val myUtils: MyUtils by inject()

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

        val followed = arguments?.getParcelable<User>(FOLLOW)
        Log.d(TAG, "followed = $followed")
        binding.user = followed

        val isFromSearch = arguments?.getBoolean(IS_FROM_SEARCH)

        if (isFromSearch != null && isFromSearch) {
            // Make FloatingActionButton Follow visible
            baseProfileFragmentFABFollow.visibility = View.VISIBLE
            baseProfileFragmentFABFollow.setOnClickListener {
                userId?.let { followed?.let { followed -> addAPersonToFollow(it, followed) } }
            }
        } else {
            // Make FloatingActionButton NoFollow visible
            baseProfileFragmentFABNoFollow.visibility = View.VISIBLE
            baseProfileFragmentFABNoFollow.setOnClickListener {
                userId?.let { followed?.let { followed -> noLongerFollow(it, followed) } }
            }
            // Make FloatingActionButton History visible
            baseProfileFragmentFABHistory.visibility = View.VISIBLE
            baseProfileFragmentFABHistory.setOnClickListener {
                Toast.makeText(activity, "HISTORY", Toast.LENGTH_SHORT).show()
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
                        // Inform the user
                        myUtils.showSnackBar(
                            baseProfileFragmentCoordinatorLayout, R.string.person_to_follow_added
                        )
                        closeFragment()
                    }
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
        baseProfileFragmentFABFollow.isEnabled = false
    }
}