package com.jpz.workoutnotebook.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity.Companion.TRAINING_SESSION
import com.jpz.workoutnotebook.adapters.ItemSeriesAdapter
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Series
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.models.Workout
import kotlinx.android.synthetic.main.fragment_training_session.*


class TrainingSessionFragment : Fragment() {

    companion object {
        private val TAG = TrainingSessionFragment::class.java.simpleName
    }

    private var trainingSession: TrainingSession? = null

    private var countDownTimer: CountDownTimer? = null
    private var timerRunning = false
    private var timeLeftInMillis = 0L
    private var countDownInterval = 1000L

    private var currentItemSeriesAdapter: ItemSeriesAdapter? = null
    private var nextItemSeriesAdapter: ItemSeriesAdapter? = null

    private var currentSeriesList: ArrayList<Series>? = ArrayList()
    private var nextSeriesList: ArrayList<Series>? = ArrayList()

    // To display the name of the next series
    private var seriesDisabledName: String? = null

    // Size of the list of exercises
    private var exercisesListSize: Int = 0

    // First exercise from the training session
    private var firstExercise = Exercise()

    // Exercise from the training session
    private var exercise: Exercise? = null

    // To know if there is a next series or exercise and the number of series and exercise
    private var hasNextSeries: Boolean = false
    private var noOfSeriesToComplete: Int = 0
    private var hasNextExercise: Boolean = false
    private var noOfExerciseToComplete: Int = 0

    // The workout is finished
    private var isFinished = false

    // To register the data entered by the user
    private val workoutToComplete = Workout()
    private var exerciseToComplete = Exercise()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trainingSession = arguments?.getParcelable(TRAINING_SESSION)

        // Display the workout name
        trainingSessionFragmentWorkoutName.text = trainingSession?.workout?.workoutName

        exercisesListSize = trainingSession?.workout?.exercisesList?.size!!
        firstExercise = trainingSession?.workout?.exercisesList!![0]

        // Display the first exercise name
        trainingSessionFragmentExerciseName.text = firstExercise.exerciseName

        // Go
        trainingSession?.let { trainingSession ->
            trainingSessionFragmentGo.setOnClickListener {
                // Display the first series, the next series or exercise and get rest time data
                displayTheFirstSeriesAndNext(firstExercise, exercisesListSize, trainingSession)

                // Enable the countDownTimer button for the rest time
                trainingSessionFragmentStartRestTime.setOnClickListener {
                    if (isFinished) {
                        // Save the completed data
                        saveWorkoutCompleted()
                        saveTrainingSession()
                    } else {
                        if (timerRunning) {
                            pauseTimer()
                        } else {
                            startTimer()
                        }
                    }
                }
            }
        }
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    // RecyclerView for the current series
    private fun configureCurrentRecyclerView() {
        // Create the adapter by passing the list of current series
        currentItemSeriesAdapter =
            activity?.let {
                currentSeriesList?.let { currentSeriesList ->
                    ItemSeriesAdapter(
                        currentSeriesList, isDisabled = false, isForTrainingSession = true,
                        seriesDisabledName = null, noOfSeries = noOfSeriesToComplete, context = it
                    )
                }
            }
        // Attach the adapter to the recyclerView to populate the series
        trainingSessionFragmentCurrentRecyclerView?.adapter = currentItemSeriesAdapter
        // Set layout manager to position the series
        trainingSessionFragmentCurrentRecyclerView?.layoutManager = LinearLayoutManager(activity)
        trainingSessionFragmentCurrentRecyclerView.layoutManager =
            object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean = false
            }
        trainingSessionFragmentCurrentRecyclerView.hasFixedSize()
    }

    // RecyclerView for the next series
    private fun configureNextRecyclerView() {
        // Create the adapter by passing the list of next series
        nextItemSeriesAdapter =
            activity?.let {
                nextSeriesList?.let { nextSeriesList ->
                    ItemSeriesAdapter(
                        nextSeriesList, isDisabled = true, isForTrainingSession = false,
                        seriesDisabledName = seriesDisabledName, noOfSeries = noOfSeriesToComplete,
                        context = it
                    )
                }
            }
        // Attach the adapter to the recyclerView to populate the series
        trainingSessionFragmentNextRecyclerView?.adapter = nextItemSeriesAdapter
        // Set layout manager to position the series
        trainingSessionFragmentNextRecyclerView?.layoutManager = LinearLayoutManager(activity)
        trainingSessionFragmentNextRecyclerView.layoutManager =
            object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean = false
            }
        trainingSessionFragmentNextRecyclerView.hasFixedSize()
    }

    //--------------------------------------------------------------------------------------
    // CountDownTimer

    private fun startTimer() {
        // If the rest time is equal to 0, pass 1L to allow the countDownTimer to start and finish
        if (timeLeftInMillis == 0L) {
            timeLeftInMillis = 1L
        }
        countDownTimer = object : CountDownTimer(timeLeftInMillis, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                // Save the completed data
                saveWorkoutCompleted()
                timerRunning = false
                // Set text button to inform user that he can start again the countDownTimer
                trainingSessionFragmentStartRestTime.text = getString(R.string.start_rest_time)
                // Display the next series or exercise
                trainingSession?.let { displayNextSeriesOrExercises(exercisesListSize, it) }
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
        if (isFinished) {
            return
        } else {
            val secondsLeft = (timeLeftInMillis / countDownInterval).toInt()
            trainingSessionFragmentRestTime.text = secondsLeft.toString()
        }
    }

    private fun restTime(restTime: String) {
        // Display the rest time
        trainingSessionFragmentRestTime.text = restTime
        // Set the countDownTimer with restTime
        timeLeftInMillis = restTime.toLong().times(countDownInterval)
    }

    //--------------------------------------------------------------------------------------
    // Display the training session data

    private fun displayTheFirstSeriesAndNext(
        firstExercise: Exercise, exercisesSize: Int, trainingSession: TrainingSession
    ) {
        val seriesListSize: Int = firstExercise.seriesList.size

        // Add the first series of the first exercise
        currentSeriesList?.add(firstExercise.seriesList[0])
        // Display the first series
        configureCurrentRecyclerView()

        when {
            // Check if there is a second series
            seriesListSize > 1 -> {
                // Add the second series of exercise
                nextSeriesList?.add(firstExercise.seriesList[1])
                seriesDisabledName = firstExercise.seriesList[1].seriesName

                // Display the second series
                configureNextRecyclerView()

                // Get rest time from restNextSet
                restTime(firstExercise.restNextSet.toString())

                // For next time
                hasNextSeries = true
                hasNextExercise = false
            }
            // Check if there is a second exercise
            exercisesSize > 1 -> {
                // Display the second exercise name
                trainingSessionFragmentNextExerciseName.text =
                    trainingSession.workout?.exercisesList!![1].exerciseName
                trainingSessionFragmentNextExerciseName.visibility = View.VISIBLE

                // Add the first series of the second exercise
                nextSeriesList?.add(trainingSession.workout?.exercisesList!![1].seriesList[0])
                seriesDisabledName =
                    trainingSession.workout?.exercisesList!![1].seriesList[0].seriesName
                // Display this series
                configureNextRecyclerView()

                // Get rest time from restNextExercise
                restTime(firstExercise.restNextExercise.toString())

                // For next time
                hasNextSeries = false
                hasNextExercise = true
            }
            else -> {
                sessionTrainingEnds()
            }
        }
        trainingSessionFragmentGo.isEnabled = false
    }

    private fun displayNextSeriesOrExercises(exercisesSize: Int, trainingSession: TrainingSession) {
        Log.d(TAG, "hasNextSeries = $hasNextSeries")
        Log.d(TAG, "hasNextExercise = $hasNextExercise")

        // Clear the series lists because we display only one item each time
        currentSeriesList?.clear()
        nextSeriesList?.clear()

        // --- Display the current series list ---

        when {
            hasNextSeries -> {
                // Add the next series to currentList and display it

                // Increment the number of series to complete
                noOfSeriesToComplete++

                // Display the next series
                exercise = trainingSession.workout?.exercisesList?.get(noOfExerciseToComplete)
                exercise?.seriesList?.get(noOfSeriesToComplete)?.let { currentSeriesList?.add(it) }
                configureCurrentRecyclerView()
            }
            hasNextExercise -> {
                // Add the next exercise with the first series to currentList and display it

                // Increment the number of exercise to complete
                noOfExerciseToComplete++
                // Reset its number of series
                noOfSeriesToComplete = 0

                // Display the first series of this exercise
                exercise = trainingSession.workout?.exercisesList?.get(noOfExerciseToComplete)
                exercise?.seriesList?.get(noOfSeriesToComplete)?.let { currentSeriesList?.add(it) }

                // Display this exercise name
                trainingSessionFragmentExerciseName.text = exercise?.exerciseName

                configureCurrentRecyclerView()
            }
        }

        // --- Display the next series list or the next exercise ---

        val seriesListSize: Int? = exercise?.seriesList?.size
        // Get the number of series to complete next time
        val noOfSeriesToCompleteNextTime = noOfSeriesToComplete.plus(1)
        // Get the number of exercise to complete next time
        val noOfExerciseToCompleteNextTime = noOfExerciseToComplete.plus(1)

        seriesListSize?.let {
            when {
                // Check if there is a next series
                seriesListSize > noOfSeriesToCompleteNextTime -> {

                    // Add the next series of this exercise
                    exercise?.seriesList?.get(noOfSeriesToCompleteNextTime)?.let { seriesList ->
                        nextSeriesList?.add(seriesList)
                    }
                    seriesDisabledName =
                        exercise?.seriesList?.get(noOfSeriesToCompleteNextTime)?.seriesName

                    // Display the next series
                    if (trainingSessionFragmentNextExerciseName.visibility == View.VISIBLE) {
                        trainingSessionFragmentNextExerciseName.visibility = View.INVISIBLE
                    }
                    configureNextRecyclerView()
                    // Get rest time from restNextSet
                    restTime(exercise?.restNextSet.toString())

                    // For next time
                    hasNextSeries = true
                    hasNextExercise = false
                }
                // Check if there is a next exercise
                exercisesSize > noOfExerciseToCompleteNextTime -> {

                    // Display the next exercise name
                    trainingSessionFragmentNextExerciseName.text =
                        trainingSession.workout?.exercisesList!![noOfExerciseToCompleteNextTime].exerciseName

                    if (trainingSessionFragmentNextExerciseName.visibility == View.INVISIBLE) {
                        trainingSessionFragmentNextExerciseName.visibility = View.VISIBLE
                    }

                    // Add the first series of this exercise
                    nextSeriesList?.add(trainingSession.workout?.exercisesList!![noOfExerciseToCompleteNextTime].seriesList[0])
                    seriesDisabledName =
                        trainingSession.workout?.exercisesList!![noOfExerciseToCompleteNextTime].seriesList[0].seriesName
                    // Display this series
                    configureNextRecyclerView()

                    // Get rest time from restNextExercise
                    restTime(exercise?.restNextExercise.toString())

                    // For next time
                    hasNextSeries = false
                    hasNextExercise = true
                }
                else -> {
                    sessionTrainingEnds()
                }
            }
        }
    }

    //--------------------------------------------------------------------------------------
    // End and save the training session

    private fun sessionTrainingEnds() {
        // There is no more series and exercise
        if (trainingSessionFragmentNextExerciseName.visibility == View.VISIBLE) {
            trainingSessionFragmentNextExerciseName.visibility = View.INVISIBLE
        }
        isFinished = true
        // Set text button to inform user that he can save the training session
        activity?.let { activity ->
            trainingSessionFragmentStartRestTime.setBackgroundColor(
                ContextCompat.getColor(activity, R.color.colorAccent)
            )
            trainingSessionFragmentStartRestTime.setTextColor(
                ContextCompat.getColor(activity, R.color.colorTextPrimary)
            )
            trainingSessionFragmentStartRestTime.iconTint =
                ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorTextPrimary))
        }
        trainingSessionFragmentStartRestTime.text = getString(R.string.save_training_session)
        trainingSessionFragmentRestTime.visibility = View.INVISIBLE
    }

    private fun saveWorkoutCompleted() {
        // Save the current series in the corresponding exercise
        when {
            exercise == null -> {
                // Get the first exercise data
                exerciseToComplete = Exercise(
                    firstExercise.exerciseId, firstExercise.exerciseName,
                    firstExercise.restNextSet, firstExercise.restNextExercise, true
                )
                // Add the first series to complete with the current series
                currentSeriesList?.get(0)?.let {
                    exerciseToComplete.seriesList.add(it)
                }
            }

            exerciseToComplete.exerciseId == exercise?.exerciseId -> {
                // Add the next series to complete with the current series
                currentSeriesList?.get(0)?.let {
                    exerciseToComplete.seriesList.add(it)
                }
            }

            else -> {
                // Add the previous exercise to the list
                exerciseToComplete.let { workoutToComplete.exercisesList.add(it) }
                // Get the current exercise data
                exerciseToComplete = exercise?.restNextSet?.let { restNextSet ->
                    exercise?.restNextExercise?.let { restNextExercise ->
                        Exercise(
                            exercise?.exerciseId, exercise?.exerciseName,
                            restNextSet, restNextExercise, true
                        )
                    }
                }!!
                // Add the first series to complete with the current series
                currentSeriesList?.get(0)?.let {
                    exerciseToComplete.seriesList.add(it)
                }
            }
        }
    }

    private fun saveTrainingSession() {
        Toast.makeText(activity, "SAVE TRAINING SESSION", Toast.LENGTH_SHORT).show()

        workoutToComplete.workoutId = trainingSession?.workout?.workoutId
        workoutToComplete.workoutName = trainingSession?.workout?.workoutName

        // Add the previous exercise in the list
        exerciseToComplete.let { workoutToComplete.exercisesList.add(it) }

        val trainingSessionToSave = TrainingSession(
            trainingSession?.trainingSessionId, trainingSession?.trainingSessionDate,
            true, workoutToComplete
        )

        Log.d(TAG, "TrainingSessionToSave = $trainingSessionToSave")
    }
}