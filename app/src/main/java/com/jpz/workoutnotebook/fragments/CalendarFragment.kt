package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.api.UserAuth
import com.jpz.workoutnotebook.viewmodels.TrainingSessionViewModel
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarFragment : Fragment() {

    companion object {
        private val TAG = CalendarFragment::class.java.simpleName
        private const val TRAINING_SESSION_DATE_FIELD = "trainingSessionDate"
    }

    private var userId: String? = null

    // List of EventDay in calendarView
    private val events = mutableListOf<EventDay>()

    // List of training session dates from Firestore
    private val trainingSessionDateList: ArrayList<String> = ArrayList()

    // Firebase Auth, Firestore
    private val userAuth: UserAuth by inject()
    private val trainingSessionViewModel: TrainingSessionViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = userAuth.getCurrentUser()?.uid

        addTrainingSessionsToCalendarView()

        calendarFragmentCalendarView.setOnPreviousPageChangeListener(
            object : OnCalendarPageChangeListener {
                override fun onChange() {
                    addTrainingSessionsToCalendarView()
                }
            })

        calendarFragmentCalendarView.setOnForwardPageChangeListener(
            object : OnCalendarPageChangeListener {
                override fun onChange() {
                    addTrainingSessionsToCalendarView()

                }
            })

        calendarFragmentCalendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
/*
                val clickedDayCalendar: Calendar = eventDay.calendar
                calendarFragmentCalendarView.setDate(clickedDayCalendar)

                val clickedDayTime = clickedDayCalendar.time

                Toast.makeText(activity, "clickedDayTime = $clickedDayTime", Toast.LENGTH_SHORT)
                    .show()*/
            }
        })
    }

    //--------------------------------------------------------------------------------------

    private fun addTrainingSessionsToCalendarView() {
        // Clear the list before use it
        trainingSessionDateList.clear()

        userId?.let {
            // Get the list of training sessions from Firestore
            trainingSessionViewModel.getListOfTrainingSessions(it)?.get()
                ?.addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Add each training session to the list
                        trainingSessionDateList.add(document.get(TRAINING_SESSION_DATE_FIELD) as String)
                    }
                    Log.d(TAG, "dateList = $trainingSessionDateList")
                    // Add an event to each training session in calendarView
                    addEventInCurrentMonth(trainingSessionDateList)
                }
                ?.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    private fun addEventInCurrentMonth(dateList: ArrayList<String>) {
        // Clear the list before use it
        events.clear()

        val formatter = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        val currentMonth = calendarFragmentCalendarView.currentPageDate.get(Calendar.MONTH)
        Log.d(TAG, "currentMonth = $currentMonth")

        for (date in dateList) {
            // Set calendar with the date from the list
            val parsedDate: Date? = formatter.parse(date)
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
        Log.d(TAG, " events.size = ${events.size}, events = $events")
        calendarFragmentCalendarView.setEvents(events)
    }
}