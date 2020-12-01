package com.jpz.workoutnotebook.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.viewholders.ItemExerciseViewHolder

class ItemExerciseFromWorkoutAdapter(
    private val list: ArrayList<Exercise>, private val context: Context
) : RecyclerView.Adapter<ItemExerciseViewHolder>() {
    // RecyclerView used only to interact with the list of exercises chosen for a workout

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemExerciseViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.sport_item, parent, false)
        return ItemExerciseViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemExerciseViewHolder, position: Int) {
        holder.updateExerciseFromWorkout(context, list[position])
    }

    fun addAnExercise(exercise: Exercise, recyclerView: RecyclerView) {
        // Add an exercise to the list
        list.add(itemCount, exercise)
        // Notify that the data has changed
        notifyItemInserted(itemCount)
        // Scroll to the bottom of the list
        recyclerView.smoothScrollToPosition(itemCount)
    }

    fun deleteAnExercise(
        coordinatorLayout: CoordinatorLayout, position: Int, recentlyDeletedItem: Exercise?
    ) {
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)

        showUndoSnackbar(coordinatorLayout, position, recentlyDeletedItem)
    }

    private fun showUndoSnackbar(
        coordinatorLayout: CoordinatorLayout, position: Int, recentlyDeletedItem: Exercise?
    ) {
        val snackbar: Snackbar = Snackbar.make(
            coordinatorLayout, context.getString(R.string.exercise_deleted), Snackbar.LENGTH_LONG
        )
        // Set action to undo delete the series swiped
        snackbar.setAction(context.getString(R.string.undo)) {
            undoDelete(position, recentlyDeletedItem)
        }
        snackbar.show()
    }

    private fun undoDelete(position: Int, recentlyDeletedItem: Exercise?) {
        recentlyDeletedItem?.let { list.add(position, it) }
        notifyItemInserted(position)
        notifyItemRangeChanged(position, itemCount)
    }
}