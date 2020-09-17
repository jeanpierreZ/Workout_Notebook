package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.storage.FirebaseStorage
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.api.UserAuth
import com.jpz.workoutnotebook.models.User
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
                            // Download the photo if it exists
                            if (user.photo) {
                                // Instance of FirebaseStorage with point to the root reference
                                val storageRef = FirebaseStorage.getInstance().reference
                                // Use variables to create child values
                                // Points to "photos/userID"
                                val photosRef = storageRef.child("photos")
                                val fileName = user.userId
                                val spaceRef = photosRef.child(fileName)
                                // Download the photo from Firebase Storage
                                spaceRef.downloadUrl.addOnSuccessListener { uri ->
                                    activity?.let { it1 ->
                                        Glide.with(it1)
                                            .load(uri)
                                            .circleCrop()
                                            .into(profileFragmentPhoto)
                                    }
                                }
                                // Else display an icon for the photo
                            } else {
                                profileFragmentPhoto.background =
                                    activity?.let { activity ->
                                        ContextCompat.getDrawable(
                                            activity, R.drawable.ic_baseline_person_pin_24
                                        )
                                    }
                            }
                            profileFragmentNickname.editText?.setText(user.nickName)
                            profileFragmentName.editText?.setText(user.name)
                            profileFragmentFirstName.editText?.setText(user.firstName)
                            profileFragmentAge.editText?.setText(user.age.toString())
                            profileFragmentSports.editText?.setText(user.sports)
                        }
                    }
                }
            }
        }
    }
}