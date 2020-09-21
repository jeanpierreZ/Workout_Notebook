package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jpz.workoutnotebook.R
import kotlinx.android.synthetic.main.fragment_sports.*


class SportsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardViewSportsFragment.setOnClickListener {
            Toast.makeText(activity, "CardView", Toast.LENGTH_SHORT).show()
        }
    }
}