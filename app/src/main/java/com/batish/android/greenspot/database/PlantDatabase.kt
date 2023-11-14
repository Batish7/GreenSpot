package com.batish.android.greenspot.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.batish.android.greenspot.Plant


@Database(entities = [ Plant::class ], version=1)
@TypeConverters(PlantTypeConverters::class)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
}