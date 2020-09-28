package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ItemSetAdapter
import com.jpz.workoutnotebook.models.Set
import kotlinx.android.synthetic.main.fragment_edit_exercise.*


class EditExerciseFragment : Fragment() {

    companion object {
        private val TAG = EditExerciseFragment::class.java.simpleName
    }

    private var itemSetAdapter: ItemSetAdapter? = null
    private var setList: MutableList<Set> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Add automatically the first set
        setList.add(Set())

        configureRecyclerView()

        swipeToDeleteASet()

        editExerciseFragmentRestFABAdd.setOnClickListener {
            addASet()
        }

        editExerciseFragmentRestFABSave.setOnClickListener {
            Log.d(TAG, "setList = $setList")
        }
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of exercises of the user
        itemSetAdapter = activity?.let { ItemSetAdapter(setList, it) }
        // Attach the adapter to the recyclerView to populate the exercises
        editExerciseFragmentRecyclerView?.adapter = itemSetAdapter
        // Set layout manager to position the exercises
        editExerciseFragmentRecyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    //----------------------------------------------------------------------------------
    // Methods to add or remove a set

    private fun swipeToDeleteASet() {
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                removeASet(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(editExerciseFragmentRecyclerView)
    }

    private fun addASet() {
        // Retrieve the size of the list
        val setListSize: Int = setList.size
        // Add a new set to the list
        setList.add(setListSize, Set())
        // Notify the adapter that the data has changed
        itemSetAdapter?.notifyItemInserted(setListSize)
        // Scroll to the bottom
        editExerciseFragmentRecyclerView.smoothScrollToPosition(setListSize)
    }

    private fun removeASet(position: Int) {
        setList.removeAt(position)
        itemSetAdapter?.notifyItemRemoved(position)
        itemSetAdapter?.notifyItemRangeChanged(position, setList.size)
    }
}