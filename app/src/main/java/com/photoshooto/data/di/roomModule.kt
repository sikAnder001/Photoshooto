package com.photoshooto.data.di

import androidx.room.Room
import com.photoshooto.data.database.AppDatabase
import com.photoshooto.ui.job.utility.AppConstant
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val roomModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            AppConstant.DB_NAME
        ).build()
    }

    single { get<AppDatabase>().appDao() }
}