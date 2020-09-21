package com.jpz.workoutnotebook.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.api.UserAuth
import com.jpz.workoutnotebook.api.UserStoragePhoto
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_base_profile.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


abstract class BaseProfileFragment : Fragment() {

    // Firebase Auth, Firestore and Storage
    protected val userAuth: UserAuth by inject()
    protected val userViewModel: UserViewModel by viewModel()
    protected val userStoragePhoto: UserStoragePhoto by inject()

    protected var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_profile, container, false)
    }

    //--------------------------------------------------------------------------------------
    // Methods to display the user profile data

    protected fun displayUserPhoto(uri: Uri) {
        activity?.let {
            Glide.with(it)
                .load(uri)
                .circleCrop()
                .into(baseProfileFragmentPhoto)
        }
    }

    protected fun displayGenericPhoto() {
        // If user has no photo, display a generic icon for the photo
        baseProfileFragmentPhoto.background = activity?.let { activity ->
            ContextCompat.getDrawable(activity, R.drawable.ic_baseline_person_pin_24)
        }
    }

    protected fun displayUserData(user: User) {
        baseProfileFragmentNickname?.editText?.setText(user.nickName)
        baseProfileFragmentName?.editText?.setText(user.name)
        baseProfileFragmentFirstName?.editText?.setText(user.firstName)
        if (user.age != null) baseProfileFragmentAge?.editText?.setText(user.age.toString())
        baseProfileFragmentSports?.editText?.setText(user.sports)
    }
}