package com.photoshooto.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.photoshooto.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // Internet connectivity check in Android Q
    val networks = connectivityManager.allNetworks
    var hasInternet = false
    if (networks.isNotEmpty()) {
        for (network in networks) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) hasInternet =
                true
        }
    }
    return hasInternet
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun Double.amount(context: Context): String {
    val decimalFormatter = DecimalFormat("#,##,##,##0.00")
    return "${context.getString(R.string.label_rupee)} ${decimalFormatter.format(this)}"
}

@SuppressLint("SimpleDateFormat")
fun String?.convertDate(output: String? = null): String? {
    if (this.isNullOrEmpty()) {
        return null
    }
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val outputDateFormat = SimpleDateFormat(output ?: "yyyy-MM-dd HH:mm:ss")
        val inDate = dateFormat.parse(this)
        val outDate = outputDateFormat.format(inDate)
        outDate
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String?.calculateDueTime(): String? {
    if (this.isNullOrEmpty()) {
        return "N/A"
    }
    val requestDate = this.convertToDate()
    if (requestDate != null) {
        val currentDate = Date()
        val diff = currentDate.time - requestDate.time
        val min = TimeUnit.MILLISECONDS.toMinutes(diff).toDouble()
        if (min < 60) {
            return "${min.toInt()}Min"
        }
        val hours = min / 60
        if (hours < 24) {
            val remainMin = (hours - hours.toInt()) * 60
            if (remainMin > 0) {
                return "${hours.toInt()}H ${remainMin.toInt()}Min"
            }
            return "${hours.toInt()}H"
        }
        val days = hours / 24
        val remainHrs = (days - days.toInt()) * 24
        if (remainHrs > 0) {
            return "${days.toInt()}D ${remainHrs.toInt()}H"
        }
        return "${days.toInt()}D"
    } else {
        return null
    }
}

@SuppressLint("SimpleDateFormat")
fun String?.convertToDate(input: String? = null): Date? {
    if (this.isNullOrEmpty()) {
        return null
    }
    return try {
        val dateFormat = SimpleDateFormat(input ?: "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val inDate = dateFormat.parse(this)
        inDate
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Display the simple Toast message with the [Toast.LENGTH_SHORT] duration.
 *
 * @param message the message text.
 */
fun Context.showToast(message: CharSequence): Toast = Toast
    .makeText(this, message, Toast.LENGTH_SHORT)
    .apply {
        show()
    }

fun Context.showToast(message: Int): Toast = showToast(resources.getString(message))

fun View.snackbar(message: CharSequence) = Snackbar
    .make(this, message, Snackbar.LENGTH_SHORT)
    .apply { show() }


/**
 * for check string
 */
fun checkStringValue(text: String?): Boolean {
    return !(text == null || text.trim { it <= ' ' } == "null" || text.trim { it <= ' ' }.isEmpty())
}

/**
 * for return string with suggested value
 */
fun checkStringReturnValue(text: String?, stReturnString: String = ""): String {
    return if (text == null || text.trim { it <= ' ' } == "null" || text.trim { it <= ' ' }
            .isEmpty()) {
        stReturnString
    } else {
        text
    }
}

fun checkIntReturnValue(text: Int?, stReturnString: Int = 0): Int {
    return if (text == null || "$text".trim { it <= ' ' } == "null" || "$text".trim { it <= ' ' }
            .isEmpty()) {
        stReturnString
    } else {
        text
    }
}

/**
 * for return string with suggested value
 */
fun checkStringReturnValueInDouble(text: String?): Double {
    return checkStringReturnValue(text, "0.0").toDouble()
}

fun checkStringReturnValueInFloat(text: String?): Float {
    return checkStringReturnValue(text, "0").toFloat()
}

fun checkStringReturnValueInInt(text: String?): Int {
    return checkStringReturnValue(text, "0").toDouble().toInt()
}


/**
 * Get String from strings.xml file using resource
 */
fun Context.getStringValue(resourceId: Int): String {
    return resources.getString(resourceId)
}

fun Context.getStringValue(resourceId: Int, attachValue: String): String {
    return resources.getString(resourceId, attachValue)
}

/**
 * Get Drawables from Drawable's Folder using resource
 */
fun Context.getDrawableValue(resourceId: Int): Drawable {
    return ContextCompat.getDrawable(this, resourceId)!!
}

/**
 * Get Drawables from Drawable's Folder using resource
 */
fun Context.getColorValue(resourceId: Int): Int {
    return ContextCompat.getColor(this, resourceId)
}


/**
 * Get Drawables from Drawable's Folder using resource
 */
fun AppCompatEditText.getTextValue(): String {
    return this.editableText.toString().trim()
}
