package com.photoshooto.util

import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.google.gson.Gson
import com.photoshooto.R
import com.photoshooto.domain.model.FileUpload
import com.photoshooto.ui.splash.SplashActivity
import java.io.*
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


val DOCUMENTS_DIR = "documents"
val IMAGE = "image"
val AUDIO = "audio"
val VIDEO = "video"
fun NavController.safeNavigate(direction: Int, bundle: Bundle?) {
    Log.d("clickTag", "Click happened")
    currentDestination?.getAction(direction)?.run {
        Log.d("clickTag", "Click Propagated")
        if (bundle != null) {
            navigate(direction, bundle)
        } else {
            navigate(direction)
        }
    }
}

fun NavController.safeNavigate(direction: Int) {
    Log.d("clickTag", "Click happened")
    currentDestination?.getAction(direction)?.run {
        Log.d("clickTag", "Click Propagated")
        navigate(direction)
    }
}

fun getTermsNConditionBundle(): Bundle {
    val path = "terms_and_conditions.pdf"
    val bundle = Bundle()
    bundle.putString("data", path)
    return bundle
}

fun getPrivacyPolicyBundle(): Bundle {
    val path = "privacy_policy.pdf"
    val bundle = Bundle()
    bundle.putString("data", path)
    return bundle
}

fun clearApplicationData(context: Context) {
    val cache = context.cacheDir
    val parent = cache.parent
    if (isNotEmpty(parent)) {
        val appDir = parent?.let { File(it) }
        if (appDir != null) {
            if (appDir.exists()) {
                val children = appDir.list()
                if (children != null) {
                    for (s in children) {
                        if ("lib" != s) {
                            deleteDir(File(appDir, s))
                        }
                    }
                }
            }
        }
    }
    clearPreferences(context)
}

private fun deleteDir(dir: File?): Boolean {
    if (dir != null && dir.isDirectory) {
        val children = dir.list()
        if (children != null) {
            for (child in children) {
                val success = deleteDir(File(dir, child))
                if (!success) {
                    return false
                }
            }
        }
    }
    return dir!!.delete()
}

fun createTransactionID(): String {
    return UUID.randomUUID().toString().replace("-".toRegex(), "")
        .lowercase(Locale.getDefault())
}

/**
 * This method returns true if the collection has at least one item.
 *
 * @param collection input collection
 * @return true | false
 */
fun isNotEmpty(collection: Collection<*>?): Boolean {
    return !(collection == null || collection.isEmpty())
}

/**
 * This method returns true if the collection is null or empty.
 *
 * @param collection input collection
 * @return true | false
 */
fun isEmpty(collection: Collection<*>?): Boolean {
    return !isNotEmpty(collection)
}

/**
 * This method returns true of the map is null or is empty.
 *
 * @param map input map
 * @return true | false
 */
fun isNotEmpty(map: Map<*, *>?): Boolean {
    return !(map == null || map.isEmpty())
}

/**
 * This method returns true if the object is null.
 *
 * @param object object
 * @return true | false
 */
fun isNotEmpty(`object`: Any?): Boolean {
    return `object` != null
}

/**
 * This method returns true if the input array is null or its length is zero.
 *
 * @param array input array
 * @return true | false
 */
fun isNotEmpty(array: Array<Any?>?): Boolean {
    return !(array == null || array.size == 0)
}

/**
 * This method returns true if the input string is null or its length is zero.
 *
 * @param string input string
 * @return true | false
 */
fun isNotEmpty(string: String?): Boolean {
    return !(string == null || string.trim { it <= ' ' }.isEmpty())
}

fun clearPreferences(context: Context?) {
    val prefs =
        PreferenceManager.getDefaultSharedPreferences(context) // here you get your preferences by either of two methods
    val editor = prefs.edit()
    editor.clear()
    editor.apply()
}

fun resetToSplashScreen(fragmentActivity: FragmentActivity) {
    clearApplicationData(fragmentActivity.applicationContext)
    val intent = Intent(fragmentActivity, SplashActivity::class.java)
    intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
    fragmentActivity.startActivity(intent)
    fragmentActivity.finish()
}

/**
 * Checking null edittext value
 *
 * @param editText
 * @return contain string
 */
fun getStringFromEditText(editText: EditText): String {
    val editable = editText.text
    return if (editable != null) {
        val string = editable.toString()
        if (isNotEmpty(string)) {
            string
        } else {
            ""
        }
    } else {
        ""
    }
}

/**
 * @param activity context of current activity
 * @return if activity is running
 */
