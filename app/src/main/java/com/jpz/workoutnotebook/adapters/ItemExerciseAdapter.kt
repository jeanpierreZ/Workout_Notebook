package com.jpz.workoutnotebook.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.viewholders.ItemExerciseViewHolder


class ItemExerciseAdapter(
    options: FirestoreRecyclerOptions<Exercise?>, private var callback: Listener
) : FirestoreRecyclerAdapter<Exercise, ItemExerciseViewHolder>(options) {

    // Callback
    interface Listener {
        fun onClickExercise(exerciseName: String?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemExerciseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.exercise_item, parent, false)
        return ItemExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemExerciseViewHolder, position: Int, model: Exercise) =
        holder.updateExercises(model, callback)
}