package com.jpz.workoutnotebook.fragments.followeractivity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.FollowerActivity
import com.jpz.workoutnotebook.adapters.ItemSearchAdapter
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.repositories.UserAuth
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.FollowViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment(), ItemSearchAdapter.Listener {

    companion object {
        private val TAG = SearchFragment::class.java.simpleName
        private const val USER_NICKNAME_FIELD = "nickName"
        private const val USER_FIRST_NAME_FIELD = "firstName"
        private const val USER_NAME_FIELD = "name"
    }

    private var userId: String? = null

    // Firebase Auth, Firestore and utils
    private val userAuth: UserAuth by inject()
    private val followViewModel: FollowViewModel by viewModel()
    private val myUtils: MyUtils by inject()

    private var searchView: SearchView? = null

    private val resultList = arrayListOf<User>()

    private var itemSearchFragment: ItemSearchAdapter? = null

    private var callback: FollowListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView = (context as FollowerActivity).findViewById(R.id.toolbarSearchView)

        userId = userAuth.getCurrentUser()?.uid

        showSearchView()
        getResultFromQuery()
    }

    //--------------------------------------------------------------------------------------
    // UI

    // Configure RecyclerView, Adapter & LayoutManager
    private fun configureRecyclerView(listSorted: ArrayList<User>) {
        // Create the adapter by passing the list of users find from the query
        itemSearchFragment = ItemSearchAdapter(listSorted, this)
        // Attach the adapter to the recyclerView to populate the users
        searchFragmentRecyclerView?.adapter = itemSearchFragment
        // Set layout manager to position the users
        searchFragmentRecyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    private fun showSearchView() {
        // Change the colors of the search icon and the close icon ; make searchView visible
        val searchIcon: ImageView? =
            searchView?.findViewById(androidx.appcompat.R.id.search_mag_icon)
        searchIcon?.colorFilter = BlendModeColorFilterCompat
            .createBlendModeColorFilterCompat(Color.BLACK, BlendModeCompat.SRC_ATOP)

        val closeIcon: ImageView? =
            searchView?.findViewById(androidx.appcompat.R.id.search_close_btn)
        closeIcon?.colorFilter = BlendModeColorFilterCompat
            .createBlendModeColorFilterCompat(Color.BLACK, BlendModeCompat.SRC_ATOP)

        searchView?.visibility = View.VISIBLE
    }

    //--------------------------------------------------------------------------------------
    // UI

    private fun getResultFromQuery() {
        // When the user valid a query in the searchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                getResultFromFirestore(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    //--------------------------------------------------------------------------------------

    private fun getResultFromFirestore(query: String) {
        if (resultList.isNotEmpty()) {
            resultList.clear()
        }

        // Get result for nickName field
        userId?.let {
            followViewModel.getListOfUsers(it)
                ?.whereEqualTo(USER_NICKNAME_FIELD, query)
                ?.get()
                ?.addOnSuccessListener { documents ->
                    Log.d(TAG, "For nickName : documents.size() => ${documents.size()}")
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            Log.d(TAG, "For nickName : ${document.id} => ${document.data}")
                            val followToAdd = document.toObject(User::class.java)
                            // Add the result to the nickNameList
                            resultList.add(followToAdd)
                        }
                    }
                    getResultForFirstName(query)

                }
                ?.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    private fun getResultForFirstName(query: String) {
        // Get result for firstName field
        userId?.let {
            followViewModel.getListOfUsers(it)
                ?.whereEqualTo(USER_FIRST_NAME_FIELD, query)
                ?.get()
                ?.addOnSuccessListener { documents ->
                    Log.d(TAG, "For firstName : documents.size() => ${documents.size()}")
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            Log.d(TAG, "For firstName : ${document.id} => ${document.data}")
                            val followToAdd = document.toObject(User::class.java)
                            // Add the result to the firstNameList
                            resultList.add(followToAdd)
                        }
                    }
                    getResultForName(query)
                }
                ?.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    private fun getResultForName(query: String) {
        // Get result for name field
        userId?.let {
            followViewModel.getListOfUsers(it)
                ?.whereEqualTo(USER_NAME_FIELD, query)
                ?.get()
                ?.addOnSuccessListener { documents ->
                    Log.d(TAG, "For name : documents.size() => ${documents.size()}")
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            Log.d(TAG, "For name : ${document.id} => ${document.data}")
                            val followToAdd = document.toObject(User::class.java)
                            // Add the result to the nameList
                            resultList.add(followToAdd)
                        }
                    }
                    showResult()
                }
                ?.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    private fun showResult() {
        if (resultList.isNotEmpty()) {
            // Convert resultList to set (avoid duplicates) then to list
            val listSorted = resultList.toSet().toList()
            Log.w(TAG, "distinct = $listSorted")
            Log.w(TAG, "distinct size = ${listSorted.size}")
            // Pass the list to the recyclerView
            configureRecyclerView(ArrayList(listSorted))
        } else {
            myUtils.showSnackBar(searchFragmentCoordinatorLayout, R.string.search_no_result)
        }
    }

    //--------------------------------------------------------------------------------------
    // Interface for callback ItemSearchAdapter

    override fun onClickProfileAfterSearch(user: User?, position: Int) {
        callback?.displayFollow(user)
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods
    // when click on an item in the list

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the method that creating callback after being attached to parent activity
        callbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface FollowListener {
        fun displayFollow(follow: User?)
    }

    // Create callback to parent activity
    private fun callbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callback = activity as FollowListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement FollowListener")
        }
    }
}