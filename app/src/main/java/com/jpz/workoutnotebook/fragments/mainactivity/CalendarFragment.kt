package com.jpz.workoutnotebook.fragments.mainactivity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ItemTrainingSessionAdapter
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.repositories.UserAuth
import com.jpz.workoutnotebook.viewmodels.TrainingSessionViewModel
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment : Fragment(), ItemTrainingSessionAdapter.Listener {

    companion object {
        private val TAG = CalendarFragment::class.java.simpleName
        private const val TRAINING_SESSION_DATE_FIELD = "trainingSessionDate"
    }

    private var userId: String? = null

    // Firebase Auth, Firestore
    private val userAuth: UserAuth by inject()
    private val trainingSessionViewModel: TrainingSessionViewModel by viewModel()

    private var itemTrainingSessionAdapter: ItemTrainingSessionAdapter? = null

    // SimpleDateFormat is used to store (and compare) the dates in the trainingSessionList
    private val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    private var callback: CalendarListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onResume() {
        super.onResume()
        configureRecyclerView(arrayListOf())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid

        addTrainingSessionsToCalendarView()

        calendarFragmentCalendarView.setOnPreviousPageChangeListener(
            object : OnCalendarPageChangeListener {
                override fun onChange() {
                    addTrainingSessionsToCalendarView()
                    configureRecyclerView(arrayListOf())
                }
            })

        calendarFragmentCalendarView.setOnForwardPageChangeListener(
            object : OnCalendarPageChangeListener {
                override fun onChange() {
                    addTrainingSessionsToCalendarView()
                    configureRecyclerView(arrayListOf())
                }
            })

        calendarFragmentCalendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                // Get the eventDay from the calendarView
                val clickedDayCalendar: Calendar = eventDay.calendar
                trainingSessionsFromOnDayClick(clickedDayCalendar)
            }
        })
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    private fun configureRecyclerView(list: ArrayList<TrainingSession>) {
        // Create the adapter by passing the list of training sessions
        itemTrainingSessionAdapter =
            activity?.let { ItemTrainingSessionAdapter(list, it, this) }
        // Attach the adapter to the recyclerView to populate the training sessions
        calendarFragmentRecyclerView?.adapter = itemTrainingSessionAdapter
        // Set layout manager to position the training sessions
        calendarFragmentRecyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    //----------------------------------------------------------------------------------
    // Methods to have and convert the dates from the click event on the calendar

    private fun getDateOfTraining(
        yearOfTraining: Int, monthOfTraining: Int, dayOfTraining: Int, sdf: SimpleDateFormat
    ): String {
        // Instantiate a Calendar to have only the date of Training
        val calendarOfTraining: Calendar = Calendar.getInstance()
        calendarOfTraining.set(yearOfTraining, monthOfTraining, dayOfTraining, 0, 0)
        val dateOfTraining: Date = calendarOfTraining.time

        // Parse the date in SimpleDateFormat to compare it with the trainingSessionList
        Log.d(TAG, "parsedDateOfTraining = ${sdf.format(dateOfTraining)}")
        return sdf.format(dateOfTraining)
    }

    private fun getDayAfterTraining(
        yearOfTraining: Int, monthOfTraining: Int, dayOfTraining: Int, sdf: SimpleDateFormat
    ): String {
        // Instantiate a Calendar to have only the day after training
        val calendarOfDayAfter: Calendar = Calendar.getInstance()
        val dayAfter = dayOfTraining + 1
        calendarOfDayAfter.set(yearOfTraining, monthOfTraining, dayAfter, 0, 0)
        val dateOfDayAfter: Date = calendarOfDayAfter.time

        // Parse the date in SimpleDateFormat to compare it with the trainingSessionList
        Log.d(TAG, "parsedDateOfDayAfter = ${sdf.format(dateOfDayAfter)}")
        return sdf.format(dateOfDayAfter)
    }

    //--------------------------------------------------------------------------------------
    // Methods to add the training sessions to the current month of the calendar

    private fun addTrainingSessionsToCalendarView() {
        userId?.let {
            // Get the list of training sessions from Firestore in real time
            trainingSessionViewModel.getListOfTrainingSessions(it)
                ?.addSnapshotListener { value, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    val trainingSessionDateList = arrayListOf<String>()
                    if (value != null && !value.isEmpty) {
                        for (doc in value) {
                            // Add each training session date to the list of dates
                            trainingSessionDateList.add(doc.get(TRAINING_SESSION_DATE_FIELD) as String)
                        }
                        // Add an event to each training session in calendarView
                        addEventInCurrentMonth(trainingSessionDateList)
                    }
                }
        }
    }

    private fun addEventInCurrentMonth(dateList: ArrayList<String>) {
        val events = arrayListOf<EventDay>()
        val currentMonth = calendarFragmentCalendarView?.currentPageDate?.get(Calendar.MONTH)
        Log.d(TAG, "currentMonth = $currentMonth")

        for (date in dateList) {
            // Set calendar with the date from the list
            val parsedDate: Date? = sdf.parse(date)
            Log.d(TAG, "parsedDate = $parsedDate")

            if (parsedDate != null) {
                // Instantiate a calendar
                val calendar = Calendar.getInstance()
                // Get time from parsedDate
                calendar.time = parsedDate
                val dateMonth = calendar.get(Calendar.MONTH)
                if (dateMonth == currentMonth) {
                    // if month from date == current page month, add the event
                    events.add(EventDay(calendar, R.drawable.ic_baseline_fitness_center_24))
                }
            }
        }
        // Log.d(TAG, " events.size = ${events.size}, events = $events")
        calendarFragmentCalendarView?.setEvents(events)
    }

    //--------------------------------------------------------------------------------------
    // Method to add the training sessions to the recyclerView when click on a day

    private fun trainingSessionsFromOnDayClick(clickedDayCalendar: Calendar) {
        val trainingSessionList = arrayListOf<TrainingSession>()

        // Get year, month and day from the eventDay
        val yearOfTraining = clickedDayCalendar.get(Calendar.YEAR)
        val monthOfTraining = clickedDayCalendar.get(Calendar.MONTH)
        val dayOfTraining = clickedDayCalendar.get(Calendar.DATE)

        userId?.let {
            // Get the list of training sessions from Firestore
            trainingSessionViewModel.getListOfTrainingSessions(it)
                // Filter the list with parsed dates
                ?.whereGreaterThanOrEqualTo(
                    TRAINING_SESSION_DATE_FIELD,
                    getDateOfTraining(yearOfTraining, monthOfTraining, dayOfTraining, sdf)
                )
                ?.whereLessThan(
                    TRAINING_SESSION_DATE_FIELD,
                    getDayAfterTraining(yearOfTraining, monthOfTraining, dayOfTraining, sdf)
                )
                ?.get()
                ?.addOnSuccessListener { documents ->
                    for (document in documents) {
                        val trainingSession = document.toObject(TrainingSession::class.java)
                        Log.d(TAG, "trainingSession = $trainingSession")
                        // Add each training session to the list
                        trainingSessionList.add(trainingSession)
                    }
                    // Pass the list to the recyclerView
                    configureRecyclerView(trainingSessionList)
                }
                ?.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    //--------------------------------------------------------------------------------------
    // Interface for callbacks from ItemTrainingSessionAdapter

    override fun onClickTrainingSession(trainingSession: TrainingSession, position: Int) {
        if (trainingSession.trainingSessionDate != null) {
            val dateFromTrainingSession: Date? = sdf.parse(trainingSession.trainingSessionDate!!)
            val nowCalendar = Calendar.getInstance()
            val now: Date = nowCalendar.time

            when {
                dateFromTrainingSession!!.before(now) && trainingSession.trainingSessionCompleted ->
                    callback?.consultATrainingSession(trainingSession)

                dateFromTrainingSession.before(now) -> callback?.cannotUpdatePreviousTrainingSession()

                trainingSession.trainingSessionCompleted -> callback?.cannotUpdateCompletedTrainingSession()

                else -> callback?.updateATrainingSession(trainingSession)
            }
        }
    }

    //----------------------------------------------------------------------------------
    // Interfaces for callback to parent activity and associated methods
    // when click on add button or on an item in the list

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the methods that creating callback after being attached to parent activity
        callbackToParentActivity()
    }

    // Declare our interface and methods that will be implemented by any container activity
    interface CalendarListener {
        fun updateATrainingSession(trainingSession: TrainingSession)
        fun cannotUpdatePreviousTrainingSession()
        fun cannotUpdateCompletedTrainingSession()
        fun consultATrainingSession(trainingSession: TrainingSession)
    }

    // Create callback to parent activity
    private fun callbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callback = activity as CalendarListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement CalendarListener")
        }
    }
}