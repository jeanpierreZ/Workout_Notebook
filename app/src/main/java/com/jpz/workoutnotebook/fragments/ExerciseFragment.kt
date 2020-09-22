package com.jpz.workoutnotebook.fragments

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

    // Firebase Auth, Firestore
    private val userAuth: UserAuth by inject()
    private val exerciseViewModel: ExerciseViewModel by viewModel()

    private var itemExerciseAdapter: ItemExerciseAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        recyclerViewExerciseFragment?.adapter = itemExerciseAdapter
        // Set layout manager to position the exercises
        recyclerViewExerciseFragment?.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
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
    }
}