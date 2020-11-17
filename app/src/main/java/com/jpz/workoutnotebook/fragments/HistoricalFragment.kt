package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity
import com.jpz.workoutnotebook.models.TrainingSession
import kotlinx.android.synthetic.main.fragment_historical.*


class HistoricalFragment : Fragment() {

    companion object {
        private val TAG = HistoricalFragment::class.java.simpleName
    }

    private var trainingSession: TrainingSession? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historical, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trainingSession = arguments?.getParcelable(MainActivity.TRAINING_SESSION)
        Log.d(TAG, "trainingSession = $trainingSession")

        historicalFragmentWorkoutName?.text = trainingSession?.workout?.workoutName
        historicalFragmentDate?.text = trainingSession?.trainingSessionDate
    }
}