package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.repositories.UserAuth
import com.jpz.workoutnotebook.repositories.UserStoragePhoto
import com.jpz.workoutnotebook.databinding.FragmentBaseProfileBinding
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


abstract class BaseProfileFragment : Fragment() {

    protected lateinit var binding: FragmentBaseProfileBinding

    // Firebase Auth, Firestore and Storage
    protected val userAuth: UserAuth by inject()
    protected val userViewModel: UserViewModel by viewModel()
    protected val userStoragePhoto: UserStoragePhoto by inject()

    protected var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_base_profile, container, false)
        return binding.root
    }
}