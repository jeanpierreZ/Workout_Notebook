package com.jpz.workoutnotebook.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.jpz.workoutnotebook.models.Set

class SetHelper {

    companion object {
        private const val COLLECTION_NAME = "sets"
        fun getSetsCollection(): CollectionReference? =
            FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
    }

    // --- CREATE ---

    fun createSet(
        setId: String, setName: String?, reps: Int?, unit: String?, numberOfUnit: Int?
    ): Task<Void>? {
        val setToCreate = Set(setId, setName, reps, unit, numberOfUnit)
        return getSetsCollection()?.document(setId)?.set(setToCreate)
    }

    // --- READ ---

    fun getSet(setId: String): Task<DocumentSnapshot>? =
        getSetsCollection()?.document(setId)?.get()

    // --- UPDATE ---

    fun updateSet(set: Set): Task<Void>? {
        return getSetsCollection()?.document(set.setId)?.set(set)
    }

    // --- DELETE ---

    fun deleteSet(setId: String): Task<Void?>? {
        return getSetsCollection()?.document(setId)?.delete()
    }
}