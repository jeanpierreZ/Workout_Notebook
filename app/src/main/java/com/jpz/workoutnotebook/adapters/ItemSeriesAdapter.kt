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
import com.jpz.workoutnotebook.viewholders.ItemSeriesViewHolder


class ItemSeriesAdapter(private var list: ArrayList<Series>, private var context: Context) :
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
            coordinatorLayout, context.getString(R.string.series_deleted),
            Snackbar.LENGTH_LONG
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