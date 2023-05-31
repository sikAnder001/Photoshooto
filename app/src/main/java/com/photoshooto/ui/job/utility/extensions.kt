package com.photoshooto.ui.job.utility

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.photoshooto.R
import com.photoshooto.base.BaseActivity
import com.photoshooto.domain.model.AddressRQ
import com.photoshooto.util.DOMAIN
import com.pscalendarevent.pscalendarevent.vo.DateData
import es.dmoral.toasty.Toasty
import org.joda.time.DateTime
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToInt

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun Double.singleDecimalPlaces(): String {
    val df = DecimalFormat("#.#")
    return df.format(this)
}

fun JSONArray.jsonArrayToArrayList(): ArrayList<String> {
    var list = arrayListOf<String>()
    for (i in 0 until this.length()) {
        list.add(this.get(i).toString())
    }
    return list
}


fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun JSONArray.has(value: JSONObject): Boolean {
    return (this.toString().contains(value.toString()))
}

fun JSONArray.has(value: String): Boolean {
    return (this.toString().contains(value.toString()))
}

fun Int.dpToInt(): Int {
    return (toFloat() * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
}

fun String.isNumeric() = this.all { it in '0'..'9' }

fun ViewPager2.removeOverScroll() {
    (getChildAt(0) as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER
}

fun hideSpinnerDropDown(spinner: Spinner) {
    try {
        val method = Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
        method.isAccessible = true
        method.invoke(spinner)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun loadImage(imageView: ImageView, url: String) {
    val tmpURL = if (!url.startsWith("http")) {
        DOMAIN + url
    } else {
        url
    }
    Glide.with(imageView.context).load(tmpURL)
        .placeholder(R.drawable.default_thumbnail)
        .error(R.drawable.default_thumbnail)
        .into(imageView)
}

fun loadImageUser(imageView: ImageView, url: String) {
    val tmpURL = if (!url.startsWith("http")) {
        DOMAIN + url
    } else {
        url
    }
    Glide.with(imageView.context).load(tmpURL)
        .placeholder(R.drawable.icn_placeholder_user)
        .error(R.drawable.icn_placeholder_user)
        .into(imageView)
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

fun String.snakeToTitleCase(): String {
    val snakeRegex = "_[a-zA-Z]".toRegex()
    val data = snakeRegex.replace(this) {
        it.value.replace("_", " ")
            .uppercase()
    }
    val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
    return camelRegex.replace(data) {
        " ${it.value}"
    }.lowercase().capitalizeWords()
}

fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { it ->
        it.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }
}

fun String.getHalfPlanPrice(): String {
    return if (equals("teams", true)) {
        "3599"
    } else if (equals("studio", true)) {
        "2499"
    } else {
        "1799"
    }
}


fun String.calculateHYearlyWithDiscount(disc: String): String {
    val monthAmount = this.toDouble()
    val discountPercentage = (disc.toDouble()) / 100
    val yearAmount = (monthAmount * 12)

    val finalAmount = yearAmount - (yearAmount * discountPercentage)
    return "${finalAmount.roundToInt()}"
}

fun Int.twoDigitNUmber(): String {
    if (this < 10) {
        return "0${this}"
    }
    return this.toString()
}

fun Context.toastSuccess(msg: String) {
    Toasty.success(this, msg).show()
}

fun Context.toastError(msg: String) {
    Toasty.error(this, msg).show()
}

fun Context.showAppDialog(
    title: String? = this.resources.getString(R.string.app_name),
    msg: String,
    positiveText: String? = this.resources.getString(R.string.ok),
    listener: DialogInterface.OnClickListener? = null,
    negativeText: String? = this.resources.getString(R.string.cancel),
    negativeListener: DialogInterface.OnClickListener? = null,
    icon: Int? = null
) {
    if (BaseActivity.dialogShowing) {
        return
    }
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(msg)
    builder.setCancelable(false)
    builder.setPositiveButton(positiveText) { dialog, which ->
        BaseActivity.dialogShowing = false
        listener?.onClick(dialog, which)
    }
    if (negativeListener != null) {
        builder.setNegativeButton(negativeText) { dialog, which ->
            BaseActivity.dialogShowing = false
            negativeListener.onClick(dialog, which)
        }
    }
    if (icon != null) {
        builder.setIcon(icon)
    }
    builder.create().show()
    BaseActivity.dialogShowing = true
}

fun Context.isValidEmail(text: String): Boolean {
    return !TextUtils.isEmpty(text)
            && Patterns.EMAIL_ADDRESS.matcher(text).matches()
            && (text.length <= this.resources.getInteger(R.integer.max_length_email))
}

fun Activity.openPermissionSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", this.packageName, null)
    intent.data = uri
    startActivityForResult(intent, AppConstant.INTENT_SETTINGS)
}

fun String.getCalendarDate(): DateData {
    val strArray = this.split("-")
    var dateArray = mutableListOf<Int>()

    val date = if (strArray.size == 3) {
        DateData(
            strArray[0].toInt(),
            strArray[1].toInt(),
            strArray[2].toInt()
        )
    } else {
        val cal = DateTime.now()
        DateData(
            cal.year,
            cal.monthOfYear,
            cal.dayOfMonth
        )
    }
    return date
}

fun Any.parseAddress(): String {
// old data contains string new data has JSON Object
    return when (this) {
        is String -> {
            this.toString()
        }
        is AddressRQ -> {
            this.toString()
        }
        else -> {
            var jsonElement = Gson().toJsonTree(this)
            val resultData = Gson().fromJson(jsonElement, AddressRQ::class.java)
            resultData.location
        }
    }
}

fun Any.getLatitudeAddress(): String {
// old data contains string new data has JSON Object
    return when (this) {
        is String -> {
            "0"
        }
        else -> {
            var jsonElement = Gson().toJsonTree(this)
            val resultData = Gson().fromJson(jsonElement, AddressRQ::class.java)
            resultData.latitude.toString()
        }
    }
}

fun Any.getLongitudeAddress(): String {
// old data contains string new data has JSON Object
    return when (this) {
        is String -> {
            "0"
        }
        else -> {
            val jsonElement = Gson().toJsonTree(this)
            val resultData = Gson().fromJson(jsonElement, AddressRQ::class.java)
            resultData.longitude.toString()
        }
    }
}

fun Context.hideKeyboardFrom(view: View) {
    val imm: InputMethodManager =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showKeyboard() {
    val imm: InputMethodManager =
        this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun RadioGroup.getSelectedValue(): String {
    return if (checkedRadioButtonId == -1) {
        ""
    } else {
        val radioButton = (findViewById(checkedRadioButtonId)) as RadioButton
        val radioId = indexOfChild(radioButton)
        val btn = getChildAt(radioId) as RadioButton
        btn.text as String
    }
}

fun String.callFromDialer(mContext: Context) {
    try {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$this")
        mContext.startActivity(callIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        mContext.toastError(mContext.getString(R.string.something_went_wrong))
    }
}

fun List<String>.listToBreakables(): String {
    val tmpData = StringBuilder()
    this.forEachIndexed { index, str ->
        if (index < this.size - 1)
            tmpData.append(str).append(System.lineSeparator())
        else
            tmpData.append(str)
    }

    return tmpData.toString()
}

fun List<Int>.to12HrFormat(): String {

    var newTime = StringBuilder(
        "${String.format("%02d", if (this[0] != 12) (this[0] % 12) else 12)}:${
            String.format("%02d", this[1])
        }"
    )

    if (this[0] >= 12) {
        newTime.append(" PM")
    } else {
        newTime.append(" AM")
    }
    return newTime.toString()
}