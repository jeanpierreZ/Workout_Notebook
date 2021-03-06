package com.jpz.workoutnotebook.fragments.editactivity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.EditActivity
import com.jpz.workoutnotebook.activities.FollowingActivity
import com.jpz.workoutnotebook.activities.MainActivity
import com.jpz.workoutnotebook.adapters.ItemHistoricalAdapter
import com.jpz.workoutnotebook.databinding.FragmentHistoricalBinding
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.TrainingSessionViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class HistoricalFragment : Fragment() {

    companion object {
        private val TAG = HistoricalFragment::class.java.simpleName
        private const val TRAINING_SESSION_COMPLETED_FIELD = "trainingSessionCompleted"
        private const val TRAINING_SESSION_DATE_FIELD = "trainingSessionDate"
    }

    private var _binding: FragmentHistoricalBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var trainingSession: TrainingSession? = null

    private var itemHistoricalAdapter: ItemHistoricalAdapter? = null

    // Firebase Firestore and utils
    private val trainingSessionViewModel: TrainingSessionViewModel by viewModel()
    private val myUtils: MyUtils by inject()

    private val list = arrayListOf<TrainingSession>()

    private val numberOfTrainingSessions: Long = 5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoricalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isFollowingHistorical = arguments?.getBoolean(EditActivity.IS_FOLLOWING_HISTORICAL)

        if (isFollowingHistorical == true) {
            val following = arguments?.getParcelable<User>(FollowingActivity.FOLLOWING)
            Log.d(TAG, "following = $following")
            following?.let { getFollowHistorical(it) }
        } else {
            trainingSession = arguments?.getParcelable(MainActivity.TRAINING_SESSION)
            Log.d(TAG, "trainingSession = $trainingSession")
            trainingSession?.let { list.add(it) }
            configureRecyclerView(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    private fun configureRecyclerView(list: ArrayList<TrainingSession>) {
        // Create the adapter by passing the list of training sessions
        itemHistoricalAdapter = activity?.let { ItemHistoricalAdapter(list, it) }
        // Attach the adapter to the recyclerView to populate the training sessions
        binding.historicalFragmentRecyclerView.adapter = itemHistoricalAdapter
        // Set layout manager to position the training sessions
        binding.historicalFragmentRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun getFollowHistorical(follow: User) {
        val today: Date = Calendar.getInstance().time
        // SimpleDateFormat is used to format today date
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        val todaySDFString = sdf.format(today)

        trainingSessionViewModel.getListOfTrainingSessionsOfFollow(follow.userId)
            .whereEqualTo(TRAINING_SESSION_COMPLETED_FIELD, true)
            .whereLessThanOrEqualTo(TRAINING_SESSION_DATE_FIELD, todaySDFString)
            .orderBy(TRAINING_SESSION_DATE_FIELD, Query.Direction.DESCENDING)
            .limit(numberOfTrainingSessions)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "documents.isEmpty")
                    // There is no document, inform user
                    myUtils.showSnackBar(
                        binding.historicalFragmentCoordinatorLayout,
                        R.string.no_historical_training_sessions
                    )
                } else {
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        // For each training session, get the TrainingSession object
                        val trainingSession = document.toObject(TrainingSession::class.java)
                        list.add(trainingSession)
                    }
                    // Show the training sessions
                    configureRecyclerView(list)
                }
            }
    }
}