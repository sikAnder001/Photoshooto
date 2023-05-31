package com.photoshooto.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.photoshooto.domain.model.Person

@Database(entities = [Person::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}