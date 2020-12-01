package com.jpz.workoutnotebook.utils

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.util.*

class TimePickerFragment(private var hourOfDay: Int?, private var minute: Int?) : DialogFragment(),
    TimePickerDialog.OnTimeSetListener {

    companion object {
        const val REQUEST_KEY_TIME = "REQUEST_KEY_TIME"
        const val BUNDLE_KEY_HOUR = "BUNDLE_KEY_HOUR"
        const val BUNDLE_KEY_MINUTE = "BUNDLE_KEY_MINUTE"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (hourOfDay == null && minute == null) {
            // Use the current time as the default values for the picker
            val c = Calendar.getInstance()
            hourOfDay = c.get(Calendar.HOUR_OF_DAY)
            minute = c.get(Calendar.MINUTE)
        }

        // Set TimePickerDialog with time from the calendar
        val dialog = activity?.let {
            TimePickerDialog(
                it, this, hourOfDay!!, minute!!, android.text.format.DateFormat.is24HourFormat(it)
            )
        }

        return dialog!!
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Send the result to EditCalendarFragment
        setFragmentResult(
            REQUEST_KEY_TIME, bundleOf(BUNDLE_KEY_HOUR to hourOfDay, BUNDLE_KEY_MINUTE to minute)
        )
    }
}