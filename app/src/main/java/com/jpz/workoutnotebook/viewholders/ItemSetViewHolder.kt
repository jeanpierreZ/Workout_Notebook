package com.jpz.workoutnotebook.viewholders

import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.databinding.SetItemBinding
import com.jpz.workoutnotebook.models.Set


class ItemSetViewHolder(private val binding: SetItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    // Represent a line of a set in the RecyclerView

    private var setName: TextView? = null
    private var setItemReps: TextInputLayout? = null
    private var setItemNumberOfUnit: TextInputLayout? = null

    init {
        setName = itemView.findViewById(R.id.setItemName)
        setItemReps = itemView.findViewById(R.id.setItemReps)
        setItemNumberOfUnit = itemView.findViewById(R.id.setItemNumberOfUnit)
    }

    fun updateSets(set: Set, context: Context) {
        binding.set = set
        val setNamePosition = adapterPosition + 1
        set.setName = context.getString(R.string.number_set, setNamePosition)
    }
}