package com.jpz.workoutnotebook.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jpz.workoutnotebook.R
import kotlinx.android.synthetic.main.fragment_sports.*


class SportsFragment : Fragment() {

    private var callback: ButtonListener? = null

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

        exercisesButtonSportsFragment.setOnClickListener {
            callback?.onClickedButton(getString(R.string.exercises))
        }

        workoutsButtonSportsFragment.setOnClickListener {
//            callback?.onClickedButton(getString(R.string.workouts))
        }
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods when click on a button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the methods that creating callback after being attached to parent activity
        callbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface ButtonListener {
        fun onClickedButton(button: String?)
    }

    // Create callback to parent activity
    private fun callbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callback = activity as ButtonListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement ButtonListener")
        }
    }
}