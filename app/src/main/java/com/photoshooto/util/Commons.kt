package com.photoshooto.util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


fun Any?.toJson(): String {
    return Gson().toJson(this)
}

fun Any?.tag(): String {
    return this?.javaClass?.simpleName ?: ""
}

fun createPartFromString(param: String): RequestBody {
    return param.toRequestBody("multipart/form-data".toMediaTypeOrNull())
}

fun urlAddingForPicture(url: String): String {
    var str = ""
    str = if (url.contains("https://photoshooto.com")) {
        url
    } else {
        "https://photoshooto.com/$url"
    }
    return str
}

fun isValidEmail(target: CharSequence?): Boolean {
    return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
}

fun isValidPasswordFormat(password: String): Boolean {
    val passwordREGEX = Pattern.compile(
        "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{8,}" +               //at least 8 characters
                "$"
    )
    return passwordREGEX.matcher(password).matches()
}

fun isValidPinCode(pinCode: String): Boolean {
    val regex = "^[1-9]{1}[0-9]{2}\\s{0,1}[0-9]{3}$"

    // Compile the ReGex

    // Compile the ReGex
    val p = Pattern.compile(regex)

    // If the pin code is empty
    // return false

    // If the pin code is empty
    // return false
    if (pinCode == null) {
        return false
    }
    val m = p.matcher(pinCode)

    // Return if the pin code
    // matched the ReGex

    // Return if the pin code
    // matched the ReGex
    return m.matches()
}

fun String.isValidMobileNumber() =
    Pattern.compile(
        "^[6-9]\\d{9}\$"
    ).matcher(this).matches()

fun isValid(email: String?): Boolean {
    val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$"
    val pat = Pattern.compile(emailRegex)
    return if (email == null) false else pat.matcher(email).matches()
}


// Function to validate
// GST (Goods and Services Tax) number.
fun String.isValidGSTNo(str: String?): Boolean {
    val regex = ("^[0-9]{2}[A-Z]{5}[0-9]{4}"
            + "[A-Z]{1}[1-9A-Z]{1}"
            + "Z[0-9A-Z]{1}$")
    val p = Pattern.compile(regex)
    if (str == null) {
        return false
    }
    val m: Matcher = p.matcher(str)
    return m.matches()
}

fun Any?.logInfo(message: String?) {
    Log.i(tag(), message ?: "")
}

fun Any?.logInfo(message: String?, data: Any?) {
    Log.i(tag(), message ?: ("" + "\t-->\t" + this.toJson()))
}

fun Any?.onToast(string: String, context: Context) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

}

fun Any?.logWarning(message: String?) {
    Log.w(tag(), message ?: "")
}

fun Any?.logWarning(throwable: Throwable?) {
    Log.w(tag(), throwable?.message, throwable)
}

fun Any?.logWarning(message: String?, throwable: Throwable?) {
    Log.w(tag(), message ?: ("" + "\t-->\t" + this.toJson()), throwable)
}

fun Any?.logError(message: String?) {
    Log.e(tag(), message ?: "")
}

fun Any?.logError(throwable: Throwable?) {
    Log.e(tag(), throwable?.message, throwable)
}

fun Any?.logError(message: String?, throwable: Throwable?) {
    Log.e(tag(), message ?: ("" + "\t-->\t" + this.toJson()), throwable)
}

fun Fragment.navigate(directions: NavDirections) {
    try {
        NavHostFragment.findNavController(requireParentFragment()).navigate(directions)
    } catch (e: java.lang.Exception) {
        logError(e)
    }
}

fun Fragment.popBackStack() {
    try {
        NavHostFragment.findNavController(requireParentFragment()).popBackStack()
    } catch (e: java.lang.Exception) {
        logError(e)
    }
}

// fun AppCompatActivity.navigate(directions: NavDirections) {
//    try {
//        Navigation.findNavController(this, R.id.nav_host).navigate(directions)
//    } catch (e: java.lang.Exception) {
//        logError(e)
//    }
// }

fun View.setSingleClickListener(onSingleClick: (View) -> Unit) {
    val singleClickListener = SingleClickListener {
        onSingleClick(it)
    }
    setOnClickListener(singleClickListener)
}

//fun View.clickObservable(): Observable<Unit>? {
//    return clicks()
//        .throttleFirst(1, TimeUnit.SECONDS)
//        .observeOn(AndroidSchedulers.mainThread())
//}

fun EditText.showKeyboard() {
    postDelayed(
        {
            if (requestFocusFromTouch()) {
                (this.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                setSelection(text.toString().length)
            }
        },
        200
    )
}

fun isValidPassword(password: String?): Boolean {
    val regex = ("^(?=.*[0-9])"
            + "(?=.*[a-z])(?=.*[A-Z])"
            + "(?=.*[@#$%^&+=])"
            + "(?=\\S+$).{8,20}$")

    val p: Pattern = Pattern.compile(regex)
    if (password == null) {
        return false
    }
    val m: Matcher = p.matcher(password)
    return m.matches()
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    if (currentFocus == null) View(this) else currentFocus?.let { hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun EditText.transformIntoDatePicker(
    context: Context,
    format: String,
    maxDate: Date? = null,
    from: String? = null
) {
    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val myCalendar = Calendar.getInstance()
    val datePickerOnDataSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat(format, Locale.UK)
            setText(sdf.format(myCalendar.time))
        }

    setOnClickListener {
        DatePickerDialog(
            context, datePickerOnDataSetListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).run {
            maxDate?.time?.also { datePicker.maxDate = it }
            if (from.equals("ServiceRequest"))
                datePicker.minDate = System.currentTimeMillis()
            show()
        }
    }
}

fun checkDate(date: String, date1: String): String {
    var result = ""
    val sdformat = SimpleDateFormat("yyyy-MM-dd")
    val d1 = sdformat.parse(date)
    val d2 = sdformat.parse(date1)
    if (d1.compareTo(d2) > 0) {
        result = "Date 1 occurs after Date 2"
    } else if (d1.compareTo(d2) < 0) {
        result = "Date 1 occurs before Date 2"
    } else if (d1.compareTo(d2) === 0) {
        result = "Both dates are equal"
    }
    return result
}

fun isPermissionGranted(context: Context?): Boolean {
    return if (Build.VERSION.SDK_INT >= 23) {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!, arrayOf(
                    "android.permission.CAMERA",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                ),
                200
            )
            false
        }
    } else {
        true
    }
}

fun showAlertDialog(message: String, context: Context) {
    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
    alertDialog.setTitle("Disclaimer")
    alertDialog.setMessage("\n" + message)
    alertDialog.setPositiveButton("Ok") { dialog, which ->
        dialog.cancel()
    }
    val alert: AlertDialog = alertDialog.create()
    alert.setCanceledOnTouchOutside(false)
    alert.show()
}

fun showAlertDialogToActivity(message: String, context: Context) {
    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
//    alertDialog.setTitle(message)
    alertDialog.setMessage("\n" + message)
    alertDialog.setPositiveButton("Ok") { dialog, which ->
//        context.startActivity(
//            Intent(context, DashboardActivity::class.java).setFlags(
//                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//            )
//        )
        dialog.cancel()
    }
    val alert: AlertDialog = alertDialog.create()
    alert.setCanceledOnTouchOutside(false)
    alert.show()
}

fun Context.isInternetAvailable(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkConnected(connectivityManager)
        } else {
            checkConnectedLegacy(connectivityManager)
        }
    }
    return false
}

@RequiresApi(Build.VERSION_CODES.M)
private fun checkConnected(connectivityManager: ConnectivityManager): Boolean {
    val activeNetwork = connectivityManager.activeNetwork
    activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
    capabilities ?: return false
    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(
        NetworkCapabilities.TRANSPORT_WIFI
    )
}

private fun checkConnectedLegacy(connectivityManager: ConnectivityManager): Boolean {
    val networkInfo = connectivityManager.activeNetworkInfo
    networkInfo ?: return false
    return networkInfo.isConnected && (networkInfo.type == ConnectivityManager.TYPE_WIFI || networkInfo.type == ConnectivityManager.TYPE_MOBILE)
}

fun recordScreenView(context: Context, screenClass: String, screenName: String) {
    val bundle = Bundle().apply {
        putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
    }
    // [START set_current_screen]
    FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
}


fun Context.loadImage(view: AppCompatImageView, url: String?) {
    Glide.with(this)
        .load(url)
        .load(url).into(view)
}


fun Context.createAndSaveFileFromBase64Url(url: String, fileCreated: (String) -> Unit) {
    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val filename = "photoshooto_" + System.currentTimeMillis().toString() + ".png"
    val file = File(path, filename)
    try {
        if (!path.exists()) path.mkdirs()
        if (!file.exists()) file.createNewFile()
        val base64EncodedString = url.substring(url.indexOf(",") + 1)
        val decodedBytes = Base64.decode(base64EncodedString, Base64.DEFAULT)
        val os: OutputStream = FileOutputStream(file)
        os.write(decodedBytes)
        os.close()

        //Tell the media scanner about the new file so that it is immediately available to the user.
        MediaScannerConnection.scanFile(
            this, arrayOf(file.toString()), null
        ) { pathLatest, uri ->
            Log.i("ExternalStorage", "Scanned $pathLatest:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }


    } catch (e: IOException) {
        Log.w("ExternalStorage", "Error writing $file", e)

    }
    return fileCreated.invoke(file.toString())
}

fun isLessThan24HourAgo(create_at: String, update_at: String): Boolean {
    val MILLIS_PER_DAY = 86400000    //24 * 60 * 60 * 1000L;
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val date1 = sdf.parse(create_at)
    val date2 = sdf.parse(update_at)

    val moreThanDay: Boolean = Math.abs(date1.time - date2.time) > MILLIS_PER_DAY

    return moreThanDay
}

fun getDate(value: String): String {

    val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = utcFormat.parse(value)
    val pstFormat = SimpleDateFormat("MMM dd")
    pstFormat.timeZone = TimeZone.getTimeZone("UTC")
//			System.out.println(pstFormat.format(date))
    return pstFormat.format(date)
}

fun getTime(value: String): String {

    val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = utcFormat.parse(value)
    val pstFormat = SimpleDateFormat("MMM dd")
    pstFormat.timeZone = TimeZone.getTimeZone("UTC")
//			System.out.println(pstFormat.format(date))
    return pstFormat.format(date)
}