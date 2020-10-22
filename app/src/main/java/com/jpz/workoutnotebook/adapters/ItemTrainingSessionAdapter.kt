package com.jpz.workoutnotebook.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.databinding.TrainingSessionItemBinding
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.viewholders.ItemTrainingSessionViewHolder

class ItemTrainingSessionAdapter(
    private var list: ArrayList<TrainingSession>,
    private var context: Context, private val callback: Listener
) : RecyclerView.Adapter<ItemTrainingSessionViewHolder>() {
    // RecyclerView used only to interact with the list of training sessions

    // Callback
    interface Listener {
        fun onClickTrainingSession(trainingSession: TrainingSession?, position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ItemTrainingSessionViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        return ItemTrainingSessionViewHolder(
            TrainingSessionItemBinding.inflate(inflater, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemTrainingSessionViewHolder, position: Int) {
        holder.updateTrainingSession(list[position], callback)
    }
}