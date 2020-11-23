package com.jpz.workoutnotebook.fragments.editactivity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity
import com.jpz.workoutnotebook.adapters.ItemExerciseFromWorkoutAdapter
import com.jpz.workoutnotebook.models.TrainingSession
import kotlinx.android.synthetic.main.fragment_historical.*


class HistoricalFragment : Fragment() {

    companion object {
        private val TAG = HistoricalFragment::class.java.simpleName
    }

    private var trainingSession: TrainingSession? = null
    private var itemExerciseFromWorkoutAdapter: ItemExerciseFromWorkoutAdapter? = null

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
        configureRecyclerView()
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of exercises from this workout
        itemExerciseFromWorkoutAdapter =
            activity?.let { activity ->
                trainingSession?.workout?.exercisesList?.let { exercisesList ->
                    ItemExerciseFromWorkoutAdapter(exercisesList, activity)
                }
            }
        // Attach the adapter to the recyclerView to populate the exercises
        historicalFragmentRecyclerView?.adapter = itemExerciseFromWorkoutAdapter
        // Set layout manager to position the exercises
        historicalFragmentRecyclerView?.layoutManager = LinearLayoutManager(activity)
        historicalFragmentRecyclerView?.hasFixedSize()
    }
}