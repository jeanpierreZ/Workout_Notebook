package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ViewPagerAdapter.Companion.ARG_OBJECT
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.FirebaseUtils
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : Fragment() {

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
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {

            val userId = firebaseUtils.getCurrentUser()?.uid

            // Get data from the connected user
            if (userId != null) {
                Log.i("PROFILE", "user = $userId")

                userViewModel.getUser(userId)?.addOnSuccessListener { documentSnapshot ->

                    val user: User? = documentSnapshot.toObject(User::class.java)

                    Glide.with(view)
                        .load(user?.photo)
                        .circleCrop()
                        .into(profileFragmentImage)

                    profileFragmentNickname.text = user?.nickName
                    profileFragmentName.text = user?.name
                    profileFragmentAge.text = user?.age.toString()

                    profileFragmentSports.text =
                        "page = " + getInt(ARG_OBJECT).toString() + " user email = " + firebaseUtils.getCurrentUser()?.email
                }
                    ?.addOnFailureListener { _ ->
                        myUtils.showSnackBar(
                            profileFragmentCoordinator,
                            R.string.user_data_recovery_error
                        )
                    }
            }
        }
    }
}