package com.photoshooto

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.multidex.MultiDex
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.photoshooto.data.di.*
import com.photoshooto.util.PreferenceManager
import com.photoshooto.util.SharedPrefsHelper
import com.pixplicity.easyprefs.library.Prefs
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    private var mInstance: App? = null

    companion object {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        PreferenceManager.init(context = this)

        mInstance = this
        try {
//            FirebaseApp.initializeApp(this)
            FirebaseApp.initializeApp(applicationContext)
            Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
        } catch (e: Exception) {
        }

        MultiDex.install(this)
        SharedPrefsHelper.init(applicationContext)
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(
                listOf(
                    AppModule,
                    NetworkModule,
                    repositoryModule,
                    viewModelModule,
                    viewModelModule,
                    roomModule
                )
            )
        }

        JodaTimeAndroid.init(this)

        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(this.packageName)
            .setUseDefaultSharedPreference(true)
            .build()

    }

    fun getInstance(): Context? {
        return mInstance
    }
}
