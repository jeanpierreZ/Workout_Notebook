package com.jpz.workoutnotebook.fragments.editactivity

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.EditActivity.Companion.EXERCISE
import com.jpz.workoutnotebook.adapters.ItemSeriesAdapter
import com.jpz.workoutnotebook.databinding.FragmentEditExerciseBinding
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Series
import com.jpz.workoutnotebook.repositories.UserAuth
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.ExerciseViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_edit_exercise.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditExerciseFragment : Fragment(), View.OnClickListener {

    companion object {
        private val TAG = EditExerciseFragment::class.java.simpleName
        private const val EXERCISE_NAME_FIELD = "exerciseName"
    }

    private lateinit var binding: FragmentEditExerciseBinding

    private var exercise: Exercise? = null

    // Get the previous exercise data to update the workouts that contain it
    private var previousExercise: Exercise? = null
    private val mapper = jacksonObjectMapper()
    private var jsonPreviousExercise: String? = null

    private var userId: String? = null
    private var exerciseNameToUpdate: String? = null
    private var toUpdate = false

    // Firebase Auth, Firestore and utils
    private val userAuth: UserAuth by inject()
    private val exerciseViewModel: ExerciseViewModel by viewModel()
    private val myUtils: MyUtils by inject()

    private var itemSeriesAdapter: ItemSeriesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_exercise, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid

        exercise = arguments?.getParcelable(EXERCISE)
        Log.d(TAG, "exercise = $exercise")

        if (exercise != null) {
            // If the user clicked on an exercise from the previous list, bind data with this exercise
            getExerciseFromBundle(exercise)
        } else {
            // Else create an empty new exercise
            exercise = Exercise()
            binding.exercise = exercise
            // Make the exercise editable
            exercise?.editable = true
            // Add automatically the first series
            exercise?.seriesList?.add(Series())
            configureRecyclerView()
        }

        swipeToDeleteASeries()

        editExerciseFragmentFABAddSeries.setOnClickListener(this)
        editExerciseFragmentFABSave.setOnClickListener(this)
    }

    //--------------------------------------------------------------------------------------

    private fun getExerciseFromBundle(exercise: Exercise?) {
        // Get the Exercise object and convert it to JSON format
        jsonPreviousExercise = mapper.writeValueAsString(exercise)
        // Get data
        binding.exercise = exercise
        // Get initial workout name to compare it later
        exerciseNameToUpdate = exercise?.exerciseName
        toUpdate = true
        // Display data in the recyclerView
        configureRecyclerView()
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of series of the user
        itemSeriesAdapter =
            activity?.let {
                exercise?.seriesList?.let { seriesList ->
                    ItemSeriesAdapter(
                        seriesList, isDisabled = false, isForTrainingSession = false,
                        seriesDisabledName = null, noOfSeries = null, context = it
                    )
                }
            }
        // Attach the adapter to the recyclerView to populate the series
        editExerciseFragmentRecyclerView?.adapter = itemSeriesAdapter
        // Set layout manager to position the series
        editExerciseFragmentRecyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    //----------------------------------------------------------------------------------
    // Methods to remove a series

    private fun swipeToDeleteASeries() {
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
                // Retrieve the series object swiped
                val recentlyDeletedItem: Series? = exercise?.seriesList?.get(position)
                Log.d(TAG, "recentlyDeletedItem = $recentlyDeletedItem")
                itemSeriesAdapter?.deleteASeries(
                    editExerciseFragmentCoordinatorLayout, position, recentlyDeletedItem
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
        itemTouchHelper.attachToRecyclerView(editExerciseFragmentRecyclerView)
    }

    //----------------------------------------------------------------------------------
    // Methods to create or update an exercise

    private fun closeFragment() {
        activity?.let { myUtils.closeFragment(editExerciseFragmentProgressBar, it) }
        editExerciseFragmentFABSave?.isEnabled = false
        editExerciseFragmentFABAddSeries?.isEnabled = false
    }

    private fun createOrUpdateExercise() {
        if (checkIfExerciseNameIsEmpty()) {
            return
        }

        if (checkIfSeriesListIsEmpty()) {
            return
        }

        Log.d(TAG, "exercise = $exercise")
        userId?.let {
            // If it is an update and it is the same name, update the exercise
            if (exerciseNameToUpdate == exercise?.exerciseName && toUpdate) {
                createOrUpdateToFirestore(it)
            } else {
                // If the name is different and it is not an update,
                // check if an exerciseName already exists
                exerciseViewModel.getListOfExercises(it)
                    .whereEqualTo(EXERCISE_NAME_FIELD, exercise?.exerciseName)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            // There is no document with this exerciseName so create the exercise
                            Log.d(TAG, "documents.isEmpty")
                            createOrUpdateToFirestore(it)
                        } else {
                            // The same exercise name exists, choose another name
                            myUtils.showSnackBar(
                                editExerciseFragmentCoordinatorLayout,
                                R.string.exercise_name_already_exists
                            )
                            for (document in documents) {
                                Log.d(TAG, "${document.id} => ${document.data}")
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }
            }
        }
    }

    private fun createOrUpdateToFirestore(userId: String) {
        if (toUpdate) {
            // Update the exercise
            exercise?.let {
                // Convert the JSON data to Exercise object
                previousExercise = jsonPreviousExercise?.let { json -> mapper.readValue(json) }
                previousExercise?.let { previousExercise ->
                    Log.d(TAG, "previousExercise = $previousExercise")
                    exerciseViewModel.updateExercise(userId, previousExercise, it)
                        ?.addOnSuccessListener {
                            context?.getString(R.string.exercise_updated, exercise?.exerciseName)
                                ?.let { text ->
                                    myUtils.showSnackBar(
                                        editExerciseFragmentCoordinatorLayout, text
                                    )
                                }
                            Log.d(TAG, "DocumentSnapshot successfully updated!")
                            closeFragment()
                        }
                }
            }
        } else {
            // Create the exercise
            exercise?.let {
                exerciseViewModel.createExercise(userId, it)
                    .addOnSuccessListener { documentReference ->
                        // Set exerciseId
                        exerciseViewModel.updateExerciseIdAfterCreate(userId, documentReference)
                            .addOnSuccessListener {
                                Log.d(
                                    TAG, "DocumentSnapshot written with id: ${documentReference.id}"
                                )
                                // Inform the user
                                context?.getString(
                                    R.string.new_exercise_created, exercise?.exerciseName
                                )?.let { text ->
                                    myUtils.showSnackBar(
                                        editExerciseFragmentCoordinatorLayout, text
                                    )
                                }
                                closeFragment()
                            }
                    }
            }
        }
    }

    private fun checkIfExerciseNameIsEmpty(): Boolean {
        return if (exercise?.exerciseName.isNullOrEmpty() || exercise?.exerciseName.isNullOrBlank()) {
            myUtils.showSnackBar(
                editExerciseFragmentCoordinatorLayout, R.string.exercise_name_cannot_be_blank
            )
            true
        } else false
    }

    private fun checkIfSeriesListIsEmpty(): Boolean {
        return if (exercise?.seriesList.isNullOrEmpty()) {
            myUtils.showSnackBar(
                editExerciseFragmentCoordinatorLayout, R.string.sets_cannot_be_empty
            )
            true
        } else false
    }

    //----------------------------------------------------------------------------------

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.editExerciseFragmentFABAddSeries ->
                itemSeriesAdapter?.addASeries(editExerciseFragmentRecyclerView)
            R.id.editExerciseFragmentFABSave -> createOrUpdateExercise()
        }
    }
}