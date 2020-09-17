package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentChange
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.api.UserAuth
import com.jpz.workoutnotebook.api.UserStoragePhoto
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : Fragment() {

    companion object {
        private val TAG = ProfileFragment::class.java.simpleName
    }

    private val userAuth: UserAuth by inject()
    private val userViewModel: UserViewModel by viewModel()
    private val userStoragePhoto: UserStoragePhoto by inject()
    private val myUtils: MyUtils by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userId = userAuth.getCurrentUser()?.uid
        Log.d(TAG, "uid = $userId")
        if (userId != null) {
            getCurrentUserInRealTime(userId)
        }
    }

    //--------------------------------------------------------------------------------------
    // Listener of current user data in real time from Firebase

    private fun getCurrentUserInRealTime(userId: String) {
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
                            Log.i(TAG, "User photo = ${user.photo}")
                            if (activity != null) {
                                // Download the photo if it exists...
                                if (user.photo != 0) {
                                    // Download the photo from Firebase Storage
                                    userStoragePhoto.storageRef(userId).downloadUrl.addOnSuccessListener { uri ->
                                        myUtils.displayUserPhoto(
                                            activity!!, uri, profileFragmentPhoto
                                        )
                                    }
                                    // ...else display an icon for the photo
                                } else {
                                    myUtils.displayGenericPhoto(activity!!, profileFragmentPhoto)
                                }
                            }
                            // Display user data
                            myUtils.displayUserData(
                                user,
                                profileFragmentNickname,
                                profileFragmentName,
                                profileFragmentFirstName,
                                profileFragmentAge,
                                profileFragmentSports
                            )
                        }
                    }
                }
            }
        }
    }
}