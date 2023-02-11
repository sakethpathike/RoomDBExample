package com.sakethh.roomdbexample.localDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="notes_table") // define a unique name for table
data class NotesDBDTO(
    @ColumnInfo(name = "text") /* column's name irrespective of variable name*/
    val _data: String,
    @PrimaryKey // primary key for a column and its row
    val primaryKey: Int
)
