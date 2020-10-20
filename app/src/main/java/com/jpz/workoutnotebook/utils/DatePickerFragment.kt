package com.jpz.workoutnotebook.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        const val REQUEST_KEY_DATE = "REQUEST_KEY_DATE"
        const val BUNDLE_KEY_YEAR = "BUNDLE_KEY_YEAR"
        const val BUNDLE_KEY_MONTH = "BUNDLE_KEY_MONTH"
        const val BUNDLE_KEY_DAY = "BUNDLE_KEY_DAY"
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
        // Send the result to EditCalendarFragment
        setFragmentResult(
            REQUEST_KEY_DATE,
            bundleOf(BUNDLE_KEY_YEAR to year, BUNDLE_KEY_MONTH to month, BUNDLE_KEY_DAY to day)
        )
    }
}