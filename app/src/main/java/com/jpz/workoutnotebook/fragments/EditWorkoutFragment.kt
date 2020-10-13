package com.jpz.workoutnotebook.fragments

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.ExerciseViewModel
import com.jpz.workoutnotebook.viewmodels.WorkoutViewModel
import kotlinx.android.synthetic.main.fragment_edit_workout.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditWorkoutFragment : Fragment(), View.OnClickListener {

    companion object {
        private val TAG = EditWorkoutFragment::class.java.simpleName
        private const val WORKOUT_NAME_FIELD = "workoutName"
    }

    private lateinit var binding: FragmentEditWorkoutBinding

    private var workout = Workout()

    private var userId: String? = null
    private var workoutNameFromList: String? = null
    private val allExerciseName = mutableListOf<CharSequence>()

    // Firebase Auth, Firestore
    private val userAuth: UserAuth by inject()
    private val workoutViewModel: WorkoutViewModel by viewModel()
    private val exerciseViewModel: ExerciseViewModel by viewModel()
    private val myUtils: MyUtils by inject()

    private var itemExerciseFromWorkoutAdapter: ItemExerciseFromWorkoutAdapter? = null

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
                        workout = documentSnapshot.toObject(Workout::class.java)!!
                        binding.workout = workout
                        configureRecyclerView()
                    }
            }
        } else {
            // Else create an empty new workout
            binding.workout = workout
            // Attach an empty list of exercises
            workout.exercisesList = mutableListOf<Exercise>() as ArrayList<Exercise>
            configureRecyclerView()
        }

        editWorkoutFragmentFABAddExercise.setOnClickListener(this)
        editWorkoutFragmentFABSave.setOnClickListener(this)
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of exercises from this workout
        itemExerciseFromWorkoutAdapter =
            activity?.let {
                ItemExerciseFromWorkoutAdapter(workout.exercisesList as ArrayList<Exercise>, it)
            }
        // Attach the adapter to the recyclerView to populate the exercises
        editWorkoutFragmentRecyclerView?.adapter = itemExerciseFromWorkoutAdapter
        // Set layout manager to position the exercises
        editWorkoutFragmentRecyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    //----------------------------------------------------------------------------------

    // Get the list of all exercises
    private fun getAllExercises(allExerciseName: MutableList<CharSequence>) {
        userId?.let {
            exerciseViewModel.getListOfExercises(it)?.get()?.addOnSuccessListener { documents ->
                for ((index, value) in documents.withIndex()) {
                    val myString: String = value.data.getValue("exerciseName") as String
                    allExerciseName.add(index, myString)
                }
                // Then show the AlertDialog to add an exercise
                addAnExerciseAlertDialog(allExerciseName.toTypedArray())
            }
                ?.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    // Display the AlertDialog with the list of all the exercises, in order to add an exercise in the workout
    private fun addAnExerciseAlertDialog(list: Array<CharSequence>): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.add_an_exercise))
                .setNeutralButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .setItems(list) { _, which ->
                    // Retrieve the exercise from the list position
                    userId?.let { it1 ->
                        exerciseViewModel.getExercise(it1, list[which] as String)
                            ?.addOnSuccessListener { documentSnapshot ->
                                val exerciseToAdd = documentSnapshot.toObject(Exercise::class.java)
                                if (exerciseToAdd != null) {
                                    // Add it to the adapter and the workout
                                    itemExerciseFromWorkoutAdapter?.addAnExercise(
                                        exerciseToAdd, editWorkoutFragmentRecyclerView
                                    )
                                }
                            }
                    }
                }
                .create()
            builder.show()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    //----------------------------------------------------------------------------------
    // Methods to save or update a workout

    private fun saveWorkout() {
        if (workout.workoutName.isNullOrEmpty() || workout.workoutName.isNullOrBlank()) {
            myUtils.showSnackBar(
                editWorkoutFragmentCoordinatorLayout, R.string.workout_name_cannot_be_blank
            )
        } else {
            Log.d(TAG, "workout = $workout")
            if (userId != null) {
                // Check if an workoutName already exists
                workoutViewModel.getListOfExercises(userId!!)
                    ?.whereEqualTo(WORKOUT_NAME_FIELD, workout.workoutName)
                    ?.get()
                    ?.addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            // There isn't document with this workoutName so create the workout
                            Log.d(TAG, "documents.isEmpty")
                            workoutViewModel.createWorkout(
                                editWorkoutFragmentCoordinatorLayout, userId!!, workout.workoutName,
                                workout.workoutDate, workout.exercisesList as ArrayList<Exercise>
                            )
                            closeFragment()
                        } else {
                            // The same exercise name exists, choose another name
                            myUtils.showSnackBar(
                                editWorkoutFragmentCoordinatorLayout,
                                R.string.workout_name_already_exists
                            )
                            for (document in documents) {
                                Log.d(TAG, "${document.id} => ${document.data}")
                            }
                        }
                    }
                    ?.addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }
            }
        }
    }

    private fun updateWorkout() {
        if (workout.workoutName.isNullOrEmpty() || workout.workoutName.isNullOrBlank()) {
            myUtils.showSnackBar(
                editWorkoutFragmentCoordinatorLayout, R.string.workout_name_cannot_be_blank
            )
        } else {
            userId?.let {
                workoutViewModel.updateWorkout(
                    editWorkoutFragmentCoordinatorLayout, it,
                    workout.workoutName, workout.workoutDate, workout.exercisesList
                )
            }
            closeFragment()
        }
    }

    //----------------------------------------------------------------------------------

    private fun closeFragment() {
        editWorkoutFragmentProgressBar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            activity?.onBackPressed()
        }, 2000)
    }

    //----------------------------------------------------------------------------------

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.editWorkoutFragmentFABAddExercise -> getAllExercises(allExerciseName)
            R.id.editWorkoutFragmentFABSave -> {
                if (workoutNameFromList != null) {
                    updateWorkout()
                } else {
                    saveWorkout()
                }
            }
        }
    }
}