package com.jpz.workoutnotebook.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.viewholders.ItemTrainingSessionViewHolder

class ItemTrainingSessionAdapter(
    private val list: ArrayList<TrainingSession>, private val callback: Listener
) : RecyclerView.Adapter<ItemTrainingSessionViewHolder>() {
    // RecyclerView used only to interact with the list of training sessions in CalendarFragment

    // Callback
    interface Listener {
        fun onClickTrainingSession(trainingSession: TrainingSession, position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ItemTrainingSessionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.training_session_item, parent, false)
        return ItemTrainingSessionViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemTrainingSessionViewHolder, position: Int) {
        holder.updateTrainingSession(list[position], callback)
    }
}