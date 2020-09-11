package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.FirebaseUtils
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditProfileFragment : Fragment() {

    companion object {
        private val TAG = EditProfileFragment::class.java.simpleName
    }

    private val userViewModel: UserViewModel by viewModel()

    private var nickName: String? = null
    private var name: String? = null
    private var firstName: String? = null
    private var age: Int? = null
    private var photo: String? = null
    private var sports: String? = null
    private var iFollow: ArrayList<String>? = null
    private var followers: ArrayList<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firebaseUtils = FirebaseUtils()
        val userId = firebaseUtils.getCurrentUser()?.uid

        displayUserData(userId)
        onChangeData()
        editProfileFragmentFABSave.setOnClickListener {
            saveUpdatedData(userId)
        }
        editProfileFragmentFABCancel.setOnClickListener {
            activity?.finish()
        }
    }

    //--------------------------------------------------------------------------------------

    private fun displayUserData(userId: String?) {
        if (userId != null) {
            userViewModel.getUser(userId)?.addOnSuccessListener { documentSnapshot ->
                val user: User? = documentSnapshot.toObject(User::class.java)
                photo = user?.photo
                iFollow = user?.iFollow
                followers = user?.followers
                view?.let {
                    Glide.with(it)
                        .load(photo)
                        .circleCrop()
                        .into(editProfileFragmentImage)
                }
                editProfileFragmentNickname.editText?.setText(user?.nickName)
                editProfileFragmentName.editText?.setText(user?.name)
                editProfileFragmentFirstName.editText?.setText(user?.firstName)
                editProfileFragmentAge.editText?.setText(user?.age.toString())
                editProfileFragmentSports.editText?.setText(user?.sports)
            }
        }
    }

    private fun onChangeData() {
        editProfileFragmentNickname.editText?.doOnTextChanged { text, _, _, _ ->
            nickName = text.toString()
        }
        editProfileFragmentName.editText?.doOnTextChanged { text, _, _, _ ->
            name = text.toString()
        }
        editProfileFragmentFirstName.editText?.doOnTextChanged { text, _, _, _ ->
            firstName = text.toString()
        }
        editProfileFragmentAge.editText?.doOnTextChanged { text, _, _, _ ->
            age = text.toString().toIntOrNull()
        }
        editProfileFragmentSports.editText?.doOnTextChanged { text, _, _, _ ->
            sports = text.toString()
        }
    }

    private fun saveUpdatedData(userId: String?) {
        // Update data
        val user = userId?.let { it1 ->
            User(it1, nickName, name, firstName, age, photo, sports, iFollow, followers)
        }
        if (user != null) {
            Log.d(TAG, "user = $user")
            userViewModel.updateUser(user)
            Toast.makeText(activity, R.string.user_data_updated, Toast.LENGTH_SHORT).show()
        }
        activity?.finish()
    }
}