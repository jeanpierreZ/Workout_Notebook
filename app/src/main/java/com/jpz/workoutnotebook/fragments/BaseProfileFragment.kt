package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.databinding.FragmentBaseProfileBinding
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


abstract class BaseProfileFragment : Fragment() {

    private var _binding: FragmentBaseProfileBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    protected val binding get() = _binding!!

    // Firestore
    protected val userViewModel: UserViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_base_profile, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}