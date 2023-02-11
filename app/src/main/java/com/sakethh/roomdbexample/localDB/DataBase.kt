package com.sakethh.roomdbexample.localDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NotesDBDTO::class], version = 1)
abstract class DataBase : RoomDatabase() {

    abstract fun localDBData(): DBDao

    companion object {
        @Volatile
        private var db: DataBase? = null
        fun getLocalDB(context: Context): DataBase {
            val instance = db
            return instance
                ?: synchronized(this) {
                    val roomDBInstance = Room.databaseBuilder(
                        context.applicationContext,
                        DataBase::class.java,
                        "notes_db"
                    ).build()
                    db = roomDBInstance
                    return roomDBInstance
                }
        }
    }
}