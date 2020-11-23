package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import com.jpz.workoutnotebook.activities.MainActivity.Companion.FOLLOWER
import com.jpz.workoutnotebook.models.User
import kotlinx.android.synthetic.main.fragment_base_profile.*


class FollowerFragment : BaseProfileFragment() {

    companion object {
        private val TAG = FollowerFragment::class.java.simpleName
    }

    //--------------------------------------------------------------------------------------

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

        // Disable FloatingActionButton
        baseProfileFragmentFABSave.isEnabled = false
        baseProfileFragmentFABSave.visibility = View.GONE

        val follower = arguments?.getParcelable<User>(FOLLOWER)
        Log.d(TAG, "follower = $follower")
        binding.user = follower
    }
}