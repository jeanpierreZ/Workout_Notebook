package com.jpz.workoutnotebook.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.repositories.UserAuth
import com.jpz.workoutnotebook.viewmodels.TrainingSessionViewModel
import kotlinx.android.synthetic.main.fragment_sports.*
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
    }

    private var userId: String? = null

    private var callback: SportsFragmentButtonListener? = null

    private var trainingSession: TrainingSession? = null

    // SimpleDateFormat is used to compare the dates in the trainingSessionList
    private val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    // Firebase Auth, Firestore and utils
    private val userAuth: UserAuth by inject()
    private val trainingSessionViewModel: TrainingSessionViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid

        displayNextTrainingSessionDate()

        sportsFragmentTrainingSessionButton.visibility = View.INVISIBLE
        sportsFragmentTrainingSessionButton.setOnClickListener {
            trainingSession?.let { callback?.onClickedTrainingSessionButton(it) }
        }

        sportsFragmentExercisesButton.setOnClickListener {
            callback?.onClickedExerciseOrWorkoutButton(getString(R.string.exercises))
        }

        sportsFragmentWorkoutsButton.setOnClickListener {
            callback?.onClickedExerciseOrWorkoutButton(getString(R.string.workouts))
        }
    }

    //----------------------------------------------------------------------------------

    private fun displayNextTrainingSessionDate() {
        // Instantiate a Calendar
        val nowCalendar = Calendar.getInstance()
        val now: Date = nowCalendar.time
        // Parse the date in SimpleDateFormat to compare it with the list of training sessions
        val formattedDate = sdf.format(now)

        userId?.let {
            trainingSessionViewModel.getListOfTrainingSessions(it)
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
                                val dateStringFormatted: String = DateFormat.getDateTimeInstance(
                                    DateFormat.MEDIUM, DateFormat.SHORT
                                ).format(nextDate)
                                val workoutName: String? = trainingSession?.workout?.workoutName
                                sportsFragmentTrainingSession.text = getString(
                                    R.string.next_training_session_data,
                                    workoutName, dateStringFormatted
                                )
                                sportsFragmentTrainingSessionButton.isEnabled = true
                                sportsFragmentTrainingSessionButton.visibility = View.VISIBLE
                            }
                        }
                        Log.d(TAG, "Current data: ${snapshot.documents}")
                    } else {
                        sportsFragmentTrainingSession.text =
                            getString(R.string.no_upcoming_training_session)
                        Log.d(TAG, "Current data: null")
                        sportsFragmentTrainingSessionButton.isEnabled = false
                        sportsFragmentTrainingSessionButton.visibility = View.INVISIBLE
                    }
                }
        }
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
            callback = activity as SportsFragmentButtonListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement SportsFragmentButtonListener")
        }
    }
}