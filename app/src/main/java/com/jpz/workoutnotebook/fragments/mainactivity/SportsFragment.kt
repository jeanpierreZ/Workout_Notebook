package com.jpz.workoutnotebook.fragments.mainactivity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.BuildConfig
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity
import com.jpz.workoutnotebook.databinding.FragmentSportsBinding
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.utils.EspressoIdlingResource
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.TrainingSessionViewModel
import kotlinx.serialization.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class SportsFragment : Fragment() {

    companion object {
        private val TAG = SportsFragment::class.java.simpleName
        private const val TRAINING_SESSION_DATE_FIELD = "trainingSessionDate"
        private const val TRAINING_SESSION_COMPLETED_FIELD = "trainingSessionCompleted"
        private const val START_DELAY = 500L
        const val ALPHA_VIEW_ANIMATION_DURATION = 500L
    }

    private var _binding: FragmentSportsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var callback: SportsFragmentButtonListener? = null

    private var trainingSession: TrainingSession? = null

    // SimpleDateFormat is used to compare the dates in the trainingSessionList
    private val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    // Firebase Firestore and utils
    private val trainingSessionViewModel: TrainingSessionViewModel by viewModel()
    private val myUtils: MyUtils by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayNextTrainingSessionDate()

        binding.sportsFragmentTrainingSessionButton.setOnClickListener {
            trainingSession?.let { callback?.onClickedTrainingSessionButton(it) }
        }

        binding.sportsFragmentExercisesButton.setOnClickListener {
            callback?.onClickedExerciseOrWorkoutButton(getString(R.string.exercises))
        }

        binding.sportsFragmentWorkoutsButton.setOnClickListener {
            callback?.onClickedExerciseOrWorkoutButton(getString(R.string.workouts))
        }

        if (!myUtils.isOnline(requireActivity())) isOnline()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //----------------------------------------------------------------------------------

    private fun isOnline() {
        // Create an AlertDialog to alert the user that he is not connected to the network
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(R.string.no_network)
        builder.apply { setPositiveButton(android.R.string.ok) { _, _ -> } }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //----------------------------------------------------------------------------------

    private fun displayNextTrainingSessionDate() {
        // Idle Resource for test
        if (BuildConfig.DEBUG) EspressoIdlingResource.incrementIdlingResource()
        // Instantiate a Calendar
        val nowCalendar = Calendar.getInstance()
        val now: Date = nowCalendar.time
        // Parse the date in SimpleDateFormat to compare it with the list of training sessions
        val formattedDate = sdf.format(now)

        trainingSessionViewModel.getListOfTrainingSessions()
            // Filter the list with upcoming parsed dates and training sessions that are not still completed
            ?.whereEqualTo(TRAINING_SESSION_COMPLETED_FIELD, false)
            ?.whereGreaterThanOrEqualTo(TRAINING_SESSION_DATE_FIELD, formattedDate)
            ?.orderBy(TRAINING_SESSION_DATE_FIELD, Query.Direction.ASCENDING)
            ?.limit(1)
            ?.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.documents
                    // Get the training session object
                    list[0].reference.get().addOnCompleteListener { document ->
                        trainingSession = document.result?.toObject(TrainingSession::class.java)
                        // Get next trainingSessionDate and the workout name
                        trainingSession?.trainingSessionDate?.let { trainingSessionDate ->
                            // Format date and display it with the workout name
                            val nextDate: Date = sdf.parse(trainingSessionDate)!!
                            val dateStringFormatted: String =
                                DateFormat
                                    .getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                                    .format(nextDate)
                            val workoutName: String? = trainingSession?.workout?.workoutName
                            alphaViewAnimation(binding.sportsFragmentTrainingSession)
                            binding.sportsFragmentTrainingSession.visibility = View.VISIBLE
                            binding.sportsFragmentTrainingSession.text = getString(
                                R.string.next_training_session_data,
                                workoutName, dateStringFormatted
                            )
                            binding.sportsFragmentTrainingSessionButton.isEnabled = true
                            myUtils.scaleViewAnimation(
                                binding.sportsFragmentTrainingSessionButton, START_DELAY
                            )
                            binding.sportsFragmentTrainingSessionButton.visibility = View.VISIBLE
                        }
                        // Idle Resource for test
                        if (BuildConfig.DEBUG) EspressoIdlingResource.decrementIdlingResource()
                    }
                    Log.d(TAG, "Current data: ${snapshot.documents}")
                } else {
                    alphaViewAnimation(binding.sportsFragmentTrainingSession)
                    binding.sportsFragmentTrainingSession.visibility = View.VISIBLE
                    binding.sportsFragmentTrainingSession.text =
                        getString(R.string.no_upcoming_training_session)
                    Log.d(TAG, "Current data: null")
                    binding.sportsFragmentTrainingSessionButton.isEnabled = false
                    binding.sportsFragmentTrainingSessionButton.visibility = View.INVISIBLE
                    // Idle Resource for test
                    if (BuildConfig.DEBUG) EspressoIdlingResource.decrementIdlingResource()
                }
            }
    }

    //----------------------------------------------------------------------------------
    // Private animation for the text of the next training session

    private fun alphaViewAnimation(view: View) {
        val animation: Animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = ALPHA_VIEW_ANIMATION_DURATION
        animation.startOffset = START_DELAY
        view.startAnimation(animation)
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods when click on a button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the methods that creating callback after being attached to parent activity
        callbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface SportsFragmentButtonListener {
        fun onClickedExerciseOrWorkoutButton(button: String?)
        fun onClickedTrainingSessionButton(trainingSession: TrainingSession)
    }

    // Create callback to parent activity
    private fun callbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            if (activity is MainActivity) {
                callback = activity as SportsFragmentButtonListener?
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement SportsFragmentButtonListener")
        }
    }
}