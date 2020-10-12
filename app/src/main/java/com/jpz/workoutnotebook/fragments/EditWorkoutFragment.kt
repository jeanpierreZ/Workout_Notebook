package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.EditActivity.Companion.WORKOUT_NAME
import com.jpz.workoutnotebook.adapters.ItemExerciseFromWorkoutAdapter
import com.jpz.workoutnotebook.api.UserAuth
import com.jpz.workoutnotebook.databinding.FragmentEditWorkoutBinding
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Workout
import com.jpz.workoutnotebook.viewmodels.ExerciseViewModel
import com.jpz.workoutnotebook.viewmodels.WorkoutViewModel
import kotlinx.android.synthetic.main.fragment_edit_workout.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditWorkoutFragment : Fragment() {

    companion object {
        private val TAG = EditWorkoutFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentEditWorkoutBinding

    private val workout = Workout()

    private var workoutFromList = Workout()
    private var userId: String? = null
    private var workoutNameFromList: String? = null

    // Firebase Auth, Firestore
    private val userAuth: UserAuth by inject()
    private val workoutViewModel: WorkoutViewModel by viewModel()
    private val exerciseViewModel: ExerciseViewModel by viewModel()

    private var itemExerciseFromWorkoutAdapter: ItemExerciseFromWorkoutAdapter? = null
    private var exercisesList: MutableList<Exercise> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_workout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid

        workoutNameFromList = arguments?.getString(WORKOUT_NAME)
        Log.d(TAG, "workoutNameFromList = $workoutNameFromList")

        if (workoutNameFromList != null) {
            // If the user click on a workout in the list, bind data with this workout
            userId?.let {
                workoutViewModel.getWorkout(it, workoutNameFromList!!)
                    ?.addOnSuccessListener { documentSnapshot ->
                        workoutFromList = documentSnapshot.toObject(Workout::class.java)!!
                        Log.d(TAG, "workoutFromList  = $workoutFromList")
                        workoutFromList.exercisesList?.let { it1 -> exercisesList.addAll(it1) }
                        binding.workout = workoutFromList
                    }
            }
        } else {
            // Else create an empty new workout
            binding.workout = workout
            // Attach the list of exercises
            workout.exercisesList = exercisesList as ArrayList<Exercise>
        }

        configureRecyclerView()

        editWorkoutFragmentFABSave.setOnClickListener { saveWorkout() }
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of exercises from this workout
        itemExerciseFromWorkoutAdapter =
            activity?.let {
                ItemExerciseFromWorkoutAdapter(exercisesList as ArrayList<Exercise>, it)
            }
        // Attach the adapter to the recyclerView to populate the exercises
        editWorkoutFragmentRecyclerView?.adapter = itemExerciseFromWorkoutAdapter
        // Set layout manager to position the exercises
        editWorkoutFragmentRecyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    //----------------------------------------------------------------------------------
    // Methods to save or update a workout

    private fun saveWorkout() {
        if (workout.workoutName.isNullOrEmpty() || workout.workoutName.isNullOrBlank()) {
            Toast.makeText(
                activity, getString(R.string.workout_name_cannot_be_blank), Toast.LENGTH_SHORT
            ).show()
        } else {
            Log.d(TAG, "workout = $workout")
            if (userId != null) {
                workoutViewModel.createWorkout(
                    editWorkoutFragmentCoordinatorLayout, userId!!, workout.workoutName,
                    workout.workoutDate, workout.exercisesList as ArrayList<Exercise>
                )
            }
            closeFragment()
        }
    }

    // todo update a workout
    // todo Add an exercise from list

    //----------------------------------------------------------------------------------

    private fun closeFragment() {
        activity?.onBackPressed()
    }
}