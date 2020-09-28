package com.jpz.workoutnotebook.utils

import android.text.TextUtils
import androidx.databinding.InverseMethod

class DataBindingConverters {

    companion object {

        @InverseMethod("convertIntegerToString")
        @JvmStatic
        fun convertStringToInteger(value: String): Int? {
            if (TextUtils.isEmpty(value) || !TextUtils.isDigitsOnly(value)) {
                return null
            }
            return value.toIntOrNull()
        }

        @JvmStatic
        fun convertIntegerToString(value: Int?): String {
            return value?.toString() ?: ""
        }

        //----------------------------------------------------------------------------------

        @InverseMethod("convertDoubleToString")
        @JvmStatic
        fun convertStringToDouble(value: String): Double? {
            if (TextUtils.isEmpty(value)) {
                return null
            }
            return value.toDoubleOrNull()
        }

        @JvmStatic
        fun convertDoubleToString(value: Double?): String {
            return value?.toString() ?: ""
        }
    }
}