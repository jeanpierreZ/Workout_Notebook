package com.jpz.workoutnotebook.fragments.followingactivity

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
import com.algolia.search.client.ClientSearch
import com.algolia.search.client.Index
import com.algolia.search.helper.deserialize
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.Attribute
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.Query
import com.jpz.workoutnotebook.BuildConfig
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.FollowingActivity
import com.jpz.workoutnotebook.adapters.ItemSearchAdapter
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.repositories.UserAuth
import com.jpz.workoutnotebook.utils.MyUtils
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class SearchFragment : Fragment(), ItemSearchAdapter.Listener {

    companion object {
        private val TAG = SearchFragment::class.java.simpleName
        private const val USER_NICKNAME_FIELD = "nickName"
        private const val USER_FIRST_NAME_FIELD = "firstName"
        private const val USER_NAME_FIELD = "name"
        const val INDEX_NAME = "users"
    }

    private var userId: String? = null

    // Firebase Auth and utils
    private val userAuth: UserAuth by inject()
    private val myUtils: MyUtils by inject()

    private var searchView: SearchView? = null

    private val resultList = arrayListOf<User>()

    private var itemSearchFragment: ItemSearchAdapter? = null

    private var callback: FollowListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView = (context as FollowingActivity).findViewById(R.id.toolbarSearchView)

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
                GlobalScope.launch { getResultFromAlgolia(query) }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    //--------------------------------------------------------------------------------------

    private suspend fun getResultFromAlgolia(query: String) {
        // Set client with keys and index name
        val appID = ApplicationID(BuildConfig.algoliaAppId)
        val apiKey = APIKey(BuildConfig.algoliaApiKey)
        val indexName = IndexName(INDEX_NAME)
        val client = ClientSearch(appID, apiKey)
        // Init the index
        val index: Index = client.initIndex(indexName)
        // Set the attributes
        val nickName = Attribute(USER_NICKNAME_FIELD)
        val name = Attribute(USER_NAME_FIELD)
        val firstName = Attribute(USER_FIRST_NAME_FIELD)
        // Set the attributes in Algolia query
        Query(attributesToRetrieve = listOf(nickName, name, firstName), hitsPerPage = 50)

        if (resultList.isNotEmpty()) {
            resultList.clear()
        }

        val list = GlobalScope.async {
            // Search in Algolia with the query from the searchView
            val result = index.search(Query(query))
            Log.d(TAG, "result from Algolia = $result")
            // For each result add a user to the list
            result.hits.deserialize(User.serializer()).forEach { user ->
                val userToAdd: User = user
                resultList.add(userToAdd)
            }
        }

        // When the previous task is done, pass the list to the recyclerView
        list.await().run {
            activity?.runOnUiThread {
                if (resultList.isNotEmpty()) {
                    configureRecyclerView(resultList)
                } else {
                    myUtils.showSnackBar(searchFragmentCoordinatorLayout, R.string.search_no_result)
                }
            }
        }
    }

    //--------------------------------------------------------------------------------------
    // Interface for callback ItemSearchAdapter

    override fun onClickProfileAfterSearch(user: User?, position: Int, viewClicked: View?) {
        callback?.displayFollow(user, viewClicked)
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
        fun displayFollow(follow: User?, viewClicked: View?)
    }

    // Create callback to parent activity
    private fun callbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            if (activity is FollowingActivity) {
                callback = activity as FollowListener?
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement FollowListener")
        }
    }
}