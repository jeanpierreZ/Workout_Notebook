package com.jpz.workoutnotebook.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.User


class MyUtils {

    fun showSnackBar(coordinatorLayout: CoordinatorLayout, text: Int) {
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_SHORT).show()
    }

    //--------------------------------------------------------------------------------------
    // Methods to display user profile data

    fun displayUserPhoto(context: Context, uri: Uri, imageView: ImageView) {
        Glide.with(context)
            .load(uri)
            .circleCrop()
            .into(imageView)
    }

    fun displayGenericPhoto(context: Context, imageView: ImageView) {
        // If user has no photo, display a generic icon for the photo
        imageView.background =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_person_pin_24)
    }

    fun displayUserData(
        user: User,
        profileFragmentNickname: TextInputLayout,
        profileFragmentName: TextInputLayout,
        profileFragmentFirstName: TextInputLayout,
        profileFragmentAge: TextInputLayout,
        profileFragmentSports: TextInputLayout,
    ) {
        profileFragmentNickname.editText?.setText(user.nickName)
        profileFragmentName.editText?.setText(user.name)
        profileFragmentFirstName.editText?.setText(user.firstName)
        if (user.age != null) profileFragmentAge.editText?.setText(user.age.toString())
        profileFragmentSports.editText?.setText(user.sports)
    }

}