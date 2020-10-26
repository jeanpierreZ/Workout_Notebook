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
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.viewholders.ItemExerciseViewHolder


class ItemExerciseAdapter(
    options: FirestoreRecyclerOptions<Exercise?>, private var callback: Listener
) : FirestoreRecyclerAdapter<Exercise, ItemExerciseViewHolder>(options) {

    companion object {
        private val TAG = ItemExerciseAdapter::class.java.simpleName
    }

    // Callback
    interface Listener {
        fun onClickExercise(exercise: Exercise?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemExerciseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.sport_item, parent, false)
        return ItemExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemExerciseViewHolder, position: Int, model: Exercise) =
        holder.updateExercise(model, callback)

    fun deleteAnExercise(position: Int, context: Context, coordinatorLayout: CoordinatorLayout) {
        // Get the documentSnapshot from the position and convert it to Exercise object
        snapshots.getSnapshot(position).reference.get().addOnSuccessListener { documentSnapshot ->

            // Convert it to Exercise object
            val recentlyDeletedItem: Exercise? = documentSnapshot.toObject(Exercise::class.java)

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
        recentlyDeletedItem: Exercise, documentReference: DocumentReference
    ) {
        val snackbar: Snackbar = Snackbar.make(
            coordinatorLayout, context.getString(R.string.exercise_deleted),
            Snackbar.LENGTH_LONG
        )
        // Set action to undo delete the exercise swiped
        snackbar.setAction(context.getString(R.string.undo)) {
            undoDelete(documentReference, recentlyDeletedItem)
        }
        snackbar.show()
    }

    private fun undoDelete(documentReference: DocumentReference, recentlyDeletedItem: Exercise) {
        documentReference.set(recentlyDeletedItem)
    }
}