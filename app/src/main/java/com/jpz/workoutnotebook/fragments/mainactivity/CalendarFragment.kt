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
import com.jpz.workoutnotebook.databinding.FragmentCalendarBinding
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.viewmodels.TrainingSessionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment : Fragment(), ItemTrainingSessionAdapter.Listener {

    companion object {
        private val TAG = CalendarFragment::class.java.simpleName
        private const val TRAINING_SESSION_DATE_FIELD = "trainingSessionDate"
    }

    private var _binding: FragmentCalendarBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Firestore
    private val trainingSessionViewModel: TrainingSessionViewModel by viewModel()

    private var itemTrainingSessionAdapter: ItemTrainingSessionAdapter? = null

    // SimpleDateFormat is used to store (and compare) the dates in the trainingSessionList
    private val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    private var callback: CalendarListener? = null

    private val trainingSessionList = arrayListOf<TrainingSession>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        configureRecyclerView(arrayListOf())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addTrainingSessionsToCalendarView()

        binding.calendarFragmentCalendarView.setOnPreviousPageChangeListener(
            object : OnCalendarPageChangeListener {
                override fun onChange() {
                    addTrainingSessionsToCalendarView()
                    configureRecyclerView(arrayListOf())
                }
            })

        binding.calendarFragmentCalendarView.setOnForwardPageChangeListener(
            object : OnCalendarPageChangeListener {
                override fun onChange() {
                    addTrainingSessionsToCalendarView()
                    configureRecyclerView(arrayListOf())
                }
            })

        binding.calendarFragmentCalendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                // Get the eventDay from the calendarView
                val clickedDayCalendar: Calendar = eventDay.calendar
                trainingSessionsFromOnDayClick(clickedDayCalendar)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerView, Adapter & LayoutManager

    private fun configureRecyclerView(list: ArrayList<TrainingSession>) {
        // Create the adapter by passing the list of training sessions
        itemTrainingSessionAdapter = ItemTrainingSessionAdapter(list, this)
        // Attach the adapter to the recyclerView to populate the training sessions
        binding.calendarFragmentRecyclerView.adapter = itemTrainingSessionAdapter
        // Set layout manager to position the training sessions
        binding.calendarFragmentRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    //--------------------------------------------------------------------------------------
    // Methods to add the training sessions to the current month of the calendar

    private fun addTrainingSessionsToCalendarView() {
        // Get the first day of month at midnight in SDF format
        val beginCurrentCalendarDate: Date? =
            binding.calendarFragmentCalendarView.currentPageDate.time
        Log.d(TAG, "beginCurrentCalendarDate = $beginCurrentCalendarDate")
        var beginMonthSDFFormat = ""
        beginCurrentCalendarDate?.let { beginMonthSDFFormat = sdf.format(it) }

        // Get the first day of next month at midnight in SDF format
        val endCurrentCalendarCalendar: Calendar =
            binding.calendarFragmentCalendarView.currentPageDate
        // Add a month to the actual calendar
        endCurrentCalendarCalendar.add(Calendar.MONTH, 1)
        val endCurrentCalendarDate: Date? = endCurrentCalendarCalendar.time
        Log.d(TAG, "endCurrentCalendarDate = $endCurrentCalendarDate")
        var endMonthSDFFormat = ""
        endCurrentCalendarDate?.let { endMonthSDFFormat = sdf.format(it) }

        // Get the list of training sessions of the current month from Firestore in real time
        trainingSessionViewModel.getListOfTrainingSessions()
            .whereLessThanOrEqualTo(TRAINING_SESSION_DATE_FIELD, endMonthSDFFormat)
            .whereGreaterThanOrEqualTo(TRAINING_SESSION_DATE_FIELD, beginMonthSDFFormat)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (value != null && !value.isEmpty) {
                    trainingSessionList.clear()
                    for (doc in value) {
                        // Add each training session to the list
                        trainingSessionList.add(doc.toObject(TrainingSession::class.java))
                        Log.d(TAG, "trainingSessionList = $trainingSessionList")
                    }
                    // Add an event to each training session in calendarView
                    addEvent(trainingSessionList)
                }
            }
    }

    private fun addEvent(trainingSessionList: ArrayList<TrainingSession>) {
        val events = arrayListOf<EventDay>()
        for (trainingSession in trainingSessionList) {
            trainingSession.trainingSessionDate?.let {
                // Set calendar with the date from the list
                val parsedDate: Date? = sdf.parse(it)
                Log.d(TAG, "parsedDate = $parsedDate")
                // Instantiate a calendar
                val calendar = Calendar.getInstance()
                // Get time from parsedDate
                calendar.time = parsedDate!!
                // Add an icon on the calendar for each day with a trainingSession
                events.add(EventDay(calendar, R.drawable.ic_baseline_fitness_center_24))
            }
        }
        // Log.d(TAG, " events.size = ${events.size}, events = $events")
        binding.calendarFragmentCalendarView.setEvents(events)
    }

    //--------------------------------------------------------------------------------------
    // Method to add the training sessions to the recyclerView when click on a day on the calendar

    private fun trainingSessionsFromOnDayClick(clickedDayCalendar: Calendar) {
        // Get year, month and day from the eventDay
        val yearOfTraining = clickedDayCalendar.get(Calendar.YEAR)
        val monthOfTraining = clickedDayCalendar.get(Calendar.MONTH)
        val dayOfTraining = clickedDayCalendar.get(Calendar.DATE)

        // Instantiate a Calendar to have only the date of Training
        val calendarOfTraining: Calendar = Calendar.getInstance()
        calendarOfTraining.set(yearOfTraining, monthOfTraining, dayOfTraining, 0, 0)
        val dateOfTraining: Date = calendarOfTraining.time

        // Instantiate a Calendar to have only the day after training
        val calendarOfDayAfter: Calendar = Calendar.getInstance()
        val dayAfter = dayOfTraining + 1
        calendarOfDayAfter.set(yearOfTraining, monthOfTraining, dayAfter, 0, 0)
        val dateOfDayAfter: Date = calendarOfDayAfter.time

        // Create a list of training sessions for the eventDay clicked
        val listForEventDay = arrayListOf<TrainingSession>()

        for (trainingSession in trainingSessionList) {
            trainingSession.trainingSessionDate?.let {
                val trainingSessionDate = sdf.parse(it)
                // Filter the trainingSessionList with parsed dates
                if (trainingSessionDate!! >= dateOfTraining && trainingSessionDate < dateOfDayAfter)
                // Add each training session for this eventDay to the list
                    listForEventDay.add(trainingSession)
            }
        }
        // Pass the list to the recyclerView
        configureRecyclerView(listForEventDay)
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