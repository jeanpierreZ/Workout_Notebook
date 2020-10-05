package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.EditActivity.Companion.EXERCISE_NAME
import com.jpz.workoutnotebook.adapters.ItemSeriesAdapter
import com.jpz.workoutnotebook.api.UserAuth
import com.jpz.workoutnotebook.databinding.FragmentEditExerciseBinding
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Series
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.ExerciseViewModel
import kotlinx.android.synthetic.main.fragment_edit_exercise.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditExerciseFragment : Fragment(), View.OnClickListener {

    companion object {
        private val TAG = EditExerciseFragment::class.java.simpleName
        private const val EXERCISE_NAME_FIELD = "exerciseName"
    }

    private lateinit var binding: FragmentEditExerciseBinding

    private val exercise = Exercise()
    private var exerciseFromList = Exercise()
    private var userId: String? = null
    private var exerciseNameFromList: String? = null

    private val userAuth: UserAuth by inject()
    private val exerciseViewModel: ExerciseViewModel by viewModel()
    private val myUtils: MyUtils by inject()

    private var itemSeriesAdapter: ItemSeriesAdapter? = null
    private var seriesList: MutableList<Series> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_exercise, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid

        exerciseNameFromList = arguments?.getString(EXERCISE_NAME)
        Log.d(TAG, "exerciseNameFromList = $exerciseNameFromList")

        if (exerciseNameFromList != null) {
            // If the user click on an exercise in the list, bind data with this exercise
            userId?.let {
                exerciseViewModel.getExercise(it, exerciseNameFromList!!)
                    ?.addOnSuccessListener { documentSnapshot ->
                        exerciseFromList = documentSnapshot.toObject(Exercise::class.java)!!
                        Log.d(TAG, "exerciseFromList  = $exerciseFromList")
                        exerciseFromList.seriesList?.let { it1 -> seriesList.addAll(it1) }
                        binding.exercise = exerciseFromList
                    }
            }
        } else {
            // Else create an empty new exercise
            binding.exercise = exercise
            // Make the exercise editable
            exercise.editable = true
            // Attach the list of series
            exercise.seriesList = seriesList as ArrayList<Series>
            // Add automatically the first series
            seriesList.add(Series())
        }

        configureRecyclerView()
        swipeToDeleteASeries()

        editExerciseFragmentRestFABAddSeries.setOnClickListener(this)
        editExerciseFragmentRestFABSave.setOnClickListener(this)
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of series of the user
        itemSeriesAdapter = activity?.let { ItemSeriesAdapter(seriesList as ArrayList<Series>, it) }
        // Attach the adapter to the recyclerView to populate the series
        editExerciseFragmentRecyclerView?.adapter = itemSeriesAdapter
        // Set layout manager to position the series
        editExerciseFragmentRecyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    //----------------------------------------------------------------------------------
    // Methods to add or remove a series

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
                val recentlyDeletedItem: Series = seriesList[position]
                Log.d(TAG, "recentlyDeletedItem = $recentlyDeletedItem")
                itemSeriesAdapter?.deleteASeries(
                    editExerciseFragmentCoordinatorLayout, position, recentlyDeletedItem
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(editExerciseFragmentRecyclerView)
    }

    //----------------------------------------------------------------------------------
    // Methods to save or update an exercise

    private fun saveExercise() {
        if (exercise.exerciseName.isNullOrEmpty() || exercise.exerciseName.isNullOrBlank()) {
            Toast.makeText(
                activity, getString(R.string.exercise_name_cannot_be_blank),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Log.d(TAG, "exercise = $exercise")
            if (userId != null) {
                // Check if an exerciseName already exists
                exerciseViewModel.getListOfExercises(userId!!)
                    ?.whereEqualTo(EXERCISE_NAME_FIELD, exercise.exerciseName)
                    ?.get()
                    ?.addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            // There isn't document with this exerciseName so create the exercise
                            Log.d(TAG, "documents.isEmpty")
                            exerciseViewModel.createExercise(
                                editExerciseFragmentCoordinatorLayout,
                                userId!!, exercise.exerciseName, exercise.restNextSet,
                                exercise.restNextExercise, true, seriesList as ArrayList<Series>
                            )
                            closeFragment()
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
                    ?.addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }
            }
        }
    }

    private fun updateExercise() {
        Log.d(TAG, "seriesList in updateExercise() = $seriesList")
        userId?.let {
            exerciseViewModel.updateExercise(
                editExerciseFragmentCoordinatorLayout, it, exerciseFromList.exerciseName,
                exerciseFromList.restNextSet, exerciseFromList.restNextExercise,
                exerciseFromList.editable, seriesList as ArrayList<Series>
            )
        }
        closeFragment()
    }

    //----------------------------------------------------------------------------------

    private fun closeFragment() {
        editExerciseFragmentProgressBar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            activity?.onBackPressed()
        }, 2000)
    }

    //----------------------------------------------------------------------------------

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.editExerciseFragmentRestFABAddSeries ->
                itemSeriesAdapter?.addASeries(editExerciseFragmentRecyclerView)
            R.id.editExerciseFragmentRestFABSave -> {
                if (exerciseNameFromList != null) {
                    updateExercise()
                } else {
                    saveExercise()
                }
            }
        }
    }
}