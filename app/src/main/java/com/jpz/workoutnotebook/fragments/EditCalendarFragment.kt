package com.jpz.workoutnotebook.fragments

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity.Companion.TRAINING_SESSION
import com.jpz.workoutnotebook.api.UserAuth
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.models.Workout
import com.jpz.workoutnotebook.utils.DatePickerFragment
import com.jpz.workoutnotebook.utils.DatePickerFragment.Companion.BUNDLE_KEY_DAY
import com.jpz.workoutnotebook.utils.DatePickerFragment.Companion.BUNDLE_KEY_MONTH
import com.jpz.workoutnotebook.utils.DatePickerFragment.Companion.BUNDLE_KEY_YEAR
import com.jpz.workoutnotebook.utils.DatePickerFragment.Companion.REQUEST_KEY_DATE
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.utils.TimePickerFragment
import com.jpz.workoutnotebook.utils.TimePickerFragment.Companion.BUNDLE_KEY_HOUR
import com.jpz.workoutnotebook.utils.TimePickerFragment.Companion.BUNDLE_KEY_MINUTE
import com.jpz.workoutnotebook.utils.TimePickerFragment.Companion.REQUEST_KEY_TIME
import com.jpz.workoutnotebook.viewmodels.TrainingSessionViewModel
import com.jpz.workoutnotebook.viewmodels.WorkoutViewModel
import kotlinx.android.synthetic.main.fragment_edit_calendar.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class EditCalendarFragment : Fragment(), View.OnClickListener {

    companion object {
        private val TAG = EditCalendarFragment::class.java.simpleName
        private const val WORKOUT_NAME_FIELD = "workoutName"
    }

    private var calendar = Calendar.getInstance()
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var minute = 0

    private var userId: String? = null

    private var allWorkoutName = mutableListOf<CharSequence>()

    // Firebase Auth, Firestore and utils
    private val userAuth: UserAuth by inject()
    private val workoutViewModel: WorkoutViewModel by viewModel()
    private val trainingSessionViewModel: TrainingSessionViewModel by viewModel()
    private val myUtils: MyUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use the Kotlin extension in the fragment-ktx artifact for setFragmentResultListener
        // Listen the result from DatePickerFragment
        childFragmentManager.setFragmentResultListener(REQUEST_KEY_DATE, this) { _, bundle ->
            editCalendarFragmentDate.text = setCalendarDate(bundle)
        }
        // Listen the result from TimePickerFragment
        childFragmentManager.setFragmentResultListener(REQUEST_KEY_TIME, this) { _, bundle ->
            editCalendarFragmentTime.text = setCalendarTime(bundle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid

        // TODO update trainingSession
        val trainingSession = arguments?.getParcelable<TrainingSession>(TRAINING_SESSION)
        Log.d(TAG, "trainingSession = $trainingSession")

        editCalendarFragmentButtonWorkout.setOnClickListener(this)
        editCalendarFragmentButtonDate.setOnClickListener(this)
        editCalendarFragmentButtonTime.setOnClickListener(this)
        editCalendarFragmentButtonSave.setOnClickListener(this)
    }

    //--------------------------------------------------------------------------------------
    // Methods to display data from pickers

    private fun setCalendarDate(bundle: Bundle): String {
        // Get data from bundle
        year = bundle.getInt(BUNDLE_KEY_YEAR)
        month = bundle.getInt(BUNDLE_KEY_MONTH)
        day = bundle.getInt(BUNDLE_KEY_DAY)
        Log.d(TAG, "year = $year, month = $month, day = $day")

        // Set calendar with the data from DatePickerFragment to display the date
        calendar.set(year, month, day, 0, 0)
        val dateChosen: Date = calendar.time
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateChosen)
    }

    private fun setCalendarTime(bundle: Bundle): String {
        // Get data from bundle
        hour = bundle.getInt(BUNDLE_KEY_HOUR)
        minute = bundle.getInt(BUNDLE_KEY_MINUTE)
        Log.d(TAG, "hour = $hour, minute = $minute")

        // Set calendar with the data from TimePickerFragment to display the time
        calendar.set(0, 0, 0, hour, minute)
        val timeChosen: Date = calendar.time
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(timeChosen)
    }

    //--------------------------------------------------------------------------------------

    // Get the list of all workouts
    private fun getAllWorkouts(allWorkoutName: MutableList<CharSequence>) {
        // Clear the list before use it
        allWorkoutName.clear()

        userId?.let { it ->
            // Get the workouts from Firestore Query
            workoutViewModel.getOrderedListOfExercises(it)?.get()
                ?.addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        myUtils.showSnackBar(
                            editCalendarFragmentCoordinatorLayout, R.string.no_workout
                        )
                    } else {
                        for (document in documents) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                            // Add the workouts name to the list
                            allWorkoutName.add(document.get(WORKOUT_NAME_FIELD) as CharSequence)
                        }
                        // Then show the AlertDialog with the list of workouts
                        addAWorkoutAlertDialog(allWorkoutName.toTypedArray())
                    }
                }
                ?.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    // Display the AlertDialog with the list of all the workouts
    private fun addAWorkoutAlertDialog(list: Array<CharSequence>): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.add_a_workout))
                .setNeutralButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .setItems(list) { _, which ->
                    // Display the workout chosen
                    editCalendarFragmentWorkout.text = list[which]
                }
                .create()
            builder.show()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    //----------------------------------------------------------------------------------
    // Methods to save a training session

    private fun saveTrainingSession() {
        if (editCalendarFragmentDate.text.isNullOrEmpty()
            || editCalendarFragmentTime.text.isNullOrEmpty()
            || editCalendarFragmentWorkout.text.isNullOrEmpty()
        ) {
            myUtils.showSnackBar(
                editCalendarFragmentCoordinatorLayout, R.string.add_date_time_workout
            )
        } else {
            if (userId != null) {
                // Retrieve the workout from its name
                workoutViewModel.getWorkout(userId!!, editCalendarFragmentWorkout.text as String)
                    ?.addOnSuccessListener { documentSnapshot ->
                        val workoutToAdd = documentSnapshot.toObject(Workout::class.java)
                        if (workoutToAdd != null) {
                            // Create the training session
                            trainingSessionViewModel.createTrainingSession(
                                editCalendarFragmentCoordinatorLayout,
                                userId!!, getTrainingSessionDate(), workoutToAdd
                            )
                        }
                    }
                closeFragment()
            }
        }
    }

    // Get the date and time chosen
    private fun getTrainingSessionDate(): String {
        calendar.set(year, month, day, hour, minute)
        val date: Date = calendar.time
        // Create a SimpleDateFormat to format and store trainingSessionDate in Firestore
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        Log.d(TAG, "trainingSessionDate = ${sdf.format(date)}")
        return sdf.format(date)
    }

    //----------------------------------------------------------------------------------

    private fun closeFragment() {
        editCalendarFragmentProgressBar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            activity?.onBackPressed()
        }, 2000)
    }

    //--------------------------------------------------------------------------------------

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.editCalendarFragmentButtonWorkout -> getAllWorkouts(allWorkoutName)

            R.id.editCalendarFragmentButtonDate -> {
                val datePicker = DatePickerFragment()
                datePicker.show(childFragmentManager, DatePickerFragment()::class.java.simpleName)
            }

            R.id.editCalendarFragmentButtonTime -> {
                val timePicker = TimePickerFragment()
                timePicker.show(childFragmentManager, TimePickerFragment::class.java.simpleName)
            }

            R.id.editCalendarFragmentButtonSave -> saveTrainingSession()
        }
    }
}