package com.jpz.workoutnotebook.viewholders

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ItemCommunityAdapter
import com.jpz.workoutnotebook.adapters.ItemSearchAdapter
import com.jpz.workoutnotebook.databinding.CommunityItemBinding
import com.jpz.workoutnotebook.models.User
import java.lang.ref.WeakReference

class ItemCommunityViewHolder(private val binding: CommunityItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    // Represent a line of a user (follow / follower) in the RecyclerView

    private var photo: ImageView? = null
    private var nickname: TextView? = null
    private var firstName: TextView? = null
    private var name: TextView? = null

    init {
        photo = itemView.findViewById(R.id.communityItemPhoto)
        nickname = itemView.findViewById(R.id.communityItemNickname)
        firstName = itemView.findViewById(R.id.communityItemFirstName)
        name = itemView.findViewById(R.id.communityItemName)
    }

    // Used in ItemCommunityAdapter to display followers and people the user follows in CommunityFragment
    fun updateUser(user: User?, callback: ItemCommunityAdapter.Listener) {
        binding.user = user

        // Create a new weak Reference to our Listener
        val callbackWeakRef: WeakReference<ItemCommunityAdapter.Listener> = WeakReference(callback)
        // Redefine callback to use it with lambda
        val finalCallback: ItemCommunityAdapter.Listener? = callbackWeakRef.get()
        // Implement Listener
        itemView.setOnClickListener {
            // When a click happens, we fire our listener to get the user position in the list
            if (finalCallback != null && adapterPosition != RecyclerView.NO_POSITION) {
                finalCallback.onClickProfile(user, adapterPosition)
            }
        }
    }

    // Used in ItemSearchAdapter to display the users find from the query
    fun updateUserFromSearch(user: User?, callback: ItemSearchAdapter.Listener) {
        binding.user = user

        // Create a new weak Reference to our Listener
        val callbackWeakRef: WeakReference<ItemSearchAdapter.Listener> = WeakReference(callback)
        // Redefine callback to use it with lambda
        val finalCallback: ItemSearchAdapter.Listener? = callbackWeakRef.get()
        // Implement Listener
        itemView.setOnClickListener {
            // When a click happens, we fire our listener to get the user position in the list
            if (finalCallback != null && adapterPosition != RecyclerView.NO_POSITION) {
                finalCallback.onClickProfileAfterSearch(user, adapterPosition)
            }
        }
    }
}