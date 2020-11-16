package com.jpz.workoutnotebook.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.repositories.UserAuth
import com.jpz.workoutnotebook.utils.DatePickerFragment
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.ExerciseViewModel
import com.jpz.workoutnotebook.viewmodels.TrainingSessionViewModel
import kotlinx.android.synthetic.main.fragment_statistics.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class StatisticsFragment : Fragment() {

    companion object {
        private val TAG = StatisticsFragment::class.java.simpleName
        private const val TRAINING_SESSION_COMPLETED_FIELD = "trainingSessionCompleted"
    }

    private var userId: String? = null

    // Firebase Auth, Firestore and utils
    private val userAuth: UserAuth by inject()
    private val exerciseViewModel: ExerciseViewModel by viewModel()
    private val trainingSessionViewModel: TrainingSessionViewModel by viewModel()
    private val myUtils: MyUtils by inject()

    private var callback: StatisticsListener? = null

    // Exercise chosen by the user
    private var exerciseChosen = Exercise()

    // List of all exercises
    private val allExercises = arrayListOf<Exercise>()

    // List of all exercise names to display in the dropDownMenu
    private val exerciseNamesList = arrayListOf<String>()

    // List of all training sessions that contain the exercise chosen
    private val trainingSessionList = arrayListOf<TrainingSession>()

    // Number of series from the exercise chosen
    private var seriesListSize = 0

    private var calendarEntry = Calendar.getInstance()
    private var calendarEnd = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the Kotlin extension in the fragment-ktx artifact for setFragmentResultListener

        // Listen the result from DatePickerFragment for entry date
        childFragmentManager.setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_ENTRY_DATE, this
        ) { _, bundle ->
            fragmentStatisticsEntryDate?.editText?.text =
                Editable.Factory.getInstance().newEditable(setCalendarEntryDate(bundle))
            displayExerciseEditText()
        }

        // Listen the result from DatePickerFragment for end date
        childFragmentManager.setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE, this
        ) { _, bundle ->
            fragmentStatisticsEndDate?.editText?.text =
                Editable.Factory.getInstance().newEditable(setCalendarEndDate(bundle))
            displayExerciseEditText()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid
        userId?.let { getAllExercises(it) }

        val allowPastDate = true
        val entryDate = true
        val endDate = false

        fragmentStatisticsEntryDate.editText?.setOnClickListener {
            val datePicker = DatePickerFragment(allowPastDate, entryDate)
            datePicker.show(childFragmentManager, DatePickerFragment::class.java.simpleName)
        }

        fragmentStatisticsEndDate.editText?.setOnClickListener {
            val datePicker = DatePickerFragment(allowPastDate, endDate)
            datePicker.show(childFragmentManager, DatePickerFragment::class.java.simpleName)
        }
    }

    //----------------------------------------------------------------------------------
    // Methods to get all exercises from Firestore and display it in the dropDownMenu

    // Display fragmentStatisticsExerciseChosen if date are sets.
    // Warn the user if entry date is after end date.
    private fun displayExerciseEditText() {
        if (fragmentStatisticsEntryDate.editText?.text != null && fragmentStatisticsEndDate?.editText?.text != null) {
            if (fragmentStatisticsEntryDate.editText?.text?.isNotEmpty()!! && fragmentStatisticsEndDate?.editText?.text?.isNotEmpty()!!) {
                if (calendarEntry.time.after(calendarEnd.time)) {
                    callback?.entryDateAfterEndDate(R.string.entry_date_cannot_be_after_end_date)
                } else {
                    fragmentStatisticsExerciseChosen.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getAllExercises(userId: String) {
        exerciseViewModel.getOrderedListOfExercises(userId)?.get()
            ?.addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    myUtils.showSnackBar(
                        fragmentStatisticsCoordinatorLayout, R.string.no_exercise
                    )
                } else {
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val exerciseToAdd = document.toObject(Exercise::class.java)
                        // Add the exercises to a list
                        allExercises.add(exerciseToAdd)
                        // Add the exercise names to a list
                        exerciseToAdd.exerciseName?.let { exerciseName ->
                            exerciseNamesList.add(exerciseName)
                        }
                    }
                    // Then show the DropDownMenu
                    activity?.let { activity -> dropDownMenu(activity) }
                }
            }
            ?.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun dropDownMenu(context: Context) {
        val adapter =
            ArrayAdapter(
                context, R.layout.unit_list_item, R.id.unitListItemTextView, exerciseNamesList
            )
        (fragmentStatisticsExerciseChosen?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        // Get statistics from the exercise chosen in the dropDownMenu
        fragmentStatisticsAutoCompleteTextView.doAfterTextChanged { text: Editable? ->
            if (trainingSessionList.isNotEmpty()) {
                trainingSessionList.clear()
            }
            fragmentStatisticsProgressBar.visibility = View.VISIBLE
            getStatisticsFromExerciseNameChosen(text.toString())
        }
    }

    //--------------------------------------------------------------------------------------
    // Methods to display data from pickers

    private fun setCalendarEntryDate(bundle: Bundle): String {
        // Get data from bundle
        val year = bundle.getInt(DatePickerFragment.BUNDLE_KEY_YEAR)
        val month = bundle.getInt(DatePickerFragment.BUNDLE_KEY_MONTH)
        val day = bundle.getInt(DatePickerFragment.BUNDLE_KEY_DAY)
        Log.d(TAG, "yearEntry = $year, monthEntry = $month, dayEntry = $day")

        // Set calendar with the data from DatePickerFragment to display the entry date
        calendarEntry.set(year, month, day)
        val dateChosen: Date = calendarEntry.time
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateChosen)
    }

    private fun setCalendarEndDate(bundle: Bundle): String {
        // Get data from bundle
        val year = bundle.getInt(DatePickerFragment.BUNDLE_KEY_YEAR)
        val month = bundle.getInt(DatePickerFragment.BUNDLE_KEY_MONTH)
        val day = bundle.getInt(DatePickerFragment.BUNDLE_KEY_DAY)
        Log.d(TAG, "yearEnd = $year, monthEnd = $month, dayEnd = $day")

        // Set calendar with the data from DatePickerFragment to display the end date
        calendarEnd.set(year, month, day)
        val dateChosen: Date = calendarEnd.time
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateChosen)
    }

    //----------------------------------------------------------------------------------
    // Methods to get statistics and display the chart

    private fun getStatisticsFromExerciseNameChosen(exerciseName: String) {
        // Find the Exercise with its name
        for (exercise in allExercises) {
            if (exercise.exerciseName == exerciseName) {
                exerciseChosen = exercise
                Log.d(TAG, "exerciseChosen = $exerciseChosen")
                break
            }
        }

        // Get the number of series for the exercise chosen
        seriesListSize = exerciseChosen.seriesList.size
        Log.d(TAG, "seriesListSize = $seriesListSize")

        userId?.let {
            // Get training sessions that are completed
            trainingSessionViewModel.getListOfTrainingSessions(it)
                ?.whereEqualTo(TRAINING_SESSION_COMPLETED_FIELD, true)
                ?.get()
                ?.addOnSuccessListener { documents ->
                    Log.d(TAG, "documents.size = ${documents.size()}")
                    Log.d(TAG, "documents = ${documents?.documents}")
                    if (documents.isEmpty) {
                        Log.w(TAG, "documents.isEmpty")
                    } else {
                        for ((index, value) in documents.withIndex()) {
                            // For each training session
                            trainingSessionViewModel.getTrainingSession(it, value.id)
                                ?.addOnSuccessListener { doc ->
                                    // Get TrainingSession object
                                    val trainingSession = doc.toObject(TrainingSession::class.java)
                                    Log.d(TAG, "trainingSession = $trainingSession")
                                    // Search if the exercise is in the training session exercises list
                                    trainingSession?.workout?.exercisesList?.forEach { exercise ->
                                        if (exercise.exerciseId == exerciseChosen.exerciseId) {
                                            // Add the training session to the list
                                            trainingSessionList.add(trainingSession)
                                        }
                                    }
                                    // Display the chart with the list
                                    if ((index + 1) == documents.size()) {
                                        if (trainingSessionList.isEmpty()) {
                                            fragmentStatisticsProgressBar.visibility =
                                                View.INVISIBLE
                                            exerciseChosen.exerciseName?.let { exerciseName ->
                                                callback?.noData(exerciseName)
                                            }
                                        } else {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                sortDataToDisplay(trainingSessionList)
                                            }, 500)
                                        }
                                    }
                                }
                                ?.addOnFailureListener { e ->
                                    Log.d(TAG, "getTrainingSession failed with ", e)
                                }
                        }
                    }
                }
                ?.addOnFailureListener { e -> Log.d(TAG, "get failed with ", e) }
        }
    }

    private fun sortDataToDisplay(trainingSessionList: ArrayList<TrainingSession>) {
        Log.d(TAG, "trainingSessionList = $trainingSessionList")

        // --- Sort trainingSessionDate ---

        // SimpleDateFormat is used to format the trainingSessionDate
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

        // Create a list of Date
        val listOfDates = mutableListOf<Date>()

        trainingSessionList.forEach { training ->
            // Get the date format for each trainingSessionDate
            training.trainingSessionDate?.let {
                val parsedDate: Date? = sdf.parse(it)
                parsedDate?.let {
                    // Add it to a list of Date format
                    listOfDates.add(parsedDate)
                }
            }
        }
        // Sort the list
        listOfDates.sort()
        Log.d(TAG, "listOfDates = $listOfDates")

        // Create a list of training sessions sorted
        val trainingSessionListSorted = ArrayList<TrainingSession>()

        listOfDates.forEach { date ->
            // Get the string format for each date
            val dateString = sdf.format(date)
            for (training in trainingSessionList) {
                // if it's the same date, add it to the final training sessions list
                if (dateString == training.trainingSessionDate) {
                    trainingSessionListSorted.add(training)
                    break
                }
            }
        }
        Log.d(TAG, "trainingSessionListSorted = $trainingSessionListSorted")

        // x axis
        val xDates = arrayListOf<String>()

        // Create a list of exercises completed
        val exercisesCompletedList = arrayListOf<Exercise>()

        for (training in trainingSessionListSorted) {
            // Get the date of each training session
            training.trainingSessionDate?.let {
                // Format the trainingSessionDate
                val trainingSessionDateFormatted = it.removeRange(10, 16).removeRange(1, 3)
                // Add it to the data for x axis
                xDates.add(trainingSessionDateFormatted)
            }

            // Retrieve the exerciseChosen for each training session
            training.workout?.exercisesList?.forEach { exercise ->
                if (exercise.exerciseId == exerciseChosen.exerciseId) {
                    // Add it to a list of exercise
                    exercisesCompletedList.add(exercise)
                    return@forEach
                }
            }
        }

        // --- Sort reps ---

        // Create an arrayListOf AASeriesElement
        val arrayListOfAASeriesElement = arrayListOf<AASeriesElement>()

        // For each series in the exercise chosen
        for (i in 1..seriesListSize) {
            // Create an AASeriesElement
            val aaSeriesElement = AASeriesElement()
            // Add the name of the current series
            aaSeriesElement.name("Set $i")
            // Create a list of reps
            val arrayListOfReps = arrayListOf<Int>()

            // For each exercise in the list of exercises completed
            for (exercise in exercisesCompletedList) {
                // Add the reps
                if (i <= exercise.seriesList.size) {
                    arrayListOfReps.add(exercise.seriesList[i - 1].reps)
                } else {
                    arrayListOfReps.add(0)
                }
            }
            // Add the arrayListOfReps in the current aaSeriesElement
            aaSeriesElement.data(arrayListOfReps.toTypedArray())
            // Add this aaSeriesElement in the array list of AASeriesElement
            arrayListOfAASeriesElement.add(aaSeriesElement)
        }
        // Display the chart with data
        fragmentStatisticsProgressBar.visibility = View.INVISIBLE
        displayChart(xDates.toTypedArray(), arrayListOfAASeriesElement.toTypedArray())
    }

    private fun displayChart(
        xDates: Array<String>, arrayListOfAASeriesElement: Array<AASeriesElement>
    ) {
        val aaChartModel = AAChartModel()
        exerciseChosen.exerciseName?.let { aaChartModel.title(it) }
        aaChartModel
            .chartType(AAChartType.Line)
            .yAxisTitle(getString(R.string.repetitions))
            .yAxisAllowDecimals(false)
            .backgroundColor(R.color.colorTextSecondary)
            .categories(xDates)
            .series(arrayListOfAASeriesElement)
        // The chart view object calls the instance object of AAChartModel and draws the final graphic
        fragmentStatisticsChartView.aa_drawChartWithChartModel(aaChartModel)
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity when choose an exercise and there is no historical data

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the methods that creating callback after being attached to parent activity
        callbackToParentActivity()
    }

    // Declare our interface and methods that will be implemented by any container activity
    interface StatisticsListener {
        fun noData(exerciseName: String)
        fun entryDateAfterEndDate(message: Int)
    }

    // Create callback to parent activity
    private fun callbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callback = activity as StatisticsListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement StatisticsListener")
        }
    }
}