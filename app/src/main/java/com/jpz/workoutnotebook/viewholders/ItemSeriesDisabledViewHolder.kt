package com.jpz.workoutnotebook.viewholders

import android.content.Context
import android.graphics.Color
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.databinding.SeriesItemBinding
import com.jpz.workoutnotebook.models.Series


class ItemSeriesDisabledViewHolder(private val binding: SeriesItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    // Represent a line of a series, without user interaction, in the RecyclerView

    private var seriesName: TextView? = null
    private var seriesItemReps: TextInputLayout? = null
    private var seriesItemUnit: TextInputLayout? = null
    private var seriesItemNumberOfUnit: TextInputLayout? = null
    private var seriesItemAutoCompleteTextView: AutoCompleteTextView? = null

    init {
        seriesName = itemView.findViewById(R.id.seriesItemName)
        seriesItemReps = itemView.findViewById((R.id.seriesItemReps))
        seriesItemUnit = itemView.findViewById(R.id.seriesItemUnit)
        seriesItemNumberOfUnit = itemView.findViewById(R.id.seriesItemNumberOfUnit)
        seriesItemAutoCompleteTextView = itemView.findViewById(R.id.seriesItemAutoCompleteTextView)
    }

    fun updateSeriesDisabled(series: Series, seriesDisabledName: String?, context: Context) {
        binding.series = series
        // Rename setName
        seriesName?.text = seriesDisabledName
        seriesName?.setTextColor(
            ContextCompat.getColor(context.applicationContext,R.color.colorSeriesDisabled))

        seriesItemReps?.editText?.isEnabled = false
                seriesItemReps?.editText?.setTextColor(
                    ContextCompat.getColor(context.applicationContext,R.color.colorSeriesDisabled))

        seriesItemUnit?.editText?.isEnabled = false
        seriesItemUnit?.editText?.setTextColor(
            ContextCompat.getColor(context.applicationContext,R.color.colorSeriesDisabled))

        seriesItemNumberOfUnit?.editText?.isEnabled = false
        seriesItemNumberOfUnit?.editText?.setTextColor(
            ContextCompat.getColor(context.applicationContext,R.color.colorSeriesDisabled))

        seriesItemUnit?.isEndIconVisible = false
    }
}