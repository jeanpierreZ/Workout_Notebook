package com.jpz.workoutnotebook.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.EditActivity


class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showSearchView()
    }

    //--------------------------------------------------------------------------------------
    // UI

    private fun showSearchView() {
        val searchView = (context as EditActivity).findViewById<SearchView>(R.id.toolbarSearchView)
        // Change the color of the search icon and make searchView visible
        val searchIcon: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.colorFilter = BlendModeColorFilterCompat
            .createBlendModeColorFilterCompat(Color.BLACK, BlendModeCompat.SRC_ATOP)
        searchView.visibility = View.VISIBLE
    }
}