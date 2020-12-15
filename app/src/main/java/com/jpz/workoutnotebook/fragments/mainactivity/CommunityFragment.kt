package com.jpz.workoutnotebook.fragments.mainactivity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ItemCommunityAdapter
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.repositories.UserAuth
import com.jpz.workoutnotebook.viewmodels.FollowViewModel
import com.jpz.workoutnotebook.viewmodels.FollowingViewModel
import com.jpz.workoutnotebook.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_community.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class CommunityFragment : Fragment(), ItemCommunityAdapter.FollowListener {

    companion object {
        private val TAG = CommunityFragment::class.java.simpleName
    }

    // Firebase Auth, Firestore
    private val userAuth: UserAuth by inject()
    private val followViewModel: FollowViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()
    private val followingViewModel: FollowingViewModel by viewModel()

    private var userId: String? = null

    private var itemCommunityAdapterFollowed: ItemCommunityAdapter? = null
    private var itemCommunityAdapterFollower: ItemCommunityAdapter? = null

    private var callbackFollow: CommunityListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid

        userId?.let { configureRecyclerViews(it) }
    }

    override fun onResume() {
        super.onResume()
        userId?.let { configureRecyclerViews(it) }
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerViews

    private fun configureRecyclerViews(userId: String) {
        configureRecyclerViewFollowed(userId)
        configureRecyclerViewFollowers(userId)
    }

    private fun configureRecyclerViewFollowed(userId: String) {
        // Create the adapter by passing the list of people followed by the user
        val list = arrayListOf<User>()
        itemCommunityAdapterFollowed = ItemCommunityAdapter(list, this, true)
        // Attach the adapter to the recyclerView to populate the people followed
        communityFragmentFollowRecyclerView?.adapter = itemCommunityAdapterFollowed
        // Set layout manager to position the list data
        communityFragmentFollowRecyclerView?.layoutManager = LinearLayoutManager(activity)

        followViewModel.getListOfPeopleFollowed(userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    userViewModel.getUser(document.id)
                        .addOnSuccessListener { documentSnapshot ->
                            Log.d(TAG, "documentSnapshot => $documentSnapshot")
                            val followedObject = documentSnapshot.toObject(User::class.java)
                            // Add the people followed to the list
                            followedObject?.let { list.add(it) }
                            // Notify the adapter
                            itemCommunityAdapterFollowed?.notifyDataSetChanged()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun configureRecyclerViewFollowers(userId: String) {
        // Create the adapter by passing the list of followers of the user
        val listOfFollowers = arrayListOf<User>()
        itemCommunityAdapterFollower = ItemCommunityAdapter(listOfFollowers, this, false)
        // Attach the adapter to the recyclerView to populate the followers
        communityFragmentFollowersRecyclerView?.adapter = itemCommunityAdapterFollower
        // Set layout manager to position the list data
        communityFragmentFollowersRecyclerView?.layoutManager = LinearLayoutManager(activity)

        followingViewModel.getListOfFollowers(userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    userViewModel.getUser(document.id)
                        .addOnSuccessListener { documentSnapshot ->
                            Log.d(TAG, "documentSnapshot => $documentSnapshot")
                            val followerObject = documentSnapshot.toObject(User::class.java)
                            // Add the follower to the list
                            followerObject?.let { listOfFollowers.add(it) }
                            // Notify the adapter
                            itemCommunityAdapterFollower?.notifyDataSetChanged()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    //----------------------------------------------------------------------------------

    // Interface for callback ItemCommunityAdapter FollowListener
    override fun onClickFollow(
        user: User?, position: Int, viewClicked: View?, isFollowed: Boolean
    ) {
        callbackFollow?.displayFollow(user, viewClicked, isFollowed)
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods when click on follow item

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the method that creating callback after being attached to parent activity
        callbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface CommunityListener {
        fun displayFollow(user: User?, viewClicked: View?, isFollowed: Boolean)
    }

    // Create callback to parent activity
    private fun callbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callbackFollow = activity as CommunityListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement CommunityListener")
        }
    }
}