fun isActivityRunning(activity: FragmentActivity?): Boolean {
    var isActivityRunning = false
    if (activity != null) {
        val window = activity.window
        if (window != null) {
            val decorView = window.decorView
            val root = decorView.rootView
            if (root != null) {
                isActivityRunning =
                    !activity.isFinishing && activity.window.decorView.rootView.isShown
            }
        }
    }
    return isActivityRunning
}

/**
 * Call Any Phone Numbers & Open Dialer
 *
 * @param activity as Fragment Activity
 * @param number   as Number to be displayed on Dailer
 */
fun callPhoneNumber(activity: FragmentActivity, number: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("tel:$number")
        activity.startActivity(intent)
    } catch (exception: Exception) {
        // ignore
        exception.printStackTrace()
    }
}

/**
 * Open Gmail with respective Email Id and Subject
 *
 * @param activity as Fragment Activity
 * @param number   as Number to be displayed on Dailer
 */
fun sendEmail(activity: FragmentActivity, email: String, subject: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setType("message/rfc822")
            .setData(Uri.parse("mailto:" + email))
            .putExtra(Intent.EXTRA_SUBJECT, subject)
            .setPackage("com.google.android.gm")
        activity.startActivity(intent)
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}

/**
 * redirects user to the play store
 *
 * @param activity    context of current activity
 * @param packageName target package name
 */
fun goToPlayStore(
    activity: FragmentActivity?,
    packageName: String
) {
    if (activity == null || !isNotEmpty(packageName)) return
    try {
        activity.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            )
        )
    } catch (e: java.lang.Exception) {
        activity.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
        )
        e.printStackTrace()
    }
}

fun navigateToRateOnPlayStore(activity: FragmentActivity) {
    try {
        activity.startActivity(
            Intent(
                "android.intent.action.VIEW",
                Uri.parse("market://details?id=${activity.packageName}")
            )
        )
    } catch (e: ActivityNotFoundException) {
        activity.startActivity(
            Intent(
                "android.intent.action.VIEW",
                Uri.parse("https://play.google.com/store/apps/details?id=${activity.packageName}")
            )
        )
    }
}

fun navigateToShareApp(activity: FragmentActivity) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_SUBJECT, "Photoshooto")
        var shareMessage = "\nLet me recommend you this application\n\n"
        shareMessage =
            """
                ${shareMessage}https://play.google.com/store/apps/details?id=${activity.packageName}
                
                
                """.trimIndent()
        putExtra(Intent.EXTRA_TEXT, shareMessage)
        type = "text/plain"
    }
    activity.startActivity(Intent.createChooser(shareIntent, "Share"))
}

/**
 * @param pm          package manager
 * @param packageName target package name
 * @return flag denoting if the give app is installed on the deice or not
 */
fun isPackageInstalled(
    pm: PackageManager,
    packageName: String?
): Boolean {
    return try {
        pm.getPackageInfo(packageName!!, 0) != null
    } catch (e: PackageManager.NameNotFoundException) {
        false
    } catch (e: java.lang.Exception) {
        false
    }
}

/**
 * * This gives the current epoch timestamp
 */
fun getCurrentTimeStamp(): Long {
    return Date().time
}

/*
 * Get the list of installed packages in the phone
 *
 * @param context as Application Context
 * @return List of android.content.pm.ApplicationInfo
 */
fun getInstalledAppsList(context: Context?): List<ApplicationInfo?>? {
    if (null != context) {
        val packageManager = context.packageManager
        if (null != packageManager) {
            val flags = (PackageManager.GET_META_DATA
                    or PackageManager.GET_SHARED_LIBRARY_FILES
                    or PackageManager.GET_UNINSTALLED_PACKAGES)
            return packageManager.getInstalledApplications(flags)
        }
    }
    return null
}

/**
 * gets the video id from youtube url
 *
 * @param url youtube url
 * @return video id
 */
fun getVideoIdFromUrl(url: String): String? {
    if (isNotEmpty(url)) {
        //embedded url
        var stringList: Array<String?> = url.split(".be/").toTypedArray()
        return if (stringList.size >= 2) {
            stringList[1]
        } else {
            //non embedded url
            stringList = url.split("\\?v=").toTypedArray()
            if (stringList.size >= 2) {
                stringList[1]
            } else {
                ""
            }
        }
    }
    return ""
}

/**
 * Sanitize String
 *
 * @param stringToBeSanitized as String
 * @param charToBeRemoved     as String
 * @param charToBeReplaced    as String
 */
fun sanitizedString(
    stringToBeSanitized: String,
    charToBeRemoved: String?,
    charToBeReplaced: String?
): String? {
    var sanitizedString = stringToBeSanitized.trim { it <= ' ' }
    if (isNotEmpty(sanitizedString) && null != charToBeRemoved) {
        if (sanitizedString.contains(charToBeRemoved)) {
            sanitizedString = sanitizedString.replace(charToBeRemoved, charToBeReplaced!!)
        }
    }
    return sanitizedString
}

/**
 * Opens URL On Browser
 *
 * @param fragmentActivity as FragmentActivity
 * @param url              as String
 */
fun openUrlOnBrowser(fragmentActivity: FragmentActivity, url: String) {
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(url)
    fragmentActivity.startActivity(i)
}

/**
 * Copies the specified text to clipboard.
 *
 * @param context
 * @param text
 */
fun copyToClipboard(context: Context, text: String?) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Label", text)
    clipboard.setPrimaryClip(clip)
    showToast(context, "Copied to clipboard")
}

/**
 * Show toast
 *
 * @param context application context
 * @param message message to show in toast
 */
fun showToast(context: Context?, message: String?) {
    // NOTE: There is a bug in API 25 which causes BadTokenException while showing toast message.
    // https://github.com/drakeet/ToastCompat
    if (Build.VERSION.SDK_INT != Build.VERSION_CODES.N_MR1) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

/**
 * @param versionName first ever version name for the given user
 * @return version code for given version name
 */
private fun getVersionCodeForName(versionName: String): Int {
    return try {
        val indexes = versionName.split("\\.").toTypedArray()
        val builder = StringBuilder()
        for (index in indexes) {
            builder.append(index)
        }
        val versionCode = builder.toString()
        versionCode.toInt()
    } catch (e: java.lang.Exception) {
        0
    }
}

/**
 * if the device os is greater than equal to 12 then add FLAG_IMMUTABLE to the flags
 *
 * @return empty intent flag for notification
 */
fun getEmptyFlags(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_IMMUTABLE
    } else {
        0
    }
}

/**
 * if the device os is greater than equal to 12 then add FLAG_IMMUTABLE to the flags
 *
 * @return one shot intent flag for notification
 */
fun getOneShotFlags(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
    } else {
        PendingIntent.FLAG_ONE_SHOT
    }
}

/**
 * if the device os is greater than equal to 12 then add FLAG_IMMUTABLE to the flags
 *
 * @return updated current intent flag for notification
 */
fun getUpdateCurrentFlags(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
}

/**
 * @param context context
 * @param resId   resource id of drawable
 * @return bitmap of given resource
 */
fun getBitmap(context: Context, @DrawableRes resId: Int): Bitmap? {
    return try {
        val drawable = ResourcesCompat.getDrawable(context.resources, resId, null)
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Opens passed url in chrome custom tab
 *
 * @param activity    activity context
 * @param url         target url
 * @param iconResId   resId of required of action icon
 * @param description description of added action button
 * @param intent      pending intent of action button
 */
fun openUrl(
    activity: FragmentActivity,
    url: String?,
    @DrawableRes iconResId: Int,
    description: String?,
    intent: PendingIntent?
) {
    if (isActivityRunning(activity)) {
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(true)
        val params = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(activity.resources.getColor(R.color.blue_clr))
            .build()
        builder.setDefaultColorSchemeParams(params)
        val bitmap: Bitmap? = getBitmap(activity, iconResId)
        if (bitmap != null) {
            builder.setActionButton(bitmap, description!!, intent!!, true)
        }
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(activity, Uri.parse(url))
    }
}

/**
 * Return Week Days according to the position
 */
fun getWeekDaysFromPosition(position: String?): String? {
    when (position) {
        "0" -> return "Sunday"
        "1" -> return "Monday"
        "2" -> return "Tuesday"
        "3" -> return "Wednesday"
        "4" -> return "Thursday"
        "5" -> return "Friday"
        "6" -> return "Saturday"
    }
    return null
}

/**
 * Return Position according to the WeekDays
 */
fun getPositionWeekDays(day: String?): Int {
    when (day) {
        "Sunday" -> return 0
        "Monday" -> return 1
        "Tuesday" -> return 2
        "Wednesday" -> return 3
        "Thursday" -> return 4
        "Friday" -> return 5
        "Saturday" -> return 6
    }
    return -1
}

/**
 * Convert Milliseconds to Time in HH:mm AM/PM
 */
fun getTimeFromMs(millis: Long): String {
    val time = Date(millis)
    val pattern = "hh:mm a"
    val dateFormat = SimpleDateFormat(pattern)
    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
    return dateFormat.format(time)
}

/**
 * Add suffix according to the date of month (th,st,nd,etc).
 */
fun getDayOfMonthSuffix(n: Int): String {
    return if (n >= 11 && n <= 13) {
        "th"
    } else when (n % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}

/**
 * handles the unauthorized error 401 and refreshes token after 20 days
 */
fun handleUnAuthorizedError(context: Context) {
    Toast.makeText(context, "Your session has expired! Kindly login again.", Toast.LENGTH_SHORT)
        .show()
    clearApplicationData(context)
    clearPreferences(context)
}

/**
 * check whether the given location is mocked location or not
 */
fun isMockLocation(location: Location?): Boolean {
    return location != null && location.isFromMockProvider
}

/**
 * Check if the string can be converted to a valid int
 *
 * @param numberInString number in string format
 * @return true if the string can be converted to a valid number and false otherwise
 */
fun isInteger(numberInString: String): Boolean {
    return try {
        numberInString.toInt()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

/**
 * give actual path of file from uri
 */
fun getActualPath(context: Context, uri: Uri): String? {

    // DocumentProvider
    if (DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            if (id != null && id.startsWith("raw:")) {
                return id.substring(4)
            }
            val contentUriPrefixesToTry = arrayOf(
                "content://downloads/public_downloads",
                "content://downloads/my_downloads",
                "content://downloads/all_downloads"
            )
            for (contentUriPrefix in contentUriPrefixesToTry) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse(contentUriPrefix),
                    java.lang.Long.valueOf(id)
                )
                try {
                    val path: String? = getDataColumn(
                        context,
                        contentUri,
                        null,
                        null
                    )
                    if (path != null) {
                        return path
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
            val fileName = getFileName(context, uri)
            val cacheDir = getDocumentCacheDir(context)
            val file: File? = generateFileName(fileName, cacheDir)
            val destinationPath: String?
            if (file != null) {
                destinationPath = file.absolutePath
                saveFileFromUri(context, uri, destinationPath)
            } else {
                destinationPath = null
            }
            return destinationPath
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if (IMAGE == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if (VIDEO == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if (AUDIO == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )
            return getDataColumn(
                context,
                contentUri,
                selection,
                selectionArgs
            )
        }
    } else if (isNotEmpty(uri) && "content".equals(
            uri.scheme,
            ignoreCase = true
        )
    ) {

        // Return the remote address
        return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
            context,
            uri,
            null,
            null
        )
    } else if (isNotEmpty(uri) && "file".equals(
            uri.scheme,
            ignoreCase = true
        )
    ) {
        return uri.path
    }
    return null
}

fun generateFileName(fileNameInput: String?, directory: File?): File? {
    var name = fileNameInput ?: return null
    var file = File(directory, name)
    if (file.exists()) {
        var fileName = name
        var extension = ""
        val dotIndex = name.lastIndexOf('.')
        if (dotIndex > 0) {
            fileName = name.substring(0, dotIndex)
            extension = name.substring(dotIndex)
        }
        var index = 0
        while (file.exists()) {
            index++
            name = "$fileName($index)$extension"
            file = File(directory, name)
        }
    }
    try {
        if (!file.createNewFile()) {
            return null
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
    return file
}

/**
 * SaveFile From Uri to destinationPath
 */
private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String?) {
    var `is`: InputStream? = null
    var bos: BufferedOutputStream? = null
    try {
        `is` = context.contentResolver.openInputStream(uri)
        bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
        val buf = ByteArray(1024)
        `is`!!.read(buf)
        do {
            bos.write(buf)
        } while (`is`.read(buf) != -1)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    } finally {
        try {
            `is`?.close()
            bos?.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * Get Path from URI
 */
fun getPathFromUri(context: Context, uri: Uri): String {
    val absolutePath = getActualPath(context, uri)
    return absolutePath ?: uri.toString()
}

/**
 * Get File Name from uri
 */
fun getFileName(context: Context, uri: Uri): String? {
    val mimeType = context.contentResolver.getType(uri)
    var filename: String? = null
    if (mimeType == null) {
        val path = getPathFromUri(context, uri)
        val file = File(path)
        filename = file.name
    } else {
        val returnCursor = context.contentResolver.query(
            uri, null,
            null, null, null
        )
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            filename = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
    }
    return filename
}

/**
 * Get Document Cache Directory
 */
fun getDocumentCacheDir(context: Context): File {
    val dir = File(context.cacheDir, DOCUMENTS_DIR)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return dir
}

/**
 * Get Name from filePath
 */
fun getNameFromFilePath(filename: String?): String? {
    if (filename == null) {
        return null
    }
    val index = filename.lastIndexOf('/')
    return filename.substring(index + 1)
}

/**
 * Return data column according to the content uri given
 */
fun getDataColumn(
    context: Context, uri: Uri?, selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(
        column
    )
    try {
        cursor = context.contentResolver.query(
            uri!!, projection, selection, selectionArgs,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is Google Photos.
 */
fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}


/**
 * gets the regex string gstIn number
 * (e.g. 37ABCDE1724A2Z5)
 *
 * @return
 */
fun getGstRegex(): String {
    return "[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}"
}

/**
 * Check if the custom id is of our generated format
 *
 * @return true if its is of our generated format and false otherwise
 */
fun matchFoundForCustomId(customId: String): Boolean {
    var matcher = ""
    if (isNotEmpty(customId)) {
        if (customId.length > 6) {
            matcher = customId.substring(0, 6)
        }
    }
    return matcher.equals("APP", ignoreCase = true)
}

/**
 * generates the 5 digit random number
 *
 * @return 5 digit no
 */
fun generateCustomIdWithFiveDigitNo(): String {
    return "APP-" + (Math.random() * 100000 + 10000).toInt()
}

/**
 * hides the keyboard explicitly
 *
 * @param activity
 * @param view
 */
fun hideKeyboard(activity: Activity, view: View) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * shows the keyboard
 *
 * @param activity
 * @param view
 */
fun showKeyboard(activity: Activity, view: View?) {
    val mImm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    mImm.showSoftInput(view, 0)
}

/**
 * Get IP address of the mobile devices
 *
 * @return ipaddress particularly IPv4
 */
fun getIPAddress(): String {
    try {
        val interfaces: List<NetworkInterface> =
            Collections.list(NetworkInterface.getNetworkInterfaces())
        for (networkInterface in interfaces) {
            val inetAddressList: List<InetAddress> =
                Collections.list(networkInterface.inetAddresses)
            for (inetAddress in inetAddressList) {
                if (!inetAddress.isLoopbackAddress) {
                    val hostAddress = inetAddress.hostAddress?.uppercase(Locale.getDefault())
                    val isIPv4 = hostAddress?.indexOf(':')!! < 0
                    if (isIPv4) return hostAddress
                }
            }
        }
    } catch (ex: java.lang.Exception) {
        return ""
    }
    return ""
}

/**
 * Get unique device id which stays unique for every installation
 *
 * @param context application context
 * @return unique device id
 */
fun getDeviceId(context: Context): String {
    val deviceId: String
    val androidId =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    deviceId =
        if (isNotEmpty(androidId) && !"9774d56d682e549c".equals(
                androidId,
                ignoreCase = true
            )
        ) {
            androidId
        } else {
            val serial = Build.SERIAL
            if (isNotEmpty(serial)) {
                serial
            } else {
                getInstallationId(context)
            }
        }
    return deviceId
}

/**
 * Read installation id written in the readonly file
 *
 * @param installation installation file
 * @return installation id
 * @throws IOException input output exception
 */
@Throws(IOException::class)
private fun readInstallationId(installation: File): String {
    val f = RandomAccessFile(installation, "r")
    val bytes = ByteArray(f.length().toInt())
    f.readFully(bytes)
    f.close()
    return String(bytes)
}

/**
 * Write installation id
 *
 * @param installation file for installation id
 * @throws IOException input output exception
 */
@Throws(IOException::class)
private fun writeInstallationId(installation: File) {
    val out = FileOutputStream(installation)
    val id = UUID.randomUUID().toString()
    out.write(id.toByteArray())
    out.close()
}

/**
 * Get installation id if already written and write if not and then return
 *
 * @param context application context
 * @return installation id
 */
@Synchronized
private fun getInstallationId(context: Context): String {
    val installationId: String
    val installation = File(context.filesDir, "INSTALLATION_ID")
    installationId = try {
        if (!installation.exists()) {
            writeInstallationId(installation)
        }
        readInstallationId(installation)
    } catch (e: java.lang.Exception) {
        throw RuntimeException(e)
    }
    return installationId
}

/**
 * Upper Case First Alphabet int any String
 *
 * @param name as String to Convert into UpperCase First
 */
fun ucFirst(name: String): String {
    var capitilizedString = ""
    if (isNotEmpty(name)) {
        if (name.contains(" ")) {
            val strArray = name.split(" ").toTypedArray()
            val builder = java.lang.StringBuilder()
            for (s in strArray) {
                capitilizedString =
                    s.substring(0, 1).uppercase(Locale.getDefault()) + s.substring(1)
                builder.append(capitilizedString)
                    .append(" ")
            }
        } else {
            capitilizedString =
                name.substring(0, 1).uppercase(Locale.getDefault()) + name.substring(1)
        }
    }
    return capitilizedString
}

/**
 * Function Displays Address on Maps
 *
 * @param context          as Context
 * @param addressToDisplay as String Address to Display on Map
 */
fun showMapUsingAddress(context: Context, addressToDisplay: String) {
    val mapUri = Uri.parse("geo:0,0?q=" + Intent(addressToDisplay))
    val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    }
}

/**
 * converts the integer list to integer array
 *
 * @param list
 * @return integer array
 */
fun toIntArray(list: List<Int>): IntArray {
    val ret = IntArray(list.size)
    var i = 0
    for (e in list) {
        ret[i++] = e
    }
    return ret
}

/**
 * Sets the secure flag to avoid taking screenshots.
 */
fun secureWindow(activity: FragmentActivity) {
    if (isActivityRunning(activity)) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }
}

/**
 * @param context application context
 * @return version info for the app
 */
fun getVersionInfo(context: Context): VersionInfo? {
    return try {
        val info = context.packageManager
            .getPackageInfo(context.packageName, 0) ?: return null
        VersionInfo(info.versionCode, info.versionName)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * @param context application context
 * @return version name (eg: "12.3.1")
 */
fun getVersionName(context: Context): String? {
    val info: VersionInfo = getVersionInfo(context) ?: return null
    return info.name
}

/**
 * @param context application context
 * @return version code (eg: 100120301)
 */
fun getVersionCode(context: Context): Int {
    val info: VersionInfo = getVersionInfo(context) ?: return 0
    return info.code
}

/**
 * checks if input gst is valid or not by matching with regex
 *
 * @param gst text that is to match
 * @return true if matches with regex and false if it doesn't match
 */
fun isValidGst(gst: String): Boolean {
    val pattern = Pattern.compile(getGstRegex())
    val matcher = pattern.matcher(gst)
    return matcher.find()
}

/**
 * checks if input pan is valid or not by matching with regex
 */
fun isValidPAN(pan: String): Boolean {
    var result = true
    if (isNotEmpty(pan) && pan.length != 10) {
        result = false
    } else if (isNotEmpty(pan) && pan.length > 1) {
        val pattern = Pattern.compile("(([A-Z]{5})([0-9]{4})([a-zA-Z]))")
        val matcher = pattern.matcher(pan)
        result = if (!matcher.matches()) {
            false
        } else {
            //([CPHFATBLJG])
            val char_4 = pan.substring(3, 4)
            val pat = Pattern.compile("[CPHFATBLJG]")
            val mat = pat.matcher(char_4)
            mat.matches()
        }
    }
    return result
}

@Throws(IOException::class)
fun createFileFromUri(context: Context, uri: Uri): File? {
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = createImageFile(context)
        tempFile.deleteOnExit()
        val out = FileOutputStream(tempFile)
        if (inputStream != null) {
            var count: Long = 0
            var n: Int
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (-1 != inputStream.read(buffer).also { n = it }) {
                out.write(buffer, 0, n)
                count += n.toLong()
            }
            inputStream.close()
        }
        out.close()
        return tempFile
    } catch (e: java.lang.Exception) {
        return null
    } catch (io: IOException) {
        return null
    } catch (e: FileNotFoundException) {
        return null
    }
}

@Throws(IOException::class)
private fun createImageFile(context: Context): File {
    // Create an image file name
    val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    return File.createTempFile(
        "image_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        val mCurrentPhotoPath = absolutePath
        Log.e("mCurrentPhotoPath", "" + mCurrentPhotoPath)

    }
}

fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(
        inContext.getContentResolver(),
        inImage,
        inContext.packageName,
        null
    )
    return Uri.parse(path)
}

fun Any?.log() {
    Log.d("OBJECT", Gson().toJson(this))
}


fun FileUpload?.filterAttachments(type: String = "aadhar"): Boolean {
    if (this?.category.equals(type)) return true
    return false
}
