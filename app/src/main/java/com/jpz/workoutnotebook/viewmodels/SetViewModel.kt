package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.api.SetHelper
import com.jpz.workoutnotebook.models.Set

class SetViewModel(private val setHelper: SetHelper) : ViewModel() {

    // --- CREATE ---

    fun createSet(setId: String, setName: String?, reps: Int?, unit: String?, numberOfUnit: Int?) =
        setHelper.createSet(setId, setName, reps, unit, numberOfUnit)
            ?.addOnFailureListener { e ->
                Log.e("createSet", "Error writing document", e)
            }

    // --- READ ---

    fun getSet(setId: String) = setHelper.getSet(setId)?.addOnFailureListener { e ->
        Log.d("getSet", "get failed with ", e)
    }

    // --- UPDATE ---

    fun updateSet(set: Set) = setHelper.updateSet(set)?.addOnFailureListener { e ->
        Log.e("updateSet", "Error updating document", e)
    }

    // --- DELETE ---

    fun deleteSet(setId: String) = setHelper.deleteSet(setId)?.addOnFailureListener { e ->
        Log.e("deleteSet", "Error deleting document", e)
    }
}