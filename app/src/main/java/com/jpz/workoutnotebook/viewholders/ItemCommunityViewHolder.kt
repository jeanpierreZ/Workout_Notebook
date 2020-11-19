package com.jpz.workoutnotebook.viewholders

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.databinding.CommunityItemBinding
import com.jpz.workoutnotebook.models.User

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
    fun updateUser(user: User?) {
        binding.user = user
    }
}