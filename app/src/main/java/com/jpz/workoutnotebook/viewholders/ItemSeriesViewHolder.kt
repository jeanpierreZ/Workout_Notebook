package com.jpz.workoutnotebook.viewholders

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.databinding.SeriesItemBinding
import com.jpz.workoutnotebook.models.Series
import com.jpz.workoutnotebook.models.Unit


class ItemSeriesViewHolder(private val binding: SeriesItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    // Represent a line of a set in the RecyclerView

    private var seriesName: TextView? = null
    private var seriesItemUnit: TextInputLayout? = null
    private var seriesItemNumberOfUnit: TextInputLayout? = null
    private var seriesItemAutoCompleteTextView: AutoCompleteTextView? = null

    init {
        seriesName = itemView.findViewById(R.id.seriesItemName)
        seriesItemUnit = itemView.findViewById(R.id.seriesItemUnit)
        seriesItemNumberOfUnit = itemView.findViewById(R.id.seriesItemNumberOfUnit)
        seriesItemAutoCompleteTextView = itemView.findViewById(R.id.seriesItemAutoCompleteTextView)
    }

    fun updateSeries(series: Series, context: Context) {
        binding.series = series
        // Rename setName
        val setNamePosition = adapterPosition.plus(1)
        series.seriesName = context.getString(R.string.number_set, setNamePosition)
        // Show menu from setItemUnit to choose a unit
        dropDownMenu(context, series)
    }

    fun updateSeriesForTrainingSession(series: Series, noOfSeries: Int?, context: Context) {
        binding.series = series
        // Rename setName
        val setNamePosition = noOfSeries?.plus(1)
        series.seriesName = context.getString(R.string.number_set, setNamePosition)
        // Show menu from setItemUnit to choose a unit
        dropDownMenu(context, series)
    }

    private fun dropDownMenu(context: Context, series: Series) {
        val items = listOf(
            Unit.KG.stringValue, Unit.LB.stringValue, Unit.M.stringValue, Unit.KM.stringValue,
            Unit.FT.stringValue, Unit.YD.stringValue, Unit.ML.stringValue, Unit.BLANK.stringValue
        )
        val adapter =
            ArrayAdapter(context, R.layout.unit_list_item, R.id.unitListItemTextView, items)
        seriesItemAutoCompleteTextView?.setText(series.unit, false)
        (seriesItemUnit?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }
}