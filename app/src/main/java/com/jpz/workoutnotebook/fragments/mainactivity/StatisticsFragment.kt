package com.jpz.workoutnotebook.fragments.mainactivity

import android.content.Context
import android.os.Bundle
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
        private const val TRAINING_SESSION_DATE_FIELD = "trainingSessionDate"
    }

    private var userId: String? = null

    // Firebase Auth, Firestore and utils
    private val userAuth: UserAuth by inject()
    private val exerciseViewModel: ExerciseViewModel by viewModel()
    private val trainingSessionViewModel: TrainingSessionViewModel by viewModel()
    private val myUtils: MyUtils by inject()

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

    // SimpleDateFormat is used to format the trainingSessionDate
    private val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    // Boolean to display reps or unit
    private var isForReps = true

    private var yAxis = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the Kotlin extension in the fragment-ktx artifact for setFragmentResultListener

        // Listen the result from DatePickerFragment for entry date
        childFragmentManager.setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_ENTRY_DATE, this
        ) { _, bundle ->
            fragmentStatisticsEntryDate?.editText?.text =
                Editable.Factory.getInstance().newEditable(setCalendarEntryDate(bundle))

            if (fragmentStatisticsExerciseChosen.visibility == View.INVISIBLE) {
                displayExerciseEditText()
            }

            if (fragmentStatisticsExerciseChosen.visibility == View.VISIBLE && allExercises.isEmpty()) {
                userId?.let { getAllExercises(it) }
            }
        }

        // Listen the result from DatePickerFragment for end date
        childFragmentManager.setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE, this
        ) { _, bundle ->
            fragmentStatisticsEndDate?.editText?.text =
                Editable.Factory.getInstance().newEditable(setCalendarEndDate(bundle))

            if (fragmentStatisticsExerciseChosen.visibility == View.INVISIBLE) {
                displayExerciseEditText()
            }

            if (fragmentStatisticsExerciseChosen.visibility == View.VISIBLE && allExercises.isEmpty()) {
                userId?.let { getAllExercises(it) }
            }
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

        val historical = true
        val entryDate = true
        val endDate = false

        fragmentStatisticsEntryDate.editText?.setOnClickListener {
            val datePicker = DatePickerFragment(historical, entryDate)
            datePicker.show(childFragmentManager, DatePickerFragment::class.java.simpleName)
        }

        fragmentStatisticsEndDate.editText?.setOnClickListener {
            val datePicker = DatePickerFragment(historical, endDate)
            datePicker.show(childFragmentManager, DatePickerFragment::class.java.simpleName)
        }

        activity?.let { unitDropDownMenu(it) }
    }

    //----------------------------------------------------------------------------------

    private fun unitDropDownMenu(context: Context) {
        val list = arrayListOf(getString(R.string.reps), getString(R.string.unit))
        val adapter =
            ArrayAdapter(context, R.layout.unit_list_item, R.id.unitListItemTextView, list)
        (fragmentStatisticsUnit?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        // Get reps or unit
        fragmentStatisticsUnitAutoCompleteTextView.doAfterTextChanged { text: Editable? ->
            isForReps = text.toString() == getString(R.string.reps)
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
                    myUtils.showSnackBar(
                        fragmentStatisticsCoordinatorLayout,
                        R.string.entry_date_cannot_be_after_end_date
                    )
                } else {
                    fragmentStatisticsExerciseChosen.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getAllExercises(userId: String) {
        exerciseViewModel.getOrderedListOfExercises(userId).get()
            .addOnSuccessListener { documents ->
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
                    // Then show the exerciseDropDownMenu
                    activity?.let { activity -> exerciseDropDownMenu(activity) }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun exerciseDropDownMenu(context: Context) {
        val adapter =
            ArrayAdapter(
                context, R.layout.unit_list_item, R.id.unitListItemTextView, exerciseNamesList
            )
        (fragmentStatisticsExerciseChosen?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        // Get statistics from the exercise chosen in the dropDownMenu
        fragmentStatisticsExerciseAutoCompleteTextView.doAfterTextChanged { text: Editable? ->
            if (trainingSessionList.isNotEmpty()) {
                trainingSessionList.clear()
            }
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
        calendarEntry.set(year, month, day, 0, 0)
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
        calendarEnd.set(year, month, day, 23, 59)
        val dateChosen: Date = calendarEnd.time
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateChosen)
    }

    //----------------------------------------------------------------------------------
    // Methods to get statistics and display the chart

    private fun getStatisticsFromExerciseNameChosen(exerciseName: String) {
        // Return if entry date is after end date
        if (calendarEntry.time.after(calendarEnd.time)) {
            myUtils.showSnackBar(
                fragmentStatisticsCoordinatorLayout, R.string.entry_date_cannot_be_after_end_date
            )
            return
        }

        fragmentStatisticsProgressBar.visibility = View.VISIBLE

        // Get the string format for each date
        val entryDate: Date = calendarEntry.time
        val endDate: Date = calendarEnd.time
        val entryDateString = sdf.format(entryDate)
        val endDateString = sdf.format(endDate)

        // Find the Exercise with its name
        for (exercise in allExercises) {
            if (exercise.exerciseName == exerciseName) {
                exerciseChosen = exercise
                Log.d(TAG, "exerciseChosen = $exerciseChosen")
                break
            }
        }

        // Get the number of series by default for the exercise chosen
        seriesListSize = exerciseChosen.seriesList.size
        Log.d(TAG, "seriesListSize = $seriesListSize")

        userId?.let {
            // Get all training sessions that are completed and according to dates
            trainingSessionViewModel.getListOfTrainingSessions(it)
                .whereEqualTo(TRAINING_SESSION_COMPLETED_FIELD, true)
                .whereGreaterThanOrEqualTo(TRAINING_SESSION_DATE_FIELD, entryDateString)
                .whereLessThanOrEqualTo(TRAINING_SESSION_DATE_FIELD, endDateString)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.isComplete) {
                        val document = task.result
                        Log.d(TAG, "document.size = ${document?.size()}")
                        if (document != null) {
                            if (document.documents.isEmpty()) {
                                Log.w(TAG, "documents.isEmpty")
                                fragmentStatisticsProgressBar.visibility = View.INVISIBLE
                                exerciseChosen.exerciseName?.let { exerciseName ->
                                    myUtils.showSnackBar(
                                        fragmentStatisticsCoordinatorLayout,
                                        getString(R.string.no_data, exerciseName)
                                    )
                                }
                            } else {
                                document.documents.forEach { eachDocument ->
                                    // For each training session, get the TrainingSession object
                                    val trainingSession =
                                        eachDocument.toObject(TrainingSession::class.java)
                                    // Search if the exercise is in the list
                                    trainingSession?.workout?.exercisesList?.forEach { exercise ->
                                        if (exercise.exerciseId == exerciseChosen.exerciseId) {
                                            // Add the training session to the list
                                            trainingSessionList.add(trainingSession)
                                        }
                                    }
                                }
                                // Then check if the list is not empty and...
                                if (trainingSessionList.isEmpty()) {
                                    fragmentStatisticsProgressBar.visibility =
                                        View.INVISIBLE
                                    exerciseChosen.exerciseName?.let { exerciseName ->
                                        myUtils.showSnackBar(
                                            fragmentStatisticsCoordinatorLayout,
                                            getString(R.string.no_data, exerciseName)
                                        )
                                    }
                                } else {
                                    // ...display the chart with the list
                                    sortDataToDisplay(trainingSessionList)
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "Task get failed: ", task.exception)
                    }
                }
                .addOnFailureListener { e -> Log.d(TAG, "get failed with ", e) }
        }
    }

    private fun sortDataToDisplay(trainingSessionList: ArrayList<TrainingSession>) {
        Log.d(TAG, "trainingSessionList = $trainingSessionList")
        Log.d(TAG, "trainingSessionList.size = ${trainingSessionList.size}")

        // --- Sort trainingSessionDate ---

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

        // --- Add reps or unit ---

        // Create an arrayListOf AASeriesElement
        val arrayListOfAASeriesElement = arrayListOf<AASeriesElement>()

        // For each series in the exercise chosen
        for (i in 1..seriesListSize) {
            // Create an AASeriesElement
            val aaSeriesElement = AASeriesElement()
            // Add the name of the current series
            aaSeriesElement.name("Set $i")

            // Create a list of reps or unit
            val arrayListOfRepsOrUnit = arrayListOf<Any>()

            if (isForReps) {
                // For each exercise in the list of exercises completed
                for (exercise in exercisesCompletedList) {
                    // Add the reps
                    if (i <= exercise.seriesList.size) {
                        arrayListOfRepsOrUnit.add(exercise.seriesList[i - 1].reps)
                    } else {
                        arrayListOfRepsOrUnit.add(0)
                    }
                }
            } else {
                // For each exercise in the list of exercises completed
                for (exercise in exercisesCompletedList) {
                    // Add the number of unit
                    if (i <= exercise.seriesList.size) {
                        arrayListOfRepsOrUnit.add(exercise.seriesList[i - 1].numberOfUnit)
                    } else {
                        arrayListOfRepsOrUnit.add(0.0)
                    }
                }
            }

            // Add the arrayListOfReps in the current aaSeriesElement
            aaSeriesElement.data(arrayListOfRepsOrUnit.toTypedArray())
            // Add this aaSeriesElement in the array list of AASeriesElement
            arrayListOfAASeriesElement.add(aaSeriesElement)
        }
        // Display the chart with data
        fragmentStatisticsProgressBar.visibility = View.INVISIBLE
        yAxis = if (isForReps) {
            getString(R.string.repetitions)
        } else {
            exerciseChosen.seriesList[0].unit
        }
        displayChart(xDates.toTypedArray(), arrayListOfAASeriesElement.toTypedArray())
    }

    private fun displayChart(
        xDates: Array<String>, arrayListOfAASeriesElement: Array<AASeriesElement>
    ) {
        val aaChartModel = AAChartModel()
        exerciseChosen.exerciseName?.let { aaChartModel.title(it) }

        if (isForReps) {
            aaChartModel.yAxisAllowDecimals(false)
        } else {
            aaChartModel.yAxisAllowDecimals(true)
        }

        aaChartModel
            .chartType(AAChartType.Line)
            .yAxisTitle(yAxis)
            .backgroundColor(R.color.colorTextSecondary)
            .categories(xDates)
            .series(arrayListOfAASeriesElement)
        // The chart view object calls the instance object of AAChartModel and draws the final graphic
        fragmentStatisticsChartView.aa_drawChartWithChartModel(aaChartModel)
    }
}