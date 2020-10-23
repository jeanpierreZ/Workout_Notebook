package com.jpz.workoutnotebook.fragments

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColor
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
        private const val TRAINING_SESSION_DATE_FIELD = "trainingSessionDate"
    }

    private var calendar = Calendar.getInstance()
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var minute = 0

    // SimpleDateFormat is used get the format of the trainingSessionDate
    private val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    private var userId: String? = null
    private var allWorkoutName = mutableListOf<CharSequence>()
    private var trainingSession: TrainingSession? = null

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
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_calendar, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_delete, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_delete) {
            userId?.let { trainingSession?.let { it1 -> deleteATrainingSession(it, it1) } }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid

        trainingSession = arguments?.getParcelable(TRAINING_SESSION)
        Log.d(TAG, "trainingSession = $trainingSession")

        if (trainingSession != null) {
            trainingSession!!.trainingSessionDate?.let {

                // Get the date
                val parsedDate: Date? = sdf.parse(it)
                Log.d(TAG, "parsedDate = $parsedDate")
                if (parsedDate != null) {
                    // Get calendar time from parsedDate
                    calendar.time = parsedDate
                    // Set values with the updated calendar
                    year = calendar.get(Calendar.YEAR)
                    month = calendar.get(Calendar.MONTH)
                    day = calendar.get(Calendar.DATE)
                    hour = calendar.get(Calendar.HOUR)
                    minute = calendar.get(Calendar.MINUTE)
                    // Set textViews with date and time
                    editCalendarFragmentDate.text =
                        DateFormat.getDateInstance(DateFormat.MEDIUM).format(parsedDate)
                    editCalendarFragmentTime.text =
                        DateFormat.getTimeInstance(DateFormat.SHORT).format(parsedDate)
                }
                // Set textView with workoutName
                editCalendarFragmentWorkout.text = trainingSession!!.workout?.workoutName
            }
        }

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
        calendar.set(year, month, day, hour, minute)
        val dateChosen: Date = calendar.time
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateChosen)
    }

    private fun setCalendarTime(bundle: Bundle): String {
        // Get data from bundle
        hour = bundle.getInt(BUNDLE_KEY_HOUR)
        minute = bundle.getInt(BUNDLE_KEY_MINUTE)
        Log.d(TAG, "hour = $hour, minute = $minute")

        // Set calendar with the data from TimePickerFragment to display the time
        calendar.set(year, month, day, hour, minute)
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
    // Methods to save or update a training session

    private fun saveTrainingSession() {
        val nowCalendar = Calendar.getInstance()
        val now: Date = nowCalendar.time
        val dateToRegister = calendar.time

        if (editCalendarFragmentDate.text.isNullOrEmpty()
            || editCalendarFragmentTime.text.isNullOrEmpty()
            || editCalendarFragmentWorkout.text.isNullOrEmpty()
        ) {
            myUtils.showSnackBar(
                editCalendarFragmentCoordinatorLayout, R.string.add_date_time_workout
            )

        } else if (dateToRegister.before(now)) {
            // Cannot create a training session with a previous time
            myUtils.showSnackBar(
                editCalendarFragmentCoordinatorLayout,
                R.string.cannot_create_update_training_session_with_past_date
            )

        } else {
            if (userId != null) {
                // Check if a trainingSession on this date already exists
                trainingSessionViewModel.getListOfTrainingSessions(userId!!)
                    ?.whereEqualTo(
                        TRAINING_SESSION_DATE_FIELD,
                        getTrainingSessionDateInSDFFormat(dateToRegister)
                    )
                    ?.get()
                    ?.addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            // There isn't document with this trainingSessionDate so create it
                            Log.d(TAG, "documents.isEmpty")
                            // Retrieve the workout from its name
                            workoutViewModel.getWorkout(
                                userId!!, editCalendarFragmentWorkout.text as String
                            )
                                ?.addOnSuccessListener { documentSnapshot ->
                                    val workoutToAdd =
                                        documentSnapshot.toObject(Workout::class.java)
                                    if (workoutToAdd != null) {
                                        // Create the training session
                                        trainingSessionViewModel.createTrainingSession(
                                            editCalendarFragmentCoordinatorLayout,
                                            userId!!,
                                            getTrainingSessionDateInSDFFormat(dateToRegister),
                                            workoutToAdd
                                        )
                                    }
                                }
                            closeFragment()
                        } else {
                            // The same trainingSessionDate exists, choose another time
                            myUtils.showSnackBar(
                                editCalendarFragmentCoordinatorLayout,
                                R.string.training_session_time_already_exists
                            )
                            for (document in documents) {
                                Log.d(TAG, "${document.id} => ${document.data}")
                            }
                        }
                    }
                    ?.addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }
            }
        }
    }

    private fun updateTrainingSession() {
        val nowCalendar = Calendar.getInstance()
        val now: Date = nowCalendar.time
        val dateToRegister = calendar.time

        if (editCalendarFragmentDate.text.isNullOrEmpty()
            || editCalendarFragmentTime.text.isNullOrEmpty()
            || editCalendarFragmentWorkout.text.isNullOrEmpty()
        ) {
            myUtils.showSnackBar(
                editCalendarFragmentCoordinatorLayout, R.string.add_date_time_workout
            )

        } else if (dateToRegister.before(now)) {
            // Cannot update a training session with a previous time
            myUtils.showSnackBar(
                editCalendarFragmentCoordinatorLayout,
                R.string.cannot_create_update_training_session_with_past_date
            )

        } else {
            userId?.let { it ->
                // Retrieve the workout from its name
                workoutViewModel.getWorkout(
                    it, editCalendarFragmentWorkout.text as String
                )
                    ?.addOnSuccessListener { documentSnapshot ->
                        val workoutToUpdate =
                            documentSnapshot.toObject(Workout::class.java)
                        if (workoutToUpdate != null) {
                            // Update the training session
                            trainingSessionViewModel.updateTrainingSession(
                                editCalendarFragmentCoordinatorLayout,
                                it,
                                getTrainingSessionDateInSDFFormat(dateToRegister),
                                workoutToUpdate
                            )
                        }
                    }
            }
            closeFragment()
        }
    }

    //----------------------------------------------------------------------------------

    // Get the date (and time) chosen and format it
    private fun getTrainingSessionDateInSDFFormat(dateToRegister: Date): String {
        Log.d(TAG, "trainingSessionDate = ${sdf.format(dateToRegister)}")
        return sdf.format(dateToRegister)
    }

    //----------------------------------------------------------------------------------

    private fun closeFragment() {
        editCalendarFragmentProgressBar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            activity?.onBackPressed()
        }, 2000)
    }

    //--------------------------------------------------------------------------------------

    private fun deleteATrainingSession(userId: String, trainingSession: TrainingSession) {
        // Create an alert dialog to prevent the user
        activity?.let { it ->
            AlertDialog.Builder(it)
                .setMessage(getString(R.string.delete_this_training_session))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // Delete the training session
                    trainingSessionViewModel.deleteATrainingSession(
                        editCalendarFragmentCoordinatorLayout, userId, trainingSession, it
                    )
                    closeFragment()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                }
                .show()
        }
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

            R.id.editCalendarFragmentButtonSave ->
                if (trainingSession != null) {
                    updateTrainingSession()
                } else {
                    saveTrainingSession()
                }
        }
    }
}