package com.sakethh.roomdbexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.roomdbexample.localDB.DataBase
import com.sakethh.roomdbexample.localDB.NotesDBDTO
import com.sakethh.roomdbexample.ui.theme.RoomDBExampleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val textFieldValue = rememberSaveable { mutableStateOf("") }
            val existingTextValue = rememberSaveable { mutableStateOf("") }
            val isEditBtnClicked = rememberSaveable { mutableStateOf(false) }

            var currentIndex = 0

            val coroutineScope = rememberCoroutineScope()

            val mainViewModel: MainViewModel = viewModel()
            val existingNotes = mainViewModel.existingNotes.collectAsState().value


            /* use collectAsState() carefully,
               it collects data without lifecycle aware which means data gets collected even when the app is in
               background which doesn't make sense in most of the cases*/

            var idForDB = 0

            RoomDBExampleTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Spacer(modifier = Modifier.height(25.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextField(value = textFieldValue.value, onValueChange = {
                            textFieldValue.value = it
                        }, modifier = Modifier.fillMaxWidth(0.75f))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 15.dp)
                                .clickable {
                                    coroutineScope
                                        .launch {
                                            idForDB = if (existingNotes.isEmpty()) {
                                                0
                                            } else {
                                                existingNotes.last().primaryKey + 1
                                            }
                                        }
                                        .invokeOnCompletion {
                                            coroutineScope.launch {
                                                MainViewModel.database
                                                    .localDBData()
                                                    .addNewNote(
                                                        notesDBDTO = NotesDBDTO(
                                                            _data = textFieldValue.value,
                                                            primaryKey = idForDB
                                                        )
                                                    )
                                            }
                                        }
                                },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    LazyColumn {
                        items(items = existingNotes) {
                            Column() {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Text(
                                        text = it._data,
                                        modifier = Modifier.fillMaxWidth(0.65f),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 18.sp,
                                        lineHeight = 20.sp,
                                        textAlign = TextAlign.Start,
                                        softWrap = true
                                    )
                                    if (!isEditBtnClicked.value) {
                                        Icon(
                                            imageVector = if (!isEditBtnClicked.value) Icons.Default.Edit else Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.clickable {
                                                isEditBtnClicked.value = !isEditBtnClicked.value
                                                existingTextValue.value = it._data
                                                currentIndex = it.primaryKey
                                            },
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.clickable {
                                            coroutineScope.launch {
                                                MainViewModel.database.localDBData().deleteNote(
                                                    notesDBDTO = NotesDBDTO(
                                                        _data = it._data, primaryKey = it.primaryKey
                                                    )
                                                )
                                            }
                                        },
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (isEditBtnClicked.value && currentIndex == it.primaryKey) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        TextField(
                                            value = existingTextValue.value, onValueChange = {
                                                existingTextValue.value = it
                                            }, modifier = Modifier
                                                .fillMaxWidth(0.75f)
                                                .padding(top = 10.dp)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(top = 15.dp)
                                                .clickable {
                                                    isEditBtnClicked.value = false
                                                    coroutineScope.launch {
                                                        MainViewModel.database
                                                            .localDBData()
                                                            .updateExistingNote(
                                                                notesDBDTO = NotesDBDTO(
                                                                    _data = existingTextValue.value,
                                                                    primaryKey = it.primaryKey
                                                                )
                                                            )
                                                    }
                                                },
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
        MainViewModel.database = DataBase.getLocalDB(this)
    }
}
