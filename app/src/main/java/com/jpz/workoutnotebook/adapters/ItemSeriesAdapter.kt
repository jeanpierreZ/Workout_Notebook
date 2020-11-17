package com.jpz.workoutnotebook.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.databinding.SeriesItemBinding
import com.jpz.workoutnotebook.models.Series
import com.jpz.workoutnotebook.viewholders.ItemSeriesDisabledViewHolder
import com.jpz.workoutnotebook.viewholders.ItemSeriesViewHolder


class ItemSeriesAdapter(
    private var list: ArrayList<Series>, private val isDisabled: Boolean,
    private val isForTrainingSession: Boolean, private val seriesDisabledName: String?,
    private val noOfSeries: Int?, private var context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_SERIES = 1
        const val TYPE_SERIES_DISABLED = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = SeriesItemBinding.inflate(inflater, parent, false)
        return if (viewType == TYPE_SERIES) {
            ItemSeriesViewHolder(view)
        } else {
            // For disabled item series
            ItemSeriesDisabledViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_SERIES) {
            if (isForTrainingSession) {
                (holder as ItemSeriesViewHolder).updateSeriesForTrainingSession(
                    list[position], noOfSeries, context
                )
            } else {
                (holder as ItemSeriesViewHolder).updateSeries(list[position], context)
            }
        } else {
            // For disabled item series
            (holder as ItemSeriesDisabledViewHolder).updateSeriesDisabled(
                list[position], seriesDisabledName, context
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isDisabled) {
            TYPE_SERIES_DISABLED
        } else {
            TYPE_SERIES
        }
    }

    override fun getItemCount(): Int = list.size

    fun addASeries(recyclerView: RecyclerView) {
        // Add a new series to the list
        list.add(itemCount, Series())
        // Notify that the data has changed
        notifyItemInserted(itemCount)
        // Scroll to the bottom of the list
        recyclerView.smoothScrollToPosition(itemCount)
    }

    fun deleteASeries(
        coordinatorLayout: CoordinatorLayout, position: Int, recentlyDeletedItem: Series?
    ) {
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)

        showUndoSnackbar(coordinatorLayout, position, recentlyDeletedItem)
    }

    private fun showUndoSnackbar(
        coordinatorLayout: CoordinatorLayout, position: Int, recentlyDeletedItem: Series?
    ) {
        val snackbar: Snackbar = Snackbar.make(
            coordinatorLayout, context.getString(R.string.series_deleted), Snackbar.LENGTH_LONG
        )
        // Set action to undo delete the series swiped
        snackbar.setAction(context.getString(R.string.undo)) {
            undoDelete(position, recentlyDeletedItem)
        }
        snackbar.show()
    }

    private fun undoDelete(position: Int, recentlyDeletedItem: Series?) {
        recentlyDeletedItem?.let { list.add(position, it) }
        notifyItemInserted(position)
        notifyItemRangeChanged(position, itemCount)
    }
}