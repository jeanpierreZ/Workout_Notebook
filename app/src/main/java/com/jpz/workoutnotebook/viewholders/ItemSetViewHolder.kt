package com.jpz.workoutnotebook.viewholders

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.databinding.SetItemBinding
import com.jpz.workoutnotebook.models.Set
import com.jpz.workoutnotebook.models.Unit


class ItemSetViewHolder(private val binding: SetItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    // Represent a line of a set in the RecyclerView

    private var setName: TextView? = null
    private var setItemUnit: TextInputLayout? = null
    private var setItemNumberOfUnit: TextInputLayout? = null

    init {
        setName = itemView.findViewById(R.id.setItemName)
        setItemUnit = itemView.findViewById(R.id.setItemUnit)
        setItemNumberOfUnit = itemView.findViewById(R.id.setItemNumberOfUnit)
    }

    fun updateSets(set: Set, context: Context) {
        binding.set = set
        // Rename setName
        val setNamePosition = adapterPosition + 1
        set.setName = context.getString(R.string.number_set, setNamePosition)
        // Show menu from setItemUnit to choose a unit
        dropDownMenu(context)
    }

    private fun dropDownMenu(context: Context) {
        val items = listOf(
            Unit.KG.stringValue, Unit.LB.stringValue, Unit.M.stringValue, Unit.KM.stringValue,
            Unit.FT.stringValue, Unit.YD.stringValue, Unit.ML.stringValue
        )
        val adapter = ArrayAdapter(context, R.layout.unit_list_item, items)
        (setItemUnit?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }
}