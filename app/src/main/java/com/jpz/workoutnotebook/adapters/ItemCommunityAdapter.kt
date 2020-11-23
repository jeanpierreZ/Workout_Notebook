package com.jpz.workoutnotebook.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jpz.workoutnotebook.databinding.CommunityItemBinding
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.viewholders.ItemCommunityViewHolder

class ItemCommunityAdapter(
    options: FirestoreRecyclerOptions<User?>, private var callback: Listener
) : FirestoreRecyclerAdapter<User, ItemCommunityViewHolder>(options) {

    // Callback
    interface Listener {
        fun onClickProfile(user: User?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCommunityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = CommunityItemBinding.inflate(inflater, parent, false)
        return ItemCommunityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemCommunityViewHolder, position: Int, model: User) {
        holder.updateUser(model, callback)
    }
}