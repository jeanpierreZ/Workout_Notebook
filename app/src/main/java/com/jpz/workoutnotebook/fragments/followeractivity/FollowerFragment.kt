package com.jpz.workoutnotebook.fragments.followeractivity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.jpz.workoutnotebook.activities.FollowerActivity.Companion.IS_FROM_SEARCH
import com.jpz.workoutnotebook.activities.MainActivity.Companion.FOLLOW
import com.jpz.workoutnotebook.fragments.BaseProfileFragment
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
                Toast.makeText(activity, "CLICKED ADD", Toast.LENGTH_SHORT).show()
            }
        }
    }
}