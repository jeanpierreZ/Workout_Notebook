package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ViewPagerAdapter.Companion.ARG_OBJECT
import com.jpz.workoutnotebook.utils.FirebaseUtils
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    private val firebaseUtils = FirebaseUtils()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {

            Glide.with(view)
                .load(firebaseUtils.getCurrentUser()?.photoUrl)
                .circleCrop()
                .into(profileFragmentImage)

            profileFragmentNickname.text = firebaseUtils.getCurrentUser()?.displayName

            profileFragmentSports.text =
                "page = " + getInt(ARG_OBJECT).toString() + " user email = " + firebaseUtils.getCurrentUser()?.email
        }
    }

}