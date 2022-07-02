package com.sgs.absensi.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sgs.absensi.database.dao.DatabaseDao
import com.sgs.absensi.model.ModelDatabase



@Database(entities = [ModelDatabase::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao?
}