package com.jpz.workoutnotebook.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.api.UserAuth
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
import com.jpz.workoutnotebook.viewmodels.WorkoutViewModel
import kotlinx.android.synthetic.main.fragment_edit_calendar.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat
import java.util.*


class EditCalendarFragment : Fragment(), View.OnClickListener {

    companion object {
        private val TAG = EditCalendarFragment::class.java.simpleName
        private const val WORKOUT_NAME_FIELD = "workoutName"
    }

    private val calendar = Calendar.getInstance()

    private var userId: String? = null

    private var allWorkoutName = mutableListOf<CharSequence>()

    // Firebase Auth, Firestore and utils
    private val userAuth: UserAuth by inject()
    private val workoutViewModel: WorkoutViewModel by viewModel()
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

        editCalendarFragmentButtonWorkout.setOnClickListener(this)
        editCalendarFragmentButtonDate.setOnClickListener(this)
        editCalendarFragmentButtonTime.setOnClickListener(this)
    }

    //--------------------------------------------------------------------------------------
    // Methods to display data from pickers

    private fun setCalendarDate(bundle: Bundle): String {
        // Get data from bundle
        val year = bundle.getInt(BUNDLE_KEY_YEAR)
        val month = bundle.getInt(BUNDLE_KEY_MONTH)
        val day = bundle.getInt(BUNDLE_KEY_DAY)
        Log.d(TAG, "year = $year, month = $month, day = $day")

        // Set calendar with the data from DatePickerFragment to display the date
        calendar.set(year, month, day, 0, 0)
        val dateChosen: Date = calendar.time
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateChosen)
    }

    private fun setCalendarTime(bundle: Bundle): String {
        // Get data from bundle
        val hour = bundle.getInt(BUNDLE_KEY_HOUR)
        val minute = bundle.getInt(BUNDLE_KEY_MINUTE)
        Log.d(TAG, "hour = $hour, minute = $minute")

        // Set calendar with the data from TimePickerFragment to display the time
        calendar.set(0, 0, 0, hour, minute)
        val timeChosen: Date = calendar.time
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(timeChosen)
    }

    //--------------------------------------------------------------------------------------

    // Get the list of all workouts
    private fun getAllWorkouts(allExerciseName: MutableList<CharSequence>) {
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
                        addAWorkoutAlertDialog(allExerciseName.toTypedArray())
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
        }
    }
}