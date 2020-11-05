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

        val trainingSession: TrainingSession? = arguments?.getParcelable(TRAINING_SESSION)
        trainingSessionFragmentWorkoutName.text = trainingSession?.workout?.workoutName

        if (trainingSession?.workout?.exercisesList != null && trainingSession.workout?.exercisesList!!.isNotEmpty()) {
            // Display the exercise name
            trainingSessionFragmentExerciseName.text =
                trainingSession.workout?.exercisesList!![0].exerciseName

            // Go
            trainingSessionFragmentGo.setOnClickListener {
                if (trainingSession.workout?.exercisesList?.get(0)?.seriesList != null) {
                    // Add the first series of exercise
                    currentSeriesList?.add(trainingSession.workout?.exercisesList?.get(0)?.seriesList!![0])
                    // Display the first series
                    configureCurrentRecyclerView()

                    // Add the second series of exercise
                    nextSeriesList?.add(trainingSession.workout?.exercisesList?.get(0)?.seriesList!![1])
                    seriesDisabledName =
                        trainingSession.workout?.exercisesList?.get(0)?.seriesList!![1].seriesName
                    // Display the add series
                    configureNextRecyclerView()
                }
                trainingSessionFragmentGo.isEnabled = false
            }

            val restTime = trainingSession.workout?.exercisesList!![0].restNextSet.toString()
            // Display rest time
            trainingSessionFragmentRestTime.text = restTime
            // Set the countDownTimer with restTime
            trainingSession.workout?.exercisesList!![0].restNextSet.toLong().times(1000).let {
                timeLeftInMillis = it
            }

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
}