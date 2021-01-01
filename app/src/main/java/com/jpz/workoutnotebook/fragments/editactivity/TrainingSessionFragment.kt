package com.jpz.workoutnotebook.fragments.editactivity

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity.Companion.TRAINING_SESSION
import com.jpz.workoutnotebook.adapters.ItemSeriesAdapter
import com.jpz.workoutnotebook.databinding.FragmentTrainingSessionBinding
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Series
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.models.Workout
import com.jpz.workoutnotebook.repositories.UserAuth
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.TrainingSessionViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class TrainingSessionFragment : Fragment() {

    companion object {
        private val TAG = TrainingSessionFragment::class.java.simpleName
        private const val COUNTDOWN_INTERVAL = 1000L
    }

    private var _binding: FragmentTrainingSessionBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var trainingSession: TrainingSession? = null

    private var countDownTimer: CountDownTimer? = null
    private var timerRunning = false
    private var timeLeftInMillis = 0L

    private var currentItemSeriesAdapter: ItemSeriesAdapter? = null
    private var nextItemSeriesAdapter: ItemSeriesAdapter? = null

    private var currentSeriesList: ArrayList<Series> = ArrayList()
    private var nextSeriesList: ArrayList<Series> = ArrayList()

    // To display the name of the next series
    private var seriesDisabledName: String? = null

    // Size of the list of exercises
    private var exercisesListSize: Int = 0

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

    private var userId: String? = null

    // Firebase Auth, Firestore and utils
    private val userAuth: UserAuth by inject()
    private val trainingSessionViewModel: TrainingSessionViewModel by viewModel()
    private val myUtils: MyUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Need to keep the screen turned on
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            saveBeforeQuit()
        }

        val toolbar = activity?.findViewById(R.id.includedLayout) as Toolbar
        toolbar.setNavigationOnClickListener {
            // Handle the toolbar's up button event
            saveBeforeQuit()
        }

        userId = userAuth.getCurrentUser()?.uid

        trainingSession = arguments?.getParcelable(TRAINING_SESSION)

        // Display the workout name
        binding.trainingSessionFragmentWorkoutName.text = trainingSession?.workout?.workoutName

        exercisesListSize = trainingSession?.workout?.exercisesList?.size!!

        // Display the first exercise name
        binding.trainingSessionFragmentExerciseName.text =
            trainingSession?.workout?.exercisesList!![0].exerciseName

        // Disabled the countDownTimer button to start rest time
        binding.trainingSessionFragmentStartRestTime.isEnabled = false

        // Go
        trainingSession?.let { trainingSession ->
            binding.trainingSessionFragmentGo.setOnClickListener {
                // Display the series, the next series or exercise and get rest time data
                displaySeries(exercisesListSize, trainingSession)
                // Enable the countDownTimer button for the rest time
                binding.trainingSessionFragmentStartRestTime.isEnabled = true
            }
        }

        // Start rest time or save the training session
        binding.trainingSessionFragmentStartRestTime.setOnClickListener {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    // RecyclerView for the current series
    private fun configureCurrentRecyclerView() {
        // Create the adapter by passing the list of current series
        currentItemSeriesAdapter = activity?.let {
            ItemSeriesAdapter(
                currentSeriesList, isDisabled = false, isForTrainingSession = true,
                seriesDisabledName = null, noOfSeries = noOfSeriesToComplete, context = it
            )
        }
        // Attach the adapter to the recyclerView to populate the series
        binding.trainingSessionFragmentCurrentRecyclerView.adapter = currentItemSeriesAdapter
        // Set layout manager to position the series
        binding.trainingSessionFragmentCurrentRecyclerView.layoutManager =
            LinearLayoutManager(activity)
        binding.trainingSessionFragmentCurrentRecyclerView.layoutManager =
            object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean = false
            }
        binding.trainingSessionFragmentCurrentRecyclerView.hasFixedSize()
    }

    // RecyclerView for the next series
    private fun configureNextRecyclerView() {
        // Create the adapter by passing the list of next series
        nextItemSeriesAdapter = activity?.let {
            ItemSeriesAdapter(
                nextSeriesList, isDisabled = true, isForTrainingSession = false,
                seriesDisabledName = seriesDisabledName, noOfSeries = noOfSeriesToComplete,
                context = it
            )
        }
        // Attach the adapter to the recyclerView to populate the series
        binding.trainingSessionFragmentNextRecyclerView.adapter = nextItemSeriesAdapter
        // Set layout manager to position the series
        binding.trainingSessionFragmentNextRecyclerView.layoutManager =
            LinearLayoutManager(activity)
        binding.trainingSessionFragmentNextRecyclerView.layoutManager =
            object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean = false
            }
        binding.trainingSessionFragmentNextRecyclerView.hasFixedSize()
    }

    //--------------------------------------------------------------------------------------
    // CountDownTimer

    private fun startTimer() {
        // If the rest time is equal to 0, pass 1L to allow the countDownTimer to start and finish
        if (timeLeftInMillis == 0L) {
            timeLeftInMillis = 1L
        }
        countDownTimer = object : CountDownTimer(timeLeftInMillis, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                // Save the completed data
                saveWorkoutCompleted()
                timerRunning = false
                // Set text button to inform user that he can start again the countDownTimer
                binding.trainingSessionFragmentStartRestTime.text =
                    getString(R.string.start_rest_time)
                // Display the next series or exercise
                trainingSession?.let { displaySeries(exercisesListSize, it) }
            }
        }.start()
        timerRunning = true
        binding.trainingSessionFragmentStartRestTime.text = getString(R.string.pause)
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        timerRunning = false
        binding.trainingSessionFragmentStartRestTime.text = getString(R.string.start_rest_time)
    }

    private fun updateCountDownText() {
        if (isFinished) {
            return
        } else {
            val secondsLeft = (timeLeftInMillis / COUNTDOWN_INTERVAL).toInt()
            binding.trainingSessionFragmentRestTime.text = secondsLeft.toString()
        }
    }

    private fun restTime(restTime: String) {
        // Display the rest time
        binding.trainingSessionFragmentRestTime.text = restTime
        // Set the countDownTimer with restTime
        timeLeftInMillis = restTime.toLong().times(COUNTDOWN_INTERVAL)
    }

    //--------------------------------------------------------------------------------------
    // Display the training session data

    private fun displaySeries(exercisesSize: Int, trainingSession: TrainingSession) {
        // Clear the series lists because we display only one item each time
        if (currentSeriesList.isNotEmpty()) {
            currentSeriesList.clear()
        }
        if (nextSeriesList.isNotEmpty()) {
            nextSeriesList.clear()
        }

        // --- Display the current series list ---

        when {
            hasNextSeries -> {
                // Add the next series to currentList and display it

                // Increment the number of series to complete
                noOfSeriesToComplete++

                // Display the next series
                getNextSeries()
                configureCurrentRecyclerView()
            }

            hasNextExercise -> {
                // Add the next exercise with the first series to currentList and display it

                // Increment the number of exercise to complete
                noOfExerciseToComplete++
                // Reset its number of series
                noOfSeriesToComplete = 0

                // Display the first series of this exercise
                getNextSeries()

                // Display this exercise name and series
                binding.trainingSessionFragmentExerciseName.text = exercise?.exerciseName
                configureCurrentRecyclerView()
            }

            else -> {
                // Display the first series of the first exercise
                getNextSeries()

                // Display the first series
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
                        nextSeriesList.add(seriesList)
                    }
                    seriesDisabledName =
                        exercise?.seriesList?.get(noOfSeriesToCompleteNextTime)?.seriesName

                    // Display the next series
                    if (binding.trainingSessionFragmentNextExerciseName.visibility == View.VISIBLE) {
                        binding.trainingSessionFragmentNextExerciseName.visibility = View.INVISIBLE
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
                    binding.trainingSessionFragmentNextExerciseName.text =
                        trainingSession.workout?.exercisesList!![noOfExerciseToCompleteNextTime].exerciseName

                    if (binding.trainingSessionFragmentNextExerciseName.visibility == View.INVISIBLE) {
                        binding.trainingSessionFragmentNextExerciseName.visibility = View.VISIBLE
                    }
                    // Add the first series of this exercise
                    nextSeriesList.add(trainingSession.workout?.exercisesList!![noOfExerciseToCompleteNextTime].seriesList[0])
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
        binding.trainingSessionFragmentGo.isEnabled = false
    }

    private fun getNextSeries() {
        exercise = trainingSession?.workout?.exercisesList?.get(noOfExerciseToComplete)
        exercise?.seriesList?.get(noOfSeriesToComplete)?.let { currentSeriesList.add(it) }
    }

    //--------------------------------------------------------------------------------------
    // End and save the training session

    private fun closeFragment() {
        activity?.let {
        binding.trainingSessionFragmentProgressBar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({ activity?.finish() }, 2000)
        binding.trainingSessionFragmentStartRestTime.isEnabled = false
        }
    }

    private fun saveBeforeQuit() {
        // Create an alert dialog to save the training session before exiting
        if (!binding.trainingSessionFragmentGo.isEnabled) {
            activity?.let {
                AlertDialog.Builder(it)
                    .setMessage(getString(R.string.save_training_session))
                    .setPositiveButton(getString(R.string.quit_and_save)) { _, _ ->
                        // Save the training session
                        saveWorkoutCompleted()
                        saveTrainingSession()
                        closeFragment()
                    }
                    .setNegativeButton(getString(R.string.quit_without_saving)) { _, _ -> activity?.finish() }
                    .show()
            }
        } else {
            activity?.finish()
        }
    }

    private fun sessionTrainingEnds() {
        // There is no more series and exercise
        if (binding.trainingSessionFragmentNextExerciseName.visibility == View.VISIBLE) {
            binding.trainingSessionFragmentNextExerciseName.visibility = View.INVISIBLE
        }
        isFinished = true
        // Set text button to inform user that he can save the training session
        activity?.let { activity ->
            binding.trainingSessionFragmentStartRestTime.setBackgroundColor(
                ContextCompat.getColor(activity, R.color.colorAccent)
            )
            binding.trainingSessionFragmentStartRestTime.setTextColor(
                ContextCompat.getColor(activity, R.color.colorTextPrimary)
            )
            binding.trainingSessionFragmentStartRestTime.icon =
                ContextCompat.getDrawable(activity, R.drawable.ic_baseline_save_24)
            binding.trainingSessionFragmentStartRestTime.iconTint =
                ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorTextPrimary))
        }
        binding.trainingSessionFragmentStartRestTime.text =
            getString(R.string.save_training_session)
        binding.trainingSessionFragmentRestTime.visibility = View.INVISIBLE
    }

    private fun saveWorkoutCompleted() {
        // Save the current series in the corresponding exercise
        when {
            exerciseToComplete.exerciseId.isNullOrEmpty() -> {
                addCurrentExerciseAndSeriesToComplete()
            }

            exerciseToComplete.exerciseId == exercise?.exerciseId -> {
                // Add the next series to complete with the current series
                exerciseToComplete.seriesList.add(currentSeriesList[0])
            }

            else -> {
                // Add the previous exercise to the list
                exerciseToComplete.let { workoutToComplete.exercisesList.add(it) }
                addCurrentExerciseAndSeriesToComplete()
            }
        }
    }

    private fun addCurrentExerciseAndSeriesToComplete() {
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
        exerciseToComplete.seriesList.add(currentSeriesList[0])
    }

    private fun saveTrainingSession() {
        // Get the workout id and name
        workoutToComplete.workoutId = trainingSession?.workout?.workoutId
        workoutToComplete.workoutName = trainingSession?.workout?.workoutName

        // Add the last exercise completed to the list
        exerciseToComplete.let { workoutToComplete.exercisesList.add(it) }

        // Create the training session to save
        val trainingSessionToSave = TrainingSession(
            trainingSession?.trainingSessionId, trainingSession?.trainingSessionDate,
            true, workoutToComplete
        )
        Log.d(TAG, "TrainingSessionToSave = $trainingSessionToSave")

        // Update the training session on Firestore
        userId?.let {
            trainingSessionViewModel.updateTrainingSession(it, trainingSessionToSave)
                ?.addOnSuccessListener {
                    myUtils.showSnackBar(
                        binding.trainingSessionFragmentCoordinatorLayout,
                        R.string.training_session_completed
                    )
                    Log.d(TAG, "DocumentSnapshot successfully updated!")
                    closeFragment()
                }
        }
    }
}