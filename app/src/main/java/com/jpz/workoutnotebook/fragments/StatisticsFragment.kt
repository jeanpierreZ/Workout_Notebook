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
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.viewmodels.ExerciseViewModel
import com.jpz.workoutnotebook.viewmodels.TrainingSessionViewModel
import kotlinx.android.synthetic.main.fragment_statistics.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


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

    // List of all exercises
    private val allExercises = arrayListOf<Exercise>()

    // List of all exercise names to display in the dropDownMenu
    private val exerciseNamesList = arrayListOf<String>()

    // List of all exercises completed from the training sessions
    private val exercisesCompletedList = arrayListOf<Exercise>()

    // Number of series from the exercise chosen
    private var seriesListSize = 0

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
    }

    //----------------------------------------------------------------------------------
    // Methods to get all exercises from Firestore and display it in the dropDownMenu

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
            getStatisticsFromExerciseNameChosen(text.toString())
        }
    }

    //----------------------------------------------------------------------------------
    // Methods to get statistics and display the chart

    private fun getStatisticsFromExerciseNameChosen(exerciseName: String) {
        var exerciseChosen = Exercise()

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
                                    // Fin the exercise with its id for each training session
                                    trainingSession?.workout?.exercisesList?.forEach { exercise ->
                                        if (exercise.exerciseId == exerciseChosen.exerciseId) {
                                            // Add the exercise to a list of exercises completed
                                            exercisesCompletedList.add(exercise)
                                            // Display the chart with the list
                                            if ((index + 1) == documents.size()) {
                                                // Display the statistics
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    createDataForChart(exercisesCompletedList)
                                                }, 500)
                                            }
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

    private fun createDataForChart(exercisesCompletedList: ArrayList<Exercise>) {
        Log.d(TAG, "exercisesCompletedList = $exercisesCompletedList")

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
        displayChart(arrayListOfAASeriesElement.toTypedArray())
    }

    private fun displayChart(arrayListOfAASeriesElement: Array<AASeriesElement>) {
        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .yAxisTitle("Repetitions")
            .yAxisAllowDecimals(false)
            .backgroundColor(R.color.colorTextSecondary)
//            .categories(arrayOf("18/10", "19/10", "20/10"))
            .series(arrayListOfAASeriesElement)
        // The chart view object calls the instance object of AAChartModel and draws the final graphic
        fragmentStatisticsChartView.aa_drawChartWithChartModel(aaChartModel)
    }
}