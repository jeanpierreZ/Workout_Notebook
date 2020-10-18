package com.jpz.workoutnotebook.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.text.DateFormat
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

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
        // Set the date chosen
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        Log.d("DATE", "year = $year, month = $month, day = $day")

        val chosenDate: String = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)

        // Send the result to CalendarFragment
        setFragmentResult("requestKeyDate", bundleOf("bundleKeyDate" to chosenDate))
    }
}