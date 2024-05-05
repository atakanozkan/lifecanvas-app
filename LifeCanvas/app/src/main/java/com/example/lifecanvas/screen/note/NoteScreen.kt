package com.example.lifecanvas.screen.note

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.example.lifecanvas.R
import com.example.lifecanvas.model.NoteModel
import com.example.lifecanvas.screen.filter.DatePicker
import com.example.lifecanvas.screen.filter.ExpandableFilterPanel
import com.example.lifecanvas.screen.filter.NoteTypeDropdownFilter
import com.example.lifecanvas.screen.filter.PublicPrivateToggleFilter
import com.example.lifecanvas.screen.filter.SearchBar
import com.example.lifecanvas.screen.filter.isValidTitle
import com.example.lifecanvas.viewModel.NoteViewModel
import com.example.lifecanvas.viewModel.UserViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(noteViewModel: NoteViewModel, userViewModel: UserViewModel,navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var selectedNoteType by remember { mutableStateOf("") }
    var isPublicFilter by remember { mutableStateOf<Boolean?>(null) }
    var noteTypeFilter by remember { mutableStateOf<String?>(null) }
    var createdDateStartFilter by remember { mutableStateOf<Date?>(null) }
    var createdDateEndFilter by remember { mutableStateOf<Date?>(null) }
    var modifiedDateStartFilter by remember { mutableStateOf<Date?>(null) }
    var modifiedDateEndFilter by remember { mutableStateOf<Date?>(null) }
    var noteList by remember { mutableStateOf(noteViewModel.getAllNotes())}

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("My Notes") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("mainScreen") }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
            SearchBar(
                searchText = searchText,
                onSearchTextChanged = { newText ->
                    searchText = newText
                },
                onSearchButtonClicked = {
                    noteList= noteViewModel.searchNotesWithFilters(
                        searchText,
                        isPublicFilter,
                        noteTypeFilter,
                        createdDateStartFilter,
                        createdDateEndFilter,
                        modifiedDateStartFilter,
                        modifiedDateEndFilter
                    )
                }
            )
            ExpandableFilterPanel(
                onResetFilters = {
                searchText = ""
                selectedNoteType= ""
                isPublicFilter = null
                noteTypeFilter= null
                createdDateStartFilter = null
                createdDateEndFilter = null
                modifiedDateStartFilter= null
                modifiedDateEndFilter= null
                noteList = noteViewModel.getAllNotes()
                }
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    PublicPrivateToggleFilter(isPublicFilter) { newValue ->
                        isPublicFilter = newValue
                    }
                    NoteTypeDropdownFilter(noteTypeFilter) { newType ->
                        noteTypeFilter = newType
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    DatePicker("Created After", createdDateStartFilter, Icons.Default.DateRange) { newDate ->
                        createdDateStartFilter = newDate
                    }
                    DatePicker("Created Before", createdDateEndFilter, Icons.Default.DateRange) { newDate ->
                        createdDateEndFilter = newDate
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    DatePicker("Modified After", modifiedDateStartFilter, Icons.Default.DateRange) { newDate ->
                        modifiedDateStartFilter = newDate
                    }
                    DatePicker("Modified Before", modifiedDateEndFilter, Icons.Default.DateRange) { newDate ->
                        modifiedDateEndFilter = newDate
                    }
                }
            }
            NotesList(noteList, userViewModel,navController)
        }
        Column(modifier = Modifier.fillMaxSize()) {
            AddNoteFloatingActionButton { noteType ->
                selectedNoteType = noteType
                showAddNoteDialog = true
            }

            if (showAddNoteDialog) {
                AddNoteDialog(
                    noteType = selectedNoteType,
                    noteViewModel = noteViewModel,
                    onDismiss = { showAddNoteDialog = false },
                    onNoteAdded = { note ->
                        noteViewModel.insert(note)
                        showAddNoteDialog = false
                        noteList = noteViewModel.getAllNotes()
                    }
                )
            }
        }
    }
}

@Composable
fun NotesList(notes: LiveData<List<NoteModel>>, userViewModel: UserViewModel,navController: NavController) {
    val noteList by notes.observeAsState(initial = emptyList())
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        content = {
            items(noteList.size) { index ->
                NoteItem(noteList[index],userViewModel,navController)
            }
        }
    )
}


@Composable
fun NoteItem(note: NoteModel, userViewModel: UserViewModel,navController: NavController) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                if (note.isPublic) {
                    navController.navigate("noteDetailScreen/${note.id}")
                } else {
                    showPasswordDialog = true
                }
            },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!note.isPublic) {
                Icon(Icons.Default.Lock, contentDescription = "Private", tint = Color.Gray)
            }
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = note.type,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

        }
    }
    if (showPasswordDialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PasswordDialog(
                onDismiss = { showPasswordDialog = false },
                onPasswordVerified = {
                    showPasswordDialog = false
                    navController.navigate("noteDetailScreen/${note.id}")
                },
                userViewModel = userViewModel
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(
    noteType: String,
    noteViewModel: NoteViewModel,
    onDismiss: () -> Unit,
    onNoteAdded: (NoteModel) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    val currentDateTime = remember { Date() }
    val isTitleValid = remember(title) { isValidTitle(title) }
    val isTitleUsedLiveData = noteViewModel.isTitleUsed(title)
    val isTitleUsed by isTitleUsedLiveData.observeAsState(initial = false)
    val isCreateButtonEnabled = isTitleValid && !isTitleUsed

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add New $noteType Note") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    isError = (!isTitleValid && title.isNotEmpty()) || isTitleUsed
                )
                if (!isTitleValid && title.isNotEmpty()) {
                    Text("Title must be at least 3 characters and start with a letter.", color = MaterialTheme.colorScheme.secondary)
                } else if (isTitleUsed) {
                    Text("Title name is already used!", color = MaterialTheme.colorScheme.secondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it }
                    )
                    Text("Set Private Mode")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onNoteAdded(
                        NoteModel(
                            type = noteType,
                            title = title,
                            content = "",
                            isPublic = !isPrivate,
                            filePath = null,
                            createdDate = currentDateTime,
                            modifiedDate = currentDateTime
                        )
                    )
                    onDismiss()
                },
                enabled = isCreateButtonEnabled
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddNoteFloatingActionButton(onAddNoteTypeSelected: (String) -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
        .zIndex(1f), contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { showMenu = !showMenu },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Note")
        }
        if (showMenu) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 72.dp)
            ) {
                NoteTypeButton("Text") { onAddNoteTypeSelected("Text"); showMenu = false }
                NoteTypeButton("Voice") { onAddNoteTypeSelected("Voice"); showMenu = false }
                NoteTypeButton("Image") { onAddNoteTypeSelected("Image"); showMenu = false }
            }
        }
    }
}

@Composable
fun NoteTypeButton(type: String, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(16.dp)
            .zIndex(1f)
    ) {
        Text(type, style = MaterialTheme.typography.labelMedium)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordDialog(
    onDismiss: () -> Unit,
    onPasswordVerified: () -> Unit,
    userViewModel: UserViewModel
) {
    var password by remember { mutableStateOf("") }
    var isPasswordIncorrect by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Enter Password") },
        text = {
            Column {
                if (isPasswordIncorrect) {
                    Text("Incorrect password, try again.", color = MaterialTheme.colorScheme.error)
                }
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    trailingIcon = {
                        val image = if (isPasswordVisible)
                            R.drawable.visibility_icon
                        else
                            R.drawable.visibility_off_icon
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }, modifier = Modifier.size(20.dp)) {
                            Icon(painterResource(image), "Toggle password visibility")
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (userViewModel.verifyPassword(password)) {
                        onPasswordVerified()
                    } else {
                        isPasswordIncorrect = true
                    }
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}



