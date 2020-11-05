package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity.Companion.TRAINING_SESSION
import com.jpz.workoutnotebook.adapters.ItemSeriesAdapter
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Series
import com.jpz.workoutnotebook.models.TrainingSession
import kotlinx.android.synthetic.main.fragment_training_session.*


class TrainingSessionFragment : Fragment() {

    companion object {
        private val TAG = TrainingSessionFragment::class.java.simpleName
    }

    private var countDownTimer: CountDownTimer? = null
    private var timerRunning = false
    private var timeLeftInMillis: Long = 0L

    private var currentItemSeriesAdapter: ItemSeriesAdapter? = null
    private var nextItemSeriesAdapter: ItemSeriesAdapter? = null

    private var currentSeriesList: ArrayList<Series>? = ArrayList()
    private var nextSeriesList: ArrayList<Series>? = ArrayList()

    private var seriesDisabledName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val trainingSession = arguments?.getParcelable<TrainingSession>(TRAINING_SESSION)

        // Display the workout name
        trainingSessionFragmentWorkoutName.text = trainingSession?.workout?.workoutName

        val exercisesSize: Int = trainingSession?.workout?.exercisesList?.size!!
        val firstExercise: Exercise = trainingSession.workout?.exercisesList!![0]

        // Display the first exercise name
        trainingSessionFragmentExerciseName.text = firstExercise.exerciseName

        // Go
        trainingSessionFragmentGo.setOnClickListener {
            val seriesSize: Int = firstExercise.seriesList.size

            // Add the first series of the first exercise
            currentSeriesList?.add(firstExercise.seriesList[0])
            // Display the first series
            configureCurrentRecyclerView()

            // Check if there is a second series
            if (seriesSize > 1) {
                // Add the second series of exercise
                nextSeriesList?.add(firstExercise.seriesList[1])
                seriesDisabledName = firstExercise.seriesList[1].seriesName
                // Display the second series
                configureNextRecyclerView()
                // Get rest time from restNextSet
                restTime(firstExercise.restNextSet.toString())

                // Check if there is a second exercise
            } else if (exercisesSize > 1) {
                // Display the second exercise name
                trainingSessionFragmentNextExerciseName.text =
                    trainingSession.workout?.exercisesList!![1].exerciseName
                trainingSessionFragmentNextExerciseName.visibility = View.VISIBLE
                // Get rest time from restNextExercise
                restTime(firstExercise.restNextExercise.toString())
            }

            trainingSessionFragmentGo.isEnabled = false

            trainingSessionFragmentStartRestTime.setOnClickListener {
                if (timerRunning) {
                    pauseTimer()
                } else {
                    startTimer()
                }
            }
        }
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    private fun configureCurrentRecyclerView() {
        // Create the adapter by passing the list of series of the user
        currentItemSeriesAdapter =
            activity?.let {
                currentSeriesList?.let { currentSeriesList ->
                    ItemSeriesAdapter(currentSeriesList, false, null, it)
                }
            }
        // Attach the adapter to the recyclerView to populate the series
        trainingSessionFragmentCurrentRecyclerView?.adapter = currentItemSeriesAdapter
        // Set layout manager to position the series
        trainingSessionFragmentCurrentRecyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    private fun configureNextRecyclerView() {
        // Create the adapter by passing the list of series of the user
        nextItemSeriesAdapter =
            activity?.let {
                nextSeriesList?.let { nextSeriesList ->
                    ItemSeriesAdapter(nextSeriesList, true, seriesDisabledName, it)
                }
            }
        // Attach the adapter to the recyclerView to populate the series
        trainingSessionFragmentNextRecyclerView?.adapter = nextItemSeriesAdapter
        // Set layout manager to position the series
        trainingSessionFragmentNextRecyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    //--------------------------------------------------------------------------------------
    // CountDownTimer

    private fun startTimer() {
        Log.d(TAG, "timeLeftInMillis = $timeLeftInMillis")
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                timerRunning = false
                trainingSessionFragmentStartRestTime.text = getString(R.string.start_rest_time)
            }
        }.start()
        timerRunning = true
        trainingSessionFragmentStartRestTime.text = getString(R.string.pause)
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        timerRunning = false
        trainingSessionFragmentStartRestTime.text = getString(R.string.start_rest_time)
    }

    private fun updateCountDownText() {
        val seconds = (timeLeftInMillis / 1000).toInt()
        trainingSessionFragmentRestTime.text = seconds.toString()
    }

    private fun restTime(restTime: String) {
        // Display the rest time
        trainingSessionFragmentRestTime.text = restTime
        // Set the countDownTimer with restTime
        timeLeftInMillis = restTime.toLong().times(1000)
    }
}