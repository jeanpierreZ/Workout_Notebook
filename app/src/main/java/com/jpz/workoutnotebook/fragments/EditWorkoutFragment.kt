package com.jpz.workoutnotebook.fragments

import android.app.Dialog
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.EditActivity.Companion.WORKOUT
import com.jpz.workoutnotebook.adapters.ItemExerciseFromWorkoutAdapter
import com.jpz.workoutnotebook.api.UserAuth
import com.jpz.workoutnotebook.databinding.FragmentEditWorkoutBinding
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Workout
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.ExerciseViewModel
import com.jpz.workoutnotebook.viewmodels.WorkoutViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_edit_workout.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditWorkoutFragment : Fragment(), View.OnClickListener {

    companion object {
        private val TAG = EditWorkoutFragment::class.java.simpleName
        private const val WORKOUT_NAME_FIELD = "workoutName"
    }

    private lateinit var binding: FragmentEditWorkoutBinding

    private var workout: Workout? = null

    private var userId: String? = null

    private var workoutNameToUpdate: String? = null
    private var toUpdate = false

    // Firebase Auth, Firestore and utils
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

        // If it is to update, get a workout object from ListSportsFragment / EditActivity
        workout = arguments?.getParcelable(WORKOUT)
        Log.d(TAG, "workout = $workout")

        if (workout != null) {
            // If the user clicked on a workout from the previous list, bind data with this workout
            getWorkoutFromBundle(workout)
        } else {
            // Else create an empty new workout
            workout = Workout()
            binding.workout = workout
            configureRecyclerView()
        }

        swipeToDeleteAnExercise()

        editWorkoutFragmentFABAddExercise.setOnClickListener(this)
        editWorkoutFragmentFABSave.setOnClickListener(this)
    }

    //--------------------------------------------------------------------------------------

    private fun getWorkoutFromBundle(workout: Workout?) {
        // Get data
        binding.workout = workout
        // Get initial workout name to compare it later
        workoutNameToUpdate = workout?.workoutName
        toUpdate = true
        // Display data in the recyclerView
        configureRecyclerView()
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of exercises from this workout
        itemExerciseFromWorkoutAdapter =
            activity?.let { activity ->
                workout?.exercisesList?.let { it -> ItemExerciseFromWorkoutAdapter(it, activity) }
            }
        // Attach the adapter to the recyclerView to populate the exercises
        editWorkoutFragmentRecyclerView?.adapter = itemExerciseFromWorkoutAdapter
        // Set layout manager to position the exercises
        editWorkoutFragmentRecyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    //----------------------------------------------------------------------------------
    // Methods to remove an exercise

    private fun swipeToDeleteAnExercise() {
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Retrieve the position swiped
                val position = viewHolder.adapterPosition
                // Retrieve the exercise object swiped
                val recentlyDeletedItem: Exercise? = workout?.exercisesList?.get(position)
                Log.d(TAG, "recentlyDeletedItem = $recentlyDeletedItem")
                itemExerciseFromWorkoutAdapter?.deleteAnExercise(
                    editWorkoutFragmentCoordinatorLayout, position, recentlyDeletedItem
                )
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                activity?.let { ContextCompat.getColor(it, R.color.colorDecorSwipeRed) }?.let {
                    RecyclerViewSwipeDecorator.Builder(
                        c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                    )
                        .addBackgroundColor(it)
                        .addActionIcon(R.drawable.ic_baseline_delete_24)
                        .create()
                        .decorate()
                }
                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(editWorkoutFragmentRecyclerView)
    }

    //----------------------------------------------------------------------------------

    // Get the list of all exercises
    private fun getAllExercises() {
        val allExercises = arrayListOf<Exercise>()

        userId?.let {
            exerciseViewModel.getOrderedListOfExercises(it)?.get()
                ?.addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        myUtils.showSnackBar(
                            editWorkoutFragmentCoordinatorLayout, R.string.no_exercise
                        )
                    } else {
                        for (document in documents) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                            val exerciseToAdd = document.toObject(Exercise::class.java)
                            // Add the exercises to the list
                            allExercises.add(exerciseToAdd)
                        }
                        // Then show the AlertDialog
                        addAnExerciseAlertDialog(allExercises)
                    }
                }
                ?.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    // Display the AlertDialog with the list of the exercises, in order to add one to the workout
    private fun addAnExerciseAlertDialog(exercises: ArrayList<Exercise>): Dialog {
        // Create a list of exercise names to display on the AlertDialog
        val exerciseNamesToDisplay = arrayListOf<String>()
        // Add all exercise names to this list
        for (exercise in exercises) {
            exercise.exerciseName?.let { exerciseNamesToDisplay.add(it) }
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.add_an_exercise))
                .setNeutralButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .setItems(exerciseNamesToDisplay.toTypedArray()) { _, which ->
                    userId?.let { userId ->
                        // Retrieve the exercise from the list position
                        val exerciseId = exercises[which].exerciseId
                        exerciseId?.let {
                            exerciseViewModel.getExercise(userId, exerciseId)
                                ?.addOnSuccessListener { documentSnapshot ->
                                    val exerciseToAdd =
                                        documentSnapshot.toObject(Exercise::class.java)
                                    exerciseToAdd?.let {
                                        // Add the exercise to the adapter and the workout
                                        itemExerciseFromWorkoutAdapter?.addAnExercise(
                                            exerciseToAdd, editWorkoutFragmentRecyclerView
                                        )
                                    }
                                }
                        }
                    }
                }
                .create()
            builder.show()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    //----------------------------------------------------------------------------------
    // Methods to create or update a workout

    private fun createOrUpdateWorkout(toUpdate: Boolean) {
        if (checkIfWorkoutNameIsEmpty()) {
            return
        }

        Log.d(TAG, "workout = $workout")
        userId?.let {
            // If it is an update and it is the same name, update the workout
            if (workoutNameToUpdate == workout?.workoutName && toUpdate) {
                createOrUpdateToFirestore(it, toUpdate)
            } else {
                // If the name is different and it is not an update,
                // check if a workoutName already exists
                workoutViewModel.getListOfExercises(it)
                    ?.whereEqualTo(WORKOUT_NAME_FIELD, workout?.workoutName)
                    ?.get()
                    ?.addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            // There is no document with this workoutName so create or update it
                            Log.d(TAG, "documents.isEmpty")
                            createOrUpdateToFirestore(it, toUpdate)
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

    private fun createOrUpdateToFirestore(userId: String, toUpdate: Boolean) {
        if (toUpdate) {
            // Update the workout
            workout?.let {
                workoutViewModel.updateWorkout(editWorkoutFragmentCoordinatorLayout, userId, it)
            }
        } else {
            // Create the workout
            workout?.let {
                workoutViewModel.createWorkout(editWorkoutFragmentCoordinatorLayout, userId, it)
            }
        }
        closeFragment()
    }

    private fun checkIfWorkoutNameIsEmpty(): Boolean {
        return if (workout?.workoutName.isNullOrEmpty() || workout?.workoutName.isNullOrBlank()) {
            myUtils.showSnackBar(
                editWorkoutFragmentCoordinatorLayout, R.string.workout_name_cannot_be_blank
            )
            true
        } else false
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
            R.id.editWorkoutFragmentFABAddExercise -> getAllExercises()
            R.id.editWorkoutFragmentFABSave -> createOrUpdateWorkout(toUpdate)
        }
    }
}