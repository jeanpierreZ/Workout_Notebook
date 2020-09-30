package com.jpz.workoutnotebook.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.databinding.SeriesItemBinding
import com.jpz.workoutnotebook.models.Series
import com.jpz.workoutnotebook.viewholders.ItemSeriesViewHolder


class ItemSeriesAdapter(private var list: List<Series>, private var context: Context) :
    RecyclerView.Adapter<ItemSeriesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSeriesViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        return ItemSeriesViewHolder(SeriesItemBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemSeriesViewHolder, position: Int) {
        holder.updateSeries(list[position], context)
    }
}