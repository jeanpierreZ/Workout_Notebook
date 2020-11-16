package com.jpz.workoutnotebook.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity.Companion.TRAINING_SESSION
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.models.Workout
import com.jpz.workoutnotebook.repositories.UserAuth
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
        private const val TRAINING_SESSION_DATE_FIELD = "trainingSessionDate"
    }

    private var calendar = Calendar.getInstance()
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var minute = 0

    // SimpleDateFormat is used to get the format of the trainingSessionDate
    private val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    private var userId: String? = null

    // TrainingSession properties
    private var workoutIdChosen: String? = null
    private var trainingSessionId: String? = null
    private var trainingDateToUpdate: String? = null

    private var trainingSession: TrainingSession? = null
    private var toUpdate = false

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
        trainingSession = arguments?.getParcelable(TRAINING_SESSION)
        Log.d(TAG, "trainingSession = $trainingSession")
        if (trainingSession != null) {
            setHasOptionsMenu(true)
        }
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

        // Get TrainingSession data
        trainingSession?.let { getTrainingSessionData(it) }

        editCalendarFragmentButtonWorkout.setOnClickListener(this)
        editCalendarFragmentButtonDate.setOnClickListener(this)
        editCalendarFragmentButtonTime.setOnClickListener(this)
        editCalendarFragmentButtonSave.setOnClickListener(this)
    }

    //--------------------------------------------------------------------------------------

    private fun getTrainingSessionData(trainingSession: TrainingSession) {
        trainingSessionId = trainingSession.trainingSessionId
        workoutIdChosen = trainingSession.workout?.workoutId
        // Get initial training session date to compare it later
        trainingDateToUpdate = trainingSession.trainingSessionDate
        toUpdate = true

        trainingDateToUpdate?.let {
            // Get the date
            val parsedDate: Date? = sdf.parse(it)
            Log.d(TAG, "parsedDate = $parsedDate")

            parsedDate?.let {
                // Get calendar time from parsedDate
                calendar.time = parsedDate
                // Set values with the updated calendar
                year = calendar.get(Calendar.YEAR)
                month = calendar.get(Calendar.MONTH)
                day = calendar.get(Calendar.DATE)
                hour = calendar.get(Calendar.HOUR_OF_DAY)
                minute = calendar.get(Calendar.MINUTE)
                // Set textViews with date and time
                editCalendarFragmentDate.text =
                    DateFormat.getDateInstance(DateFormat.MEDIUM).format(parsedDate)
                editCalendarFragmentTime.text =
                    DateFormat.getTimeInstance(DateFormat.SHORT).format(parsedDate)
            }
            // Set textView with workoutName
            editCalendarFragmentWorkout.text = trainingSession.workout?.workoutName
            // Set title for update
            editCalendarFragmentTitle.text = getString(R.string.update_training_session)
        }
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
    private fun getAllWorkouts() {
        val allWorkouts = arrayListOf<Workout>()

        userId?.let { it ->
            // Get the workouts from Firestore Query
            workoutViewModel.getOrderedListOfWorkouts(it)?.get()
                ?.addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        myUtils.showSnackBar(
                            editCalendarFragmentCoordinatorLayout, R.string.no_workout
                        )
                    } else {
                        for (document in documents) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                            val workoutToAdd = document.toObject(Workout::class.java)
                            // Add the workouts to the list
                            allWorkouts.add(workoutToAdd)
                        }
                        // Then show the AlertDialog
                        addAWorkoutAlertDialog(allWorkouts)
                    }
                }
                ?.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    // Display the AlertDialog with the list of the workouts, in order to add one to the training session
    private fun addAWorkoutAlertDialog(workouts: ArrayList<Workout>): Dialog {
        // Create a list of workout names to display on the AlertDialog
        val workoutNamesToDisplay = arrayListOf<String>()
        // Add all workout names to this list
        for (workout in workouts) {
            workout.workoutName?.let { workoutNamesToDisplay.add(it) }
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.add_a_workout))
                .setNeutralButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .setItems(workoutNamesToDisplay.toTypedArray()) { _, which ->
                    // Retrieve the workoutId from the list position
                    workoutIdChosen = workouts[which].workoutId
                    // Display the workout name chosen in textView
                    editCalendarFragmentWorkout.text = workoutNamesToDisplay[which]
                }
                .create()
            builder.show()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    //----------------------------------------------------------------------------------
    // Methods to create or update a training session

    private fun closeFragment() {
        activity?.let { myUtils.closeFragment(editCalendarFragmentProgressBar, it) }
        editCalendarFragmentButtonSave.isEnabled = false
        setHasOptionsMenu(false)
    }

    private fun createOrUpdateTrainingSession() {
        val dateToRegister = calendar.time

        if (checkIfATextViewIsEmpty()) {
            return
        }

        if (checkIfDateToRegisterBeforeNow(dateToRegister)) {
            return
        }

        userId?.let {
            // If it is an update and it is the same date, update the training session
            if (trainingDateToUpdate == getTrainingSessionDateInSDFFormat(dateToRegister) && toUpdate) {
                createOrUpdateToFirestore(it, dateToRegister)
            } else {
                // If the date is different and it is not an update,
                // check if a trainingSession on this date (and time) already exists
                trainingSessionViewModel.getListOfTrainingSessions(it)
                    ?.whereEqualTo(
                        TRAINING_SESSION_DATE_FIELD,
                        getTrainingSessionDateInSDFFormat(dateToRegister)
                    )
                    ?.get()
                    ?.addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            // There is no document with this trainingSessionDate so create or update it
                            Log.d(TAG, "documents.isEmpty")
                            createOrUpdateToFirestore(it, dateToRegister)
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
            }
        }
    }

    private fun createOrUpdateToFirestore(userId: String, dateToRegister: Date) {
        // Retrieve the workout from its id
        workoutIdChosen?.let { workoutIdChosen ->
            workoutViewModel.getWorkout(userId, workoutIdChosen)
        }
            ?.addOnSuccessListener { documentSnapshot ->
                val thisWorkout = documentSnapshot.toObject(Workout::class.java)
                thisWorkout?.let { _ ->
                    if (toUpdate) {
                        // Update the training session
                        val trainingSession = TrainingSession(
                            trainingSessionId, getTrainingSessionDateInSDFFormat(dateToRegister),
                            false, thisWorkout
                        )
                        trainingSessionViewModel.updateTrainingSession(userId, trainingSession)
                            ?.addOnSuccessListener {
                                myUtils.showSnackBar(
                                    editCalendarFragmentCoordinatorLayout,
                                    R.string.training_session_updated
                                )
                                Log.d(TAG, "DocumentSnapshot successfully updated!")
                                closeFragment()
                            }
                    } else {
                        // Create the training session
                        val trainingSession = TrainingSession(
                            null, getTrainingSessionDateInSDFFormat(dateToRegister),
                            false, thisWorkout
                        )
                        trainingSessionViewModel.createTrainingSession(userId, trainingSession)
                            ?.addOnSuccessListener { documentReference ->
                                // Set trainingSessionId
                                trainingSessionViewModel.updateTrainingSessionIdAfterCreate(
                                    userId, documentReference
                                )
                                    ?.addOnSuccessListener {
                                        Log.d(
                                            TAG,
                                            "DocumentSnapshot written with id: ${documentReference.id}"
                                        )
                                        // Inform the user
                                        myUtils.showSnackBar(
                                            editCalendarFragmentCoordinatorLayout, getString(
                                                R.string.new_training_session_created,
                                                trainingSession.workout?.workoutName
                                            )
                                        )
                                        closeFragment()
                                    }
                            }
                    }
                }
            }
    }

    private fun checkIfATextViewIsEmpty(): Boolean {
        return if (editCalendarFragmentDate.text.isNullOrEmpty()
            || editCalendarFragmentTime.text.isNullOrEmpty()
            || editCalendarFragmentWorkout.text.isNullOrEmpty()
        ) {
            myUtils.showSnackBar(
                editCalendarFragmentCoordinatorLayout, R.string.add_date_time_workout
            )
            true
        } else false
    }

    private fun checkIfDateToRegisterBeforeNow(dateToRegister: Date): Boolean {
        val nowCalendar = Calendar.getInstance()
        val now: Date = nowCalendar.time
        return if (dateToRegister.before(now)) {
            // Cannot update a training session with a previous time
            myUtils.showSnackBar(
                editCalendarFragmentCoordinatorLayout,
                R.string.cannot_create_update_training_session_with_past_date
            )
            true
        } else false
    }

    //----------------------------------------------------------------------------------

    // Get the date (and time) chosen and format it
    private fun getTrainingSessionDateInSDFFormat(dateToRegister: Date): String {
        Log.d(TAG, "trainingSessionDate = ${sdf.format(dateToRegister)}")
        return sdf.format(dateToRegister)
    }

    //--------------------------------------------------------------------------------------

    private fun deleteATrainingSession(userId: String, trainingSession: TrainingSession) {
        // Create an alert dialog to prevent the user
        activity?.let { it ->
            AlertDialog.Builder(it)
                .setMessage(getString(R.string.delete_this_training_session))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // Delete the training session
                    trainingSessionViewModel.deleteATrainingSession(userId, trainingSession)
                        ?.addOnSuccessListener {
                            myUtils.showSnackBar(
                                editCalendarFragmentCoordinatorLayout,
                                R.string.training_session_deleted
                            )
                            Log.d(TAG, "DocumentSnapshot successfully deleted!")
                            closeFragment()
                        }
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                }
                .show()
        }
    }

    //--------------------------------------------------------------------------------------

    override fun onClick(v: View?) {
        val historical = false
        // This is used only for StatisticsFragment
        val entryDate = false

        when (v?.id) {
            R.id.editCalendarFragmentButtonWorkout -> getAllWorkouts()

            R.id.editCalendarFragmentButtonDate -> {
                val datePicker = DatePickerFragment(historical, entryDate)
                datePicker.show(childFragmentManager, DatePickerFragment::class.java.simpleName)
            }

            R.id.editCalendarFragmentButtonTime -> {
                val timePicker = TimePickerFragment()
                timePicker.show(childFragmentManager, TimePickerFragment::class.java.simpleName)
            }

            R.id.editCalendarFragmentButtonSave -> createOrUpdateTrainingSession()
        }
    }
}