package com.jpz.workoutnotebook.fragments.followeractivity

import android.os.Bundle
import android.util.Log
import android.view.View
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

        val follow = arguments?.getParcelable<User>(FOLLOW)
        Log.d(TAG, "follow = $follow")
        binding.user = follow

        val isFromSearch = arguments?.getBoolean(IS_FROM_SEARCH)

        if (isFromSearch != null && isFromSearch) {
            // Enable FloatingActionButton to follow
            baseProfileFragmentFABFollow.isEnabled = true
            baseProfileFragmentFABFollow.visibility = View.VISIBLE

            baseProfileFragmentFABFollow.setOnClickListener {
                userId?.let { follow?.let { follow -> addAPersonToFollow(it, follow) } }
            }
        } else {
            // Enable FloatingActionButton and change the text to no longer follow
            baseProfileFragmentFABFollow.text = getString(R.string.no_longer_follow)
            baseProfileFragmentFABFollow.isEnabled = true
            baseProfileFragmentFABFollow.visibility = View.VISIBLE

            baseProfileFragmentFABFollow.setOnClickListener {
                userId?.let { follow?.let { follow -> noLongerFollow(it, follow) } }
            }
        }
    }

    //----------------------------------------------------------------------------------
    // Methods to create or delete a person to follow

    private fun addAPersonToFollow(userId: String, follow: User) {
        followViewModel.getListOfFollow(userId)
            ?.get()
            ?.addOnSuccessListener { documents ->
                // Check if the person to follow is already added
                for (document in documents) {
                    if (document.id == follow.userId) {
                        // Inform the user that the person is already followed
                        myUtils.showSnackBar(
                            baseProfileFragmentCoordinatorLayout, R.string.person_already_followed
                        )
                        return@addOnSuccessListener
                    }
                }
                // Else add the person
                followViewModel.follow(userId, follow)
                    ?.addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot written with id: ${follow.userId}")
                        // Inform the user
                        myUtils.showSnackBar(
                            baseProfileFragmentCoordinatorLayout, R.string.person_to_follow_added
                        )
                        closeFragment()
                    }
            }
    }

    private fun noLongerFollow(userId: String, follow: User) {
        followViewModel.noLongerFollow(userId, follow)
            ?.addOnSuccessListener {
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