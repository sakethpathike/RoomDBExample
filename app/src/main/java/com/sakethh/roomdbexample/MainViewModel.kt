package com.sakethh.roomdbexample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.roomdbexample.localDB.DataBase
import com.sakethh.roomdbexample.localDB.NotesDBDTO
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _existingNotes = MutableStateFlow<List<NotesDBDTO>>(emptyList())
    val existingNotes = _existingNotes.asStateFlow()

    companion object {
        lateinit var database: DataBase
    }

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() }

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            database.localDBData().getAllNotes().collect {
                _existingNotes.emit(it)
            }
        }
    }
}