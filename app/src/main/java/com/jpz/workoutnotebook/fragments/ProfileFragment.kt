package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.firestore.DocumentChange
import com.jpz.workoutnotebook.models.User
import kotlinx.android.synthetic.main.fragment_base_profile.*


class ProfileFragment : BaseProfileFragment() {

    companion object {
        private val TAG = ProfileFragment::class.java.simpleName
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

        // Disable FloatingActionButton
        baseProfileFragmentFABSave.visibility = View.GONE
        baseProfileFragmentFABCancel.visibility = View.GONE

        val userId = userAuth.getCurrentUser()?.uid
        Log.d(TAG, "uid = $userId")

        userId?.let { getCurrentUserDataInRealTime(it) }
    }

    //--------------------------------------------------------------------------------------
    // Listener of current user data in real time from Firebase

    private fun getCurrentUserDataInRealTime(userId: String) {
        userViewModel.getCurrentUser(userId)?.addSnapshotListener { snapshot, e ->

            if (e != null) {
                Log.w(TAG, "listen:error", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                for (dc in snapshot.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED || dc.type == DocumentChange.Type.MODIFIED) {
                        val user: User? = dc.document.toObject(User::class.java)

                        user?.let {
                            Log.i(TAG, "user.photo = ${user.photo}")
                            if (activity != null) {
                                // Download the photo if it exists...
                                if (user.photo != null && user.photo != 0) {
                                    // Download the photo from Firebase Storage
                                    userStoragePhoto.storageRef(userId).downloadUrl.addOnSuccessListener { uri ->
                                        displayUserPhoto(uri)
                                    }
                                    // ...else display an icon for the photo
                                } else {
                                    displayGenericPhoto()
                                }
                            }
                            // Display user data
                            displayUserData(user)
                        }
                    }
                }
            }
        }
    }
}