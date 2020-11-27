package com.jpz.workoutnotebook.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.viewholders.ItemHistoricalViewHolder

class ItemHistoricalAdapter(
    private val list: ArrayList<TrainingSession>, private var context: Context
) : RecyclerView.Adapter<ItemHistoricalViewHolder>() {
    // RecyclerView used only to display historical training sessions

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHistoricalViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.historical_item, parent, false)
        return ItemHistoricalViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemHistoricalViewHolder, position: Int) {
        holder.updateHistoricalTrainingSession(list[position], context)
    }
}