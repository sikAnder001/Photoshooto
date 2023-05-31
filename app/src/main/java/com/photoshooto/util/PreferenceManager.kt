package com.photoshooto.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

object PreferenceManager {
    private lateinit var sharedPreferences: SharedPreferences

    public const val PhotoShooto_PREFERENCES = "PhotoShooto Prefs"

    private const val IS_FIRST_TIME_LOGIN = "firstTimeLogin"


    private const val UserID = "UserID" //Save UserID
    private const val MOBILE_NUMBER = "MOBILE_NUMBER" //Save Mobile
    private const val Email = "Email" //Save Email
    private const val Username = "Username" //Save Username
    private const val profileUrl = "profileUrl" //Save profileUrl

    private const val notifyDone = "notifyDone" //Save profileUrl

    private const val verification_key = "verification_key" //Save verification_key
    private const val saveOtp = "saveOtp" //Save saveOtp
    private val IS_LOGGED_IN = "is_login"
    private val authToken = "authToken"
    private val ROLE = "role"

    var saveMobile: String?
        get() = getFromPrefs(MOBILE_NUMBER, "")
        set(value) = setToPrefs(MOBILE_NUMBER, value)

    var saveUserID: String?
        get() = getFromPrefs(UserID, "")
        set(value) = setToPrefs(UserID, value)

    var saveEmail: String?
        get() = getFromPrefs(Email, "")
        set(value) = setToPrefs(Email, value)

    var saveUsername: String?
        get() = getFromPrefs(Username, "")
        set(value) = setToPrefs(Username, value)

    var saveProfileUrl: String?
        get() = getFromPrefs(profileUrl, "")
        set(value) = setToPrefs(profileUrl, value)

    var saveNotifyDone: Boolean?
        get() = getFromPrefs(notifyDone, false)
        set(value) = setToPrefs(notifyDone, value)


    var saveVerification_key: String?
        get() = getFromPrefs(verification_key, "")
        set(value) = setToPrefs(verification_key, value)

    var saveOtpKey: String?
        get() = getFromPrefs(saveOtp, "")
        set(value) = setToPrefs(saveOtp, value)

    var isLogged: Boolean
        get() = getFromPrefs(IS_LOGGED_IN, false)
        set(value) = setToPrefs(IS_LOGGED_IN, value)

    var authTokenSave: String?
        get() = getFromPrefs(authToken, "")
        set(value) = setToPrefs(authToken, value)

    var getLoginType: String
        get() = getFromPrefs(LoginType, "")
        set(value) = setToPrefs(LoginType, value)

    var getCreated_at: String
        get() = getFromPrefs(CreateAt, "")
        set(value) = setToPrefs(CreateAt, value)
    var getUpdated_at: String
        get() = getFromPrefs(UpdatedAt, "")
        set(value) = setToPrefs(UpdatedAt, value)


    private const val EMPLOYEE_MOBILE_NUMBER = "employeeMobileNumber"

    private val FIRST_LAUNCH = "firstLaunch"
    private const val SECURE_PIN = "securePin"
    private const val EMPLOYEE_BY = "employeeBy"
    private const val CreateAt = "CreateAt"
    private const val UpdatedAt = "UpdatedAt"
    private const val LoginType = "LoginType"

    private const val LATTITUDE = "lattitude"
    private const val LONGITUDE = "longitude"

    private const val DAY_LOGGED = "day_logged"

    const val LoggedSuccess = "LoggedSuccess"


    internal lateinit var spEditor: SharedPreferences.Editor

    private val CLEAR_PROFILE_PREFS_KEYS = listOf(SECURE_PIN)
    private val UNACCEPTABLE_PREFS_NULL_TYPES =
        listOf(Boolean::class, Int::class, Float::class, Long::class)

    fun init(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.getSharedPreferences(
                PhotoShooto_PREFERENCES,
                AppCompatActivity.MODE_PRIVATE
            )
        }
    }

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        spEditor.putBoolean(FIRST_LAUNCH, isFirstTime)
        spEditor.commit()
    }

    fun FirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(FIRST_LAUNCH, true)
    }


    var isFirstTimeLogin: Boolean
        get() = getFromPrefs(IS_FIRST_TIME_LOGIN, false)
        set(value) = setToPrefs(IS_FIRST_TIME_LOGIN, value)

    var loggedinMobileNumber: String?
        get() = getFromPrefs(EMPLOYEE_MOBILE_NUMBER, "")
        set(value) = setToPrefs(EMPLOYEE_MOBILE_NUMBER, value)

    var employeeBy: String
        get() = getFromPrefs(EMPLOYEE_BY, "")
        set(value) = setToPrefs(EMPLOYEE_BY, value)

    var role: String
        get() = getFromPrefs(ROLE, "")
        set(value) = setToPrefs(ROLE, value)


    var lattitude: String
        get() = getFromPrefs(LATTITUDE, "")
        set(value) = setToPrefs(LATTITUDE, value)
    var longitude: String
        get() = getFromPrefs(LONGITUDE, "")
        set(value) = setToPrefs(LONGITUDE, value)


    var today_logged: String
        get() = getFromPrefs(DAY_LOGGED, "")
        set(value) = setToPrefs(DAY_LOGGED, value)

    var loggedStatus: Boolean
        get() = getFromPrefs(LoggedSuccess, false)
        set(value) = setToPrefs(LoggedSuccess, value)


    var securePin: String
        get() = getFromPrefs(SECURE_PIN, "")
        set(value) = setToPrefs(SECURE_PIN, value)

    var isVerified: Boolean
        get() = getFromPrefs(IS_LOGGED_IN, false)
        set(value) = setToPrefs(IS_LOGGED_IN, value)

    fun clearAllPreferences() {
        checkHasSharedPrefsInitialized()
        sharedPreferences.edit { clear() }
    }

    fun clearProfilePreferences() {
        checkHasSharedPrefsInitialized()
        sharedPreferences.edit {
            CLEAR_PROFILE_PREFS_KEYS.forEach { remove(it) }
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    private inline fun <reified T> getFromPrefs(key: String, default: T): T {
        checkHasSharedPrefsInitialized()
        if (default == null && UNACCEPTABLE_PREFS_NULL_TYPES.any { T::class == it }) {
            throw IllegalArgumentException("$default of type ${T::class.simpleName} cannot be null ")
        }

        return when (T::class) {
            Boolean::class -> sharedPreferences.getBoolean(key, default as Boolean)
            String::class -> sharedPreferences.getString(key, default as? String)
            Int::class -> sharedPreferences.getInt(key, default as Int)
            Float::class -> sharedPreferences.getFloat(key, default as Float)
            Long::class -> sharedPreferences.getLong(key, default as Long)
            Set::class -> sharedPreferences.getStringSet(key, default as? Set<String>)
            else -> throw IllegalArgumentException("cannot get value for $key from SharedPreferences")
        }.let { it as T }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> setToPrefs(key: String, value: T?) {
        checkHasSharedPrefsInitialized()
        if (value == null && UNACCEPTABLE_PREFS_NULL_TYPES.any { T::class == it }) {
            throw IllegalArgumentException("$value of type ${T::class.simpleName} cannot be null ")
        }

        sharedPreferences.edit {
            when (T::class) {
                Boolean::class -> putBoolean(key, value as Boolean)
                String::class -> putString(key, value as? String)
                Int::class -> putInt(key, value as Int)
                Float::class -> putFloat(key, value as Float)
                Long::class -> putLong(key, value as Long)
                Set::class -> putStringSet(key, value as? Set<String>)
                else -> throw IllegalArgumentException("cannot set $value to $key in SharedPreferences")
            }
        }
    }

    private fun checkHasSharedPrefsInitialized() {
        if (!::sharedPreferences.isInitialized) {
            throw UninitializedPropertyAccessException("missed to call Pref.init(context). This is necessary and should be called in Application class")
        }
    }
}
