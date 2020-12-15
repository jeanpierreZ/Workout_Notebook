package com.jpz.workoutnotebook.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.databinding.CommunityItemBinding
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.viewholders.ItemCommunityViewHolder

class ItemCommunityAdapter(
    private val list: ArrayList<User>, private val callbackFollow: FollowListener,
    private val isFollowed: Boolean
) : RecyclerView.Adapter<ItemCommunityViewHolder>() {
    // RecyclerView used only to interact with the list of people followed and the followers in CommunityFragment

    // Callback
    interface FollowListener {
        fun onClickFollow(user: User?, position: Int, viewClicked: View?, isFollowed: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCommunityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = CommunityItemBinding.inflate(inflater, parent, false)
        return ItemCommunityViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemCommunityViewHolder, position: Int) =
        holder.updateFollow(list[position], callbackFollow, isFollowed)
}