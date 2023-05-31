package com.photoshooto.ui.job.utility

import com.photoshooto.ui.job.utility.DateTimeHelper.INPUTFORMAT
import com.photoshooto.ui.job.utility.DateTimeHelper.OUTPUTFORMAT
import java.text.SimpleDateFormat
import java.util.*


object DateTimeHelper {
    const val YYYYMMDD = "yyyy-MM-dd"
    const val DDMMYYYY = "dd-MM-yyyy"
    const val HHMMA = "hh:mm a"
    const val DDMMMYYYYHHMMA = "dd/MM/yy hh:mm a"
    const val MONTH_DATE_TIME_AMPM = "MMMM dd'th' yyyy, hh:mm a"
    const val INPUTFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val OUTPUTFORMAT = "dd MMM 'at' hh:mm a"

}

fun String.getFormattedDatetime(
    inputFormat: String = INPUTFORMAT,
    outputFormat: String = OUTPUTFORMAT,
    withZoneCalculation: Boolean = true
): String {
    var outputString = this
    try {
        val informat = SimpleDateFormat(inputFormat)
        val outFormat = SimpleDateFormat(outputFormat)

        if (withZoneCalculation) {
            informat.timeZone = TimeZone.getTimeZone("UTC")
            outFormat.timeZone = TimeZone.getDefault()
        }
        outputString = outFormat.format(informat.parse(this))
    } catch (e: Exception) {
        e.printStackTrace()
        outputString = this
    }
    return outputString
}

fun String.getDateTimeDiff(
    inputFormat: String = INPUTFORMAT,
    withZoneCalculation: Boolean = false,
    endDate: String = ""
): String {
    var outputString = this
    try {
        val informat = SimpleDateFormat(inputFormat)
        if (withZoneCalculation) {
            informat.timeZone = TimeZone.getTimeZone("UTC")
        }

        val date1 = informat.parse(this)
        val date2 = informat.parse(endDate)

        val currentDate = Calendar.getInstance().time

        if (date2 > date1) {
            val diff = date2.time - date1.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            return if (days > 0 && (hours % 24) > 0) {
                "${days}d:${(hours % 24)}h"
            } else if (days > 0) {
                "${days}d"
            } else {
                "${(hours % 24)}h"
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        outputString = this
    }
    return outputString
}

fun String.getDateTimeDiffSingle(
    inputFormat: String = INPUTFORMAT,
    withZoneCalculation: Boolean = true
): String {
    var outputString = this
    try {
        val informat = SimpleDateFormat(inputFormat)
        if (withZoneCalculation) {
            informat.timeZone = TimeZone.getTimeZone("UTC")
        }
        val dynamicDate = informat.parse(this)
        val currentDate = Calendar.getInstance().time

        if (currentDate > dynamicDate) {

            val diff = currentDate.time - dynamicDate.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            if (days > 0) {
                return "${days} d ago"
            } else if (hours > 0) {
                return "${hours} h ago"
            } else if (minutes > 0) {
                return "${minutes} m ago"
            } else if (seconds > 0) {
                return "${seconds} s ago"
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        outputString = this
    }
    return outputString
}

fun String.checkIfDateGone(
    inputFormat: String = INPUTFORMAT,
    withZoneCalculation: Boolean = true
): Boolean {
    try {
        val informat = SimpleDateFormat(inputFormat)
        if (withZoneCalculation) {
            informat.timeZone = TimeZone.getTimeZone("UTC")
        }
        val dynamicDate = informat.parse(this)
        val currentDate = Calendar.getInstance().time

        return currentDate > dynamicDate
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

fun String.getDateFromTime(inputFormat: String = INPUTFORMAT): Date? {
    return try {
        val informat = SimpleDateFormat(inputFormat)
        informat.timeZone = TimeZone.getTimeZone("UTC")
        informat.parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/*
fun String.getTimeForComment(): String {
    return when (getFormattedDatetime(outputFormat = "dd MMMM yyyy")) {
        TimeHelper.today -> "Today, " + getFormattedDatetime(
            outputFormat = "hh:mm a"
        )
        TimeHelper.yesterday -> "Yesterday, " + getFormattedDatetime(
            outputFormat = "hh:mm a"
        )
        else -> getFormattedDatetime(
            outputFormat = "dd MMMM yyyy, hh:mm a"
        )
    }
*/

