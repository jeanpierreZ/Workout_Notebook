package com.jpz.workoutnotebook.utils

import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseMethod
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jpz.workoutnotebook.R

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

        @JvmStatic
        @BindingAdapter("profileImage")
        fun loadImage(view: ImageView, profileImage: String?) {
            Glide.with(view.context)
                .load(profileImage)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_person_add))
                .error(R.drawable.ic_person_add)
                .circleCrop()
                .into(view)
        }
    }
}