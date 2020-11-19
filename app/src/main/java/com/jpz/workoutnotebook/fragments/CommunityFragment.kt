package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ItemCommunityAdapter
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.repositories.UserAuth
import com.jpz.workoutnotebook.viewmodels.FollowViewModel
import kotlinx.android.synthetic.main.fragment_community.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class CommunityFragment : Fragment() {

    // Firebase Auth, Firestore
    private val userAuth: UserAuth by inject()
    private val followViewModel: FollowViewModel by viewModel()

    private var userId: String? = null

    private var itemCommunityAdapter: ItemCommunityAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid

        userId?.let { configureRecyclerView(it) }
    }

    //----------------------------------------------------------------------------------

    // Configure RecyclerView with a Query
    private fun configureRecyclerView(userId: String) {
        // Create the adapter by passing the list of follow of the user
        val list: Query? = followViewModel.getListOfFollow(userId)

        if (list != null) {
            itemCommunityAdapter =
                generateOptionsForCommunityAdapter(list)?.let { ItemCommunityAdapter(it) }
        }
        // Attach the adapter to the recyclerView to populate the exercises
        communityFragmentFollowRecyclerView?.adapter = itemCommunityAdapter
        // Set layout manager to position the exercises or workouts
        communityFragmentFollowRecyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    // Create options for RecyclerView from a Query
    private fun generateOptionsForCommunityAdapter(query: Query): FirestoreRecyclerOptions<User?>? {
        return FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .setLifecycleOwner(this)
            .build()
    }
}