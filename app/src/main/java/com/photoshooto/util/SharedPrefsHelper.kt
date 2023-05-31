package com.photoshooto.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.photoshooto.domain.model.User
import com.photoshooto.ui.job.utility.AppConstant
import com.pixplicity.easyprefs.library.Prefs

object SharedPrefsHelper {
    private lateinit var prefs: SharedPreferences
    private const val PREFS_NAME = "photoshooto"
    const val subscribed = "subscribed"
    const val UserProfile = "userprofile"
    const val IsLogin = "IsLogin"
    const val AuthKey = "AuthKey"
    const val DeviceToken = "DeviceToken"
    const val PushTokenKey = "push_token_key"

    const val Latitude = "latitude"
    const val Longitude = "longitude"
    const val AndroidId = AppConstant.vDeviceUniqueId

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun read(key: String, defValue: String): String? {
        return prefs.getString(key, defValue)
    }

    fun read(key: String, defValue: Boolean): Boolean {
        return prefs.getBoolean(key, defValue)
    }

    fun read(key: String, defValue: Int): Int {
        return prefs.getInt(key, defValue)
    }

    fun read(key: String): String? {
        return prefs.getString(key, null)
    }

    fun readBoolean(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    fun readInt(key: String): Int {
        return prefs.getInt(key, 0)
    }

    fun write(key: String, value: String) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putString(key, value)
            commit()
        }
    }

    fun write(key: String, value: Boolean) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putBoolean(key, value)
            commit()
        }
    }

    fun write(key: String, value: Int) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putInt(key, value)
            commit()
        }
    }

    fun setSubscribed(isSubscribed: Boolean) {
        Prefs.putBoolean(SharedPrefConstant.subscribed, isSubscribed)
    }

    fun getSubscribed(): Boolean {
        return Prefs.getBoolean(SharedPrefConstant.subscribed, false)
    }

    fun setBackClick(value: String) {
        Prefs.putString(SharedPrefConstant.CLICKED_ITEM, value)
    }

    fun getBackClick(): String {
        return Prefs.getString(SharedPrefConstant.CLICKED_ITEM, "")
    }

    fun isUserLoggedIn(): Boolean {
        return !(getAuthKey().isNullOrBlank())
    }

    fun getAuthKey(): String {
        return Prefs.getString(AuthKey, read(SharedPrefConstant.AUTH_TOKEN))
    }

    fun setUser(user: User) {
        Prefs.putString(UserProfile, Gson().toJson(user))
    }

    fun getLoggedUserId(): String {
        val userJson = Prefs.getString(UserProfile, "")
        return if (null != userJson) Gson().fromJson<User>(
            userJson,
            User::class.java
        ).id.toString() else ""
    }

    fun getUserCommon(): User? {
        val userJson = Prefs.getString(UserProfile, "")
        return if (null != userJson) Gson().fromJson<User>(userJson, User::class.java) else null
    }

    fun clearAllPreferences() {
        prefs.edit { clear() }
    }
}