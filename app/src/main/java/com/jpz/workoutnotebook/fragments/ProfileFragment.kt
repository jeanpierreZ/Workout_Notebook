package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ViewPagerAdapter.Companion.ARG_OBJECT
import com.jpz.workoutnotebook.injections.Injection
import com.jpz.workoutnotebook.injections.ViewModelFactory
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.FirebaseUtils
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    private val firebaseUtils = FirebaseUtils()
    private val myUtils = MyUtils()

    private var userViewModel: UserViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {

            configureViewModel()

            val userId = firebaseUtils.getCurrentUser()?.uid

            // Get data from the connected user
            if (userId != null) {
                Log.i("PROFILE", "user = $userId")

                userViewModel?.getUser(userId)?.addOnSuccessListener { documentSnapshot ->

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

    //--------------------------------------------------------------------------------------

    private fun configureViewModel() {
        val viewModelFactory: ViewModelFactory? =
            activity?.application?.let { Injection.provideViewModelFactory(it) }
        // Use the ViewModelProvider to associate the ViewModel with Activity
        userViewModel =
            viewModelFactory?.let { ViewModelProvider(this, it).get(UserViewModel::class.java) }
    }
}