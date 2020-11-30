package com.jpz.workoutnotebook.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.databinding.CommunityItemBinding
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.viewholders.ItemCommunityViewHolder

class ItemCommunityAdapter(
    private val list: ArrayList<User>, private val callback: FollowedListener,
    private val callbackFollower: FollowerListener, private val isFollowed: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // RecyclerView used only to interact with the list of people followed and the followers in CommunityFragment

    // Callbacks

    interface FollowedListener {
        fun onClickFollowed(followed: User?, position: Int)
    }

    interface FollowerListener {
        fun onClickFollower(follower: User?, position: Int)
    }

    companion object {
        const val TYPE_FOLLOWED = 1
        const val TYPE_FOLLOWER = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = CommunityItemBinding.inflate(inflater, parent, false)
        return ItemCommunityViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_FOLLOWED) {
            // For people followed
            (holder as ItemCommunityViewHolder).updateFollowed(list[position], callback)
        } else {
            // For follower
            (holder as ItemCommunityViewHolder).updateFollower(list[position], callbackFollower)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isFollowed) {
            TYPE_FOLLOWED
        } else {
            TYPE_FOLLOWER
        }
    }

    override fun getItemCount(): Int = list.size
}