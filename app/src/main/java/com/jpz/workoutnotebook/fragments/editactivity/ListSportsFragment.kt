package com.jpz.workoutnotebook.fragments.editactivity

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.EditActivity.Companion.IS_AN_EXERCISE
import com.jpz.workoutnotebook.adapters.ItemExerciseAdapter
import com.jpz.workoutnotebook.adapters.ItemWorkoutAdapter
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Workout
import com.jpz.workoutnotebook.repositories.UserAuth
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.ExerciseViewModel
import com.jpz.workoutnotebook.viewmodels.WorkoutViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_list_sports.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ListSportsFragment : Fragment(), ItemExerciseAdapter.Listener, ItemWorkoutAdapter.Listener {

    companion object {
        private val TAG = ListSportsFragment::class.java.simpleName
        private const val START_DELAY = 500L
    }

    // Firebase Auth, Firestore and utils
    private val userAuth: UserAuth by inject()
    private val exerciseViewModel: ExerciseViewModel by viewModel()
    private val workoutViewModel: WorkoutViewModel by viewModel()
    private val myUtils: MyUtils by inject()

    private var itemExerciseAdapter: ItemExerciseAdapter? = null
    private var itemWorkoutAdapter: ItemWorkoutAdapter? = null

    private var isAnExercise = false

    private var callback: ItemListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_sports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Boolean from EditActivity used to display UI for exercises or workouts
        isAnExercise = arguments?.getBoolean(IS_AN_EXERCISE)!!
        Log.d(TAG, "isAnExercise = $isAnExercise")

        if (isAnExercise) {
            listSportsFragmentTitle.setText(R.string.exercises)
        } else {
            listSportsFragmentTitle.setText(R.string.workouts)
        }

        myUtils.scaleViewAnimation(listSportsFragmentFABAdd, START_DELAY)

        listSportsFragmentFABAdd.setOnClickListener {
            if (isAnExercise) {
                callback?.addOrUpdateExercise(null)
            } else {
                callback?.addOrUpdateWorkout(null)
            }
        }

        val userId = userAuth.getCurrentUser()?.uid
        userId?.let { configureRecyclerView(isAnExercise, it) }
        swipeToDeleteAnItem()
    }

    //----------------------------------------------------------------------------------

    // Configure RecyclerView with a Query
    private fun configureRecyclerView(isAnExercise: Boolean, userId: String) {
        val list: Query?

        if (isAnExercise) {
            // Create the adapter by passing the list of exercises of the user
            list = exerciseViewModel.getOrderedListOfExercises(userId)
            itemExerciseAdapter = ItemExerciseAdapter(generateOptionsForExerciseAdapter(list), this)
            // Attach the adapter to the recyclerView to populate the exercises
            listSportsFragmentRecyclerView?.adapter = itemExerciseAdapter

        } else {
            // Create the adapter by passing the list of workouts of the user
            list = workoutViewModel.getOrderedListOfWorkouts(userId)
            itemWorkoutAdapter = ItemWorkoutAdapter(generateOptionsForWorkoutAdapter(list), this)
            // Attach the adapter to the recyclerView to populate the workouts
            listSportsFragmentRecyclerView?.adapter = itemWorkoutAdapter
        }

        // Set layout manager to position the exercises or workouts
        listSportsFragmentRecyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    // Create options for RecyclerView from a Query
    private fun generateOptionsForExerciseAdapter(query: Query): FirestoreRecyclerOptions<Exercise?> {
        return FirestoreRecyclerOptions.Builder<Exercise>()
            .setQuery(query, Exercise::class.java)
            .setLifecycleOwner(this)
            .build()
    }

    // Create options for RecyclerView from a Query
    private fun generateOptionsForWorkoutAdapter(query: Query): FirestoreRecyclerOptions<Workout?> {
        return FirestoreRecyclerOptions.Builder<Workout>()
            .setQuery(query, Workout::class.java)
            .setLifecycleOwner(this)
            .build()
    }

    //----------------------------------------------------------------------------------

    private fun swipeToDeleteAnItem() {
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
                activity?.let {
                    if (itemExerciseAdapter != null) {
                        itemExerciseAdapter?.deleteAnExercise(
                            viewHolder.adapterPosition, it, listSportsFragmentCoordinatorLayout
                        )
                    } else {
                        itemWorkoutAdapter?.deleteAWorkout(
                            viewHolder.adapterPosition, it, listSportsFragmentCoordinatorLayout
                        )
                    }
                }
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
        itemTouchHelper.attachToRecyclerView(listSportsFragmentRecyclerView)
    }

    //----------------------------------------------------------------------------------
    // Interface for callbacks Exercise and Workout Adapters

    override fun onClickExercise(exercise: Exercise?, position: Int) {
        exercise?.let { callback?.addOrUpdateExercise(it) }
    }

    override fun onClickWorkout(workout: Workout?, position: Int) {
        workout?.let { callback?.addOrUpdateWorkout(it) }
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods
    // when click on add button or on an item in the list

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the method that creating callback after being attached to parent activity
        callbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface ItemListener {
        fun addOrUpdateExercise(exercise: Exercise?)
        fun addOrUpdateWorkout(workout: Workout?)
    }

    // Create callback to parent activity
    private fun callbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callback = activity as ItemListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement ItemListener")
        }
    }
}