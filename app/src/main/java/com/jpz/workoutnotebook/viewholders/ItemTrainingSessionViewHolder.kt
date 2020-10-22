package com.jpz.workoutnotebook.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ItemTrainingSessionAdapter
import com.jpz.workoutnotebook.databinding.TrainingSessionItemBinding
import com.jpz.workoutnotebook.models.TrainingSession
import java.lang.ref.WeakReference

class ItemTrainingSessionViewHolder(private val binding: TrainingSessionItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    // Represent a line of a training session in the RecyclerView

    private var workoutName: MaterialTextView? = null
    private var time: MaterialTextView? = null

    init {
        workoutName = itemView.findViewById(R.id.trainingSessionItemWorkoutName)
        time = itemView.findViewById(R.id.trainingSessionItemTime)
    }

    fun updateTrainingSession(
        trainingSession: TrainingSession,
        callback: ItemTrainingSessionAdapter.Listener
    ) {
        binding.trainingSession = trainingSession

        // Create a new weak Reference to our Listener
        val callbackWeakRef: WeakReference<ItemTrainingSessionAdapter.Listener> =
            WeakReference(callback)
        // Redefine callback to use it with lambda
        val finalCallback: ItemTrainingSessionAdapter.Listener? = callbackWeakRef.get()
        // Implement Listener
        itemView.setOnClickListener {
            // When a click happens, we fire our listener to get the training session position in the list
            if (finalCallback != null && adapterPosition != RecyclerView.NO_POSITION) {
                finalCallback.onClickTrainingSession(trainingSession, adapterPosition)
            }
        }
    }
}