package com.jpz.workoutnotebook.viewholders

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ItemExerciseFromWorkoutAdapter
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.models.Workout
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ItemHistoricalViewHolder(trainingSession: View) : RecyclerView.ViewHolder(trainingSession) {
    // Represent a line of a past training session in the RecyclerView

    private var workoutName: MaterialTextView? = null
    private var time: MaterialTextView? = null
    private var recyclerView: RecyclerView? = null
    private var itemExerciseFromWorkoutAdapter: ItemExerciseFromWorkoutAdapter? = null

    init {
        workoutName = itemView.findViewById(R.id.historicalItemWorkoutName)
        time = itemView.findViewById(R.id.historicalItemDate)
        recyclerView = itemView.findViewById(R.id.historicalItemRecyclerView)
    }

    fun updateHistoricalTrainingSession(trainingSession: TrainingSession, context: Context) {
        workoutName?.text = trainingSession.workout?.workoutName

        // SimpleDateFormat is used get the format of the trainingSessionDate
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        trainingSession.trainingSessionDate?.let {
            val myDate = sdf.parse(it)
            myDate?.let {
                time?.text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(myDate)
            }
        }
        recyclerView?.visibility = View.VISIBLE
        configureRecyclerView(context, trainingSession.workout)
    }

    // RecyclerView to display exercises and series from the workout of the training session
    private fun configureRecyclerView(context: Context, workout: Workout?) {
        // Create the adapter by passing the list of exercises
        itemExerciseFromWorkoutAdapter = workout?.exercisesList?.let { exercisesList ->
            ItemExerciseFromWorkoutAdapter(exercisesList, context)
        }
        // Attach the adapter to the recyclerView to populate the exercises
        recyclerView?.adapter = itemExerciseFromWorkoutAdapter
        // Set layout manager to position the exercises
        recyclerView?.layoutManager = LinearLayoutManager(context)
    }
}