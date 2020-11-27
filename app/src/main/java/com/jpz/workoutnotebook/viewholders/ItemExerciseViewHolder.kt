package com.jpz.workoutnotebook.viewholders

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ItemExerciseAdapter
import com.jpz.workoutnotebook.adapters.ItemSeriesAdapter
import com.jpz.workoutnotebook.models.Exercise
import java.lang.ref.WeakReference

class ItemExerciseViewHolder(exercise: View) : RecyclerView.ViewHolder(exercise) {
    // Represent a line of an exercise in the RecyclerView

    private var exerciseName: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var itemSeriesAdapter: ItemSeriesAdapter? = null

    init {
        exerciseName = itemView.findViewById(R.id.sportItemName)
        recyclerView = itemView.findViewById(R.id.sportItemSeriesRecyclerView)
    }

    // Used in ItemExerciseAdapter to display, access or delete an exercise in ListSportsFragment
    fun updateExercise(exercise: Exercise?, callback: ItemExerciseAdapter.Listener) {
        exerciseName?.text = exercise?.exerciseName

        // Create a new weak Reference to our Listener
        val callbackWeakRef: WeakReference<ItemExerciseAdapter.Listener> = WeakReference(callback)
        // Redefine callback to use it with lambda
        val finalCallback: ItemExerciseAdapter.Listener? = callbackWeakRef.get()
        // Implement Listener
        itemView.setOnClickListener {
            // When a click happens, we fire our listener to get the exercise position in the list
            if (finalCallback != null && adapterPosition != RecyclerView.NO_POSITION) {
                finalCallback.onClickExercise(exercise, adapterPosition)
            }
        }
    }

    // Used in ItemExerciseFromWorkoutAdapter to display exercise data in EditWorkoutFragment
    fun updateExerciseFromWorkout(context: Context, exercise: Exercise?) {
        exerciseName?.text = exercise?.exerciseName
        recyclerView?.visibility = View.VISIBLE
        configureRecyclerView(context, exercise)
    }

    // RecyclerView to display disabled series in updateExerciseFromWorkout()
    private fun configureRecyclerView(context: Context, exercise: Exercise?) {
        // Create the adapter by passing the list of series
        itemSeriesAdapter = exercise?.seriesList?.let { seriesList ->
            ItemSeriesAdapter(
                seriesList, isDisabled = true, isForTrainingSession = false,
                seriesDisabledName = null, noOfSeries = null, context = context
            )
        }
        // Attach the adapter to the recyclerView to populate the series
        recyclerView?.adapter = itemSeriesAdapter
        // Set layout manager to position the series
        recyclerView?.layoutManager = LinearLayoutManager(context)
    }
}