package com.photoshooto.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object ThemeManager {
    private lateinit var sharedPreferences: SharedPreferences

    private const val DEFAULT_PREFERENCES = "default_preferences"

    private const val PREFERENCE_NIGHT_MODE = "preference_night_mode"
    private const val PREFERENCE_NIGHT_MODE_DEF_VAL = AppCompatDelegate.MODE_NIGHT_NO

    fun init(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.getSharedPreferences(
                DEFAULT_PREFERENCES,
                AppCompatActivity.MODE_PRIVATE
            )

            nightModeLiveData.value = nightMode
            isDarkThemeLiveData.value = isDarkTheme

            sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangedListener)
        }
    }

    private val nightModeLiveData: MutableLiveData<Int> = MutableLiveData()
    val nightModeLive: LiveData<Int>
        get() = nightModeLiveData

    private val isDarkThemeLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isDarkThemeLive: LiveData<Boolean>
        get() = isDarkThemeLiveData

    private val nightMode: Int
        get() = sharedPreferences.getInt(PREFERENCE_NIGHT_MODE, PREFERENCE_NIGHT_MODE_DEF_VAL)

    var isDarkTheme: Boolean = false
        get() = nightMode == AppCompatDelegate.MODE_NIGHT_YES
        set(value) {
            sharedPreferences.edit {
                putInt(
                    PREFERENCE_NIGHT_MODE,
                    if (value) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
                field = value
            }
        }

    private val preferenceChangedListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                PREFERENCE_NIGHT_MODE -> {
                    nightModeLiveData.value = nightMode
                    isDarkThemeLiveData.value = isDarkTheme
                }
            }
        }
}
