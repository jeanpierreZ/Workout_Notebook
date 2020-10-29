package com.jpz.workoutnotebook.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.Workout
import com.jpz.workoutnotebook.viewholders.ItemWorkoutViewHolder

class ItemWorkoutAdapter(
    options: FirestoreRecyclerOptions<Workout?>, private var callback: Listener
) : FirestoreRecyclerAdapter<Workout, ItemWorkoutViewHolder>(options) {

    companion object {
        private val TAG = ItemWorkoutAdapter::class.java.simpleName
    }

    // Callback
    interface Listener {
        fun onClickWorkout(workout: Workout?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemWorkoutViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.sport_item, parent, false)
        return ItemWorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemWorkoutViewHolder, position: Int, model: Workout) =
        holder.updateWorkout(model, callback)

    fun deleteAWorkout(position: Int, context: Context, coordinatorLayout: CoordinatorLayout) {
        // Get the documentSnapshot from the position and convert it to Workout object
        snapshots.getSnapshot(position).reference.get().addOnSuccessListener { documentSnapshot ->

            // Convert it to Workout object
            val recentlyDeletedItem: Workout? = documentSnapshot.toObject(Workout::class.java)

            // Get the documentReference from the position to delete and undo delete it
            val documentReference: DocumentReference = snapshots.getSnapshot(position).reference

            documentReference.delete()
                .addOnSuccessListener {
                    Log.d(TAG, "recentlyDeletedItem = $recentlyDeletedItem")
                    if (recentlyDeletedItem != null) {
                        showUndoSnackbar(
                            coordinatorLayout, context, recentlyDeletedItem, documentReference
                        )
                        Log.d(TAG, "DocumentSnapshot successfully deleted!")
                    }
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
    }

    private fun showUndoSnackbar(
        coordinatorLayout: CoordinatorLayout, context: Context,
        recentlyDeletedItem: Workout, documentReference: DocumentReference
    ) {
        val snackbar: Snackbar = Snackbar.make(
            coordinatorLayout, context.getString(R.string.workout_deleted), Snackbar.LENGTH_LONG
        )
        // Set action to undo delete the workout swiped
        snackbar.setAction(context.getString(R.string.undo)) {
            undoDelete(documentReference, recentlyDeletedItem)
        }
        snackbar.show()
    }

    private fun undoDelete(documentReference: DocumentReference, recentlyDeletedItem: Workout) {
        documentReference.set(recentlyDeletedItem)
    }
}