package com.sakethh.roomdbexample.localDB

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DBDao {

    /*
    You can use annotations directly or can use SQL queries for making CRUD operations, either way things work!
    */

    @Query("SELECT * FROM notes_table") // SQL query for getting all notes from the db
    fun getAllNotes(): Flow<List<NotesDBDTO>>

    @Insert // Make sure to add the annotation in order to add the new notes when required
    suspend fun addNewNote(notesDBDTO: NotesDBDTO)

    @Update // Make sure to add the annotation in order to add the update the existing notes
    suspend fun updateExistingNote(notesDBDTO: NotesDBDTO)

    @Delete
    suspend fun deleteNote(notesDBDTO: NotesDBDTO)
}