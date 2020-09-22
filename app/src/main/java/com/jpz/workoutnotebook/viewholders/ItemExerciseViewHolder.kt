package com.jpz.workoutnotebook.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ItemExerciseAdapter
import com.jpz.workoutnotebook.models.Exercise
import java.lang.ref.WeakReference

class ItemExerciseViewHolder(exercise: View) : RecyclerView.ViewHolder(exercise) {
    // Represent a line of an exercise in the RecyclerView

    private var exerciseName: TextView? = null

    init {
        exerciseName = itemView.findViewById(R.id.textExerciseItem)
    }

    fun updateExercises(exercise: Exercise?, callback: ItemExerciseAdapter.Listener) {
        exerciseName?.text = exercise?.exerciseName

        // Create a new weak Reference to our Listener
        val callbackWeakRef: WeakReference<ItemExerciseAdapter.Listener> = WeakReference(callback)
        //callback = callbackWeakRef.get()!!
        // Redefine callback to use it with lambda
        val finalCallback: ItemExerciseAdapter.Listener? = callbackWeakRef.get()
        // Implement Listener
        itemView.setOnClickListener {
            // When a click happens, we fire our listener to get the exercise position in the list
            if (finalCallback != null && adapterPosition != RecyclerView.NO_POSITION) {
                finalCallback.onClickExercise(exercise?.exerciseId, adapterPosition)
            }
        }
    }
}