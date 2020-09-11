package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentChange
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.FirebaseUtils
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : Fragment() {

    companion object {
        private val TAG = ProfileFragment::class.java.simpleName
    }

    private val firebaseUtils = FirebaseUtils()
    private val myUtils = MyUtils()

    private val userViewModel: UserViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userId = firebaseUtils.getCurrentUser()?.uid
        Log.d(TAG, "uid = $userId")
        if (userId != null) {
            getCurrentUserInRealTime(userId)
        }
    }

    private fun getUser(userId: String) {
        // Get data from the connected user
        Log.i("PROFILE", "user = $userId")
        userViewModel.getUser(userId)?.addOnSuccessListener { documentSnapshot ->
            val user: User? = documentSnapshot.toObject(User::class.java)
            view?.let {
                Glide.with(it)
                    .load(user?.photo)
                    .circleCrop()
                    .into(profileFragmentImage)
            }
            profileFragmentNickname.editText?.setText(user?.nickName)
            profileFragmentName.editText?.setText(user?.name)
            profileFragmentFirstName.editText?.setText(user?.firstName)
            profileFragmentAge.editText?.setText(user?.age.toString())
            profileFragmentSports.editText?.setText(user?.sports)
        }
            ?.addOnFailureListener { _ ->
                myUtils.showSnackBar(
                    profileFragmentCoordinatorLayout,
                    R.string.user_data_recovery_error
                )
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
                        view?.let {
                            Glide.with(it)
                                .load(user?.photo)
                                .circleCrop()
                                .into(profileFragmentImage)
                        }
                        profileFragmentNickname.editText?.setText(user?.nickName)
                        profileFragmentName.editText?.setText(user?.name)
                        profileFragmentFirstName.editText?.setText(user?.firstName)
                        profileFragmentAge.editText?.setText(user?.age.toString())
                        profileFragmentSports.editText?.setText(user?.sports)
                    }
                }
            }
        }
    }
}