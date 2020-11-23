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

        val follow = arguments?.getParcelable<User>(FOLLOW)
        Log.d(TAG, "follow = $follow")
        binding.user = follow

        val isFromSearch = arguments?.getBoolean(IS_FROM_SEARCH)

        if (isFromSearch != null && isFromSearch) {
            // Enable FloatingActionButton Add
            baseProfileFragmentFABAdd.isEnabled = true
            baseProfileFragmentFABAdd.visibility = View.VISIBLE

            baseProfileFragmentFABAdd.setOnClickListener {
                userId = userAuth.getCurrentUser()?.uid
                Log.d(TAG, "uid = $userId")
                userId?.let { follow?.let { follow -> addAPersonToFollow(it, follow) } }
            }
        }
    }

    //----------------------------------------------------------------------------------
    // Methods to create a person to follow

    // Todo : compare if the person to follow is already in the list before adding
    private fun addAPersonToFollow(userId: String, follow: User) {
        followViewModel.addFollow(userId, follow)
            ?.addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with id: ${documentReference.id}")
                // Inform the user
                myUtils.showSnackBar(
                    baseProfileFragmentCoordinatorLayout, R.string.person_to_follow_added
                )
                closeFragment()
            }
    }

    private fun closeFragment() {
        activity?.let { myUtils.closeFragment(baseProfileFragmentProgressBar, it) }
        baseProfileFragmentFABAdd.isEnabled = false
    }
}