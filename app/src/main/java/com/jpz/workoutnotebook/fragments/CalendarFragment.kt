package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.utils.DatePickerFragment
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.util.*


class CalendarFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use the Kotlin extension in the fragment-ktx artifact for setFragmentResultListener
        // Listen the result from DatePickerFragment
        childFragmentManager.setFragmentResultListener("requestKeyDate", this) { key, bundle ->
            val result = bundle.getString("bundleKeyDate")

            Toast.makeText(activity, "result = $result", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarFragmentCalendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {

                val clickedDayCalendar: Calendar = eventDay.calendar
                calendarFragmentCalendarView.setDate(clickedDayCalendar)

                val clickedDayTime = clickedDayCalendar.time

                Toast.makeText(activity, "clickedDayTime = $clickedDayTime", Toast.LENGTH_SHORT)
                    .show()

                addEvent(clickedDayCalendar)
            }
        })

        /*calendarFragmentFABAdd.setOnClickListener {
            val datePicker = DatePickerFragment()
            datePicker.show(childFragmentManager, DatePickerFragment()::class.java.simpleName)
        }*/
    }

    //--------------------------------------------------------------------------------------

    private fun addEvent(calendar: Calendar) {
        val events: MutableList<EventDay> = ArrayList()
        events.add(EventDay(calendar, R.drawable.ic_baseline_fitness_center_24))
        calendarFragmentCalendarView.setEvents(events)
    }

}