package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity.Companion.TRAINING_SESSION
import com.jpz.workoutnotebook.models.TrainingSession
import kotlinx.android.synthetic.main.fragment_training_session.*


class TrainingSessionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val trainingSession: TrainingSession? = arguments?.getParcelable(TRAINING_SESSION)

        trainingSessionFragmentWorkoutName.text = trainingSession?.workout?.workoutName

        if (trainingSession?.workout?.exercisesList != null && trainingSession.workout?.exercisesList!!.isNotEmpty()) {
            trainingSessionFragmentExerciseName.text =
                trainingSession.workout?.exercisesList!![0].exerciseName

            trainingSessionFragmentRestTime.text = trainingSession.workout?.exercisesList!![0].restNextSet.toString()

        }

    }
}