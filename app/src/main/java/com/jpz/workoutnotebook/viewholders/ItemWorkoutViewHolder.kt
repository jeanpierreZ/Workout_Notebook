package com.jpz.workoutnotebook.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ItemWorkoutAdapter
import com.jpz.workoutnotebook.models.Workout
import java.lang.ref.WeakReference

class ItemWorkoutViewHolder(workout: View) : RecyclerView.ViewHolder(workout) {
    // Represent a line of a workout in the RecyclerView

    private var workoutName: TextView? = null

    init {
        workoutName = itemView.findViewById(R.id.sportItemName)
    }

    fun updateWorkout(workout: Workout?, callback: ItemWorkoutAdapter.Listener) {
        workoutName?.text = workout?.workoutName

        // Create a new weak Reference to our Listener
        val callbackWeakRef: WeakReference<ItemWorkoutAdapter.Listener> = WeakReference(callback)
        // Redefine callback to use it with lambda
        val finalCallback: ItemWorkoutAdapter.Listener? = callbackWeakRef.get()
        // Implement Listener
        itemView.setOnClickListener {
            // When a click happens, we fire our listener to get the workout position in the list
            if (finalCallback != null && adapterPosition != RecyclerView.NO_POSITION) {
                finalCallback.onClickWorkout(workout?.workoutId, adapterPosition)
            }
        }
    }
}