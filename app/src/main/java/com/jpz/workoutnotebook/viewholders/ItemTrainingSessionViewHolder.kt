package com.jpz.workoutnotebook.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ItemTrainingSessionAdapter
import com.jpz.workoutnotebook.models.TrainingSession
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ItemTrainingSessionViewHolder(trainingSession: View) :
    RecyclerView.ViewHolder(trainingSession) {
    // Represent a line of a training session in the RecyclerView

    private var workoutName: MaterialTextView? = null
    private var time: MaterialTextView? = null

    init {
        workoutName = itemView.findViewById(R.id.trainingSessionItemWorkoutName)
        time = itemView.findViewById(R.id.trainingSessionItemTime)
    }

    fun updateTrainingSession(
        trainingSession: TrainingSession, callback: ItemTrainingSessionAdapter.Listener
    ) {
        workoutName?.text = trainingSession.workout?.workoutName

        // SimpleDateFormat is used get the format of the trainingSessionDate
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        trainingSession.trainingSessionDate?.let { it ->
            val myDate = sdf.parse(it)
            myDate?.let {
                time?.text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(myDate)
            }
        }

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