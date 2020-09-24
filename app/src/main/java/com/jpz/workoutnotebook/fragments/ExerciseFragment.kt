package com.jpz.workoutnotebook.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ItemExerciseAdapter
import com.jpz.workoutnotebook.api.UserAuth
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.viewmodels.ExerciseViewModel
import kotlinx.android.synthetic.main.fragment_exercise.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ExerciseFragment : Fragment(), ItemExerciseAdapter.Listener {

    companion object {
        const val EDIT_EXERCISE_FRAGMENT = "EDIT_EXERCISE_FRAGMENT"
    }

    // Firebase Auth, Firestore
    private val userAuth: UserAuth by inject()
    private val exerciseViewModel: ExerciseViewModel by viewModel()

    private var itemExerciseAdapter: ItemExerciseAdapter? = null

    private var callback: ExerciseListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exerciseFragmentFABAdd.setOnClickListener {
            callback?.onClickedExercise(EDIT_EXERCISE_FRAGMENT, null)
        }

        exerciseFragmentFABCancel.setOnClickListener {
            activity?.finish()
        }

        val userId = userAuth.getCurrentUser()?.uid
        userId?.let { configureRecyclerView(it) }
    }

    //----------------------------------------------------------------------------------

    //Configure RecyclerView with a Query
    private fun configureRecyclerView(userId: String) {
        // Create the adapter by passing the list of exercises of the user
        val list = exerciseViewModel.getListOfExercises(userId)
        if (list != null) {
            itemExerciseAdapter =
                generateOptionsForAdapter(list)?.let { ItemExerciseAdapter(it, this) }
        }
        // Attach the adapter to the recyclerView to populate the exercises
        exerciseFragmentRecyclerView?.adapter = itemExerciseAdapter
        // Set layout manager to position the exercises
        exerciseFragmentRecyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    // Create options for RecyclerView from a Query
    private fun generateOptionsForAdapter(query: Query): FirestoreRecyclerOptions<Exercise?>? {
        return FirestoreRecyclerOptions.Builder<Exercise>()
            .setQuery(query, Exercise::class.java)
            .setLifecycleOwner(this)
            .build()
    }

    //----------------------------------------------------------------------------------

    // Interface for callback from ItemExerciseAdapter
    override fun onClickExercise(exerciseId: String?, position: Int) {
        Toast.makeText(activity, "Click on $position where name is $exerciseId", Toast.LENGTH_SHORT)
            .show()
        if (exerciseId != null) {
            callback?.onClickedExercise(EDIT_EXERCISE_FRAGMENT, exerciseId)
        }
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods when click on add button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the methods that creating callback after being attached to parent activity
        callbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface ExerciseListener {
        fun onClickedExercise(edit: String, id: String?)
    }

    // Create callback to parent activity
    private fun callbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callback = activity as ExerciseListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement ExerciseListener")
        }
    }
}