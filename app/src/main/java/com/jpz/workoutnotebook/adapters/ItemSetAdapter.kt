package com.jpz.workoutnotebook.adapters

import android.content.ClipData.Item
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.databinding.SetItemBinding
import com.jpz.workoutnotebook.models.Set
import com.jpz.workoutnotebook.viewholders.ItemSetViewHolder


/*class ItemSetAdapter(
    private var list: List<Set>, private var context: Context
) : RecyclerView.Adapter<ItemSetViewHolder>() {*/
class ItemSetAdapter(private var list: List<Set>, private var context: Context) :
    RecyclerView.Adapter<ItemSetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSetViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        return ItemSetViewHolder(SetItemBinding.inflate(inflater, parent, false))

       /* return ItemSetViewHolder(
            SetItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )*/

    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemSetViewHolder, position: Int) {
        holder.updateSets(list[position], context)
    }

    fun setList(setList: ArrayList<Set>) {
        list = setList
        notifyDataSetChanged()
    }
}