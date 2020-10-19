package com.jpz.workoutnotebook.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        private val TAG = DatePickerFragment::class.java.simpleName
        const val BUNDLE_KEY_DATE = "BUNDLE_KEY_DATE"
        const val REQUEST_KEY_DATE = "REQUEST_KEY_DATE"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Set DatePickerDialog with date from the calendar
        val dialog = activity?.let { DatePickerDialog(it, this, year, month, day) }

        // Configure the calendar to not choose a past date
        dialog?.datePicker?.minDate = System.currentTimeMillis() - 1000

        return dialog!!
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        Log.d(TAG, "onDateSet = year = $year, month = $month, day = $day")

        // Set date format
        val dateFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        // Set the date chosen
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        // The date chosen by the user
        val dateChosen: Date = calendar.time
        // The date formatted
        val dateFormatted = sdf.format(dateChosen)

        // Send the result to EditCalendarFragment
        setFragmentResult(REQUEST_KEY_DATE, bundleOf(BUNDLE_KEY_DATE to dateFormatted))
    }
}