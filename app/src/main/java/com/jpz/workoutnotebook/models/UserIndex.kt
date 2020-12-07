package com.jpz.workoutnotebook.models

import com.algolia.search.model.ObjectID
import com.algolia.search.model.indexing.Indexable
import kotlinx.serialization.Serializable

@Serializable
data class UserIndex(
    // This class is used only for Algolia index
    var nickName: String? = null,
    var name: String? = null,
    var firstName: String? = null,
    override val objectID: ObjectID
) : Indexable
