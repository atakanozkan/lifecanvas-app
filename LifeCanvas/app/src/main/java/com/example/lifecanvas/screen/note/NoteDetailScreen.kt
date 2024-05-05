package com.example.lifecanvas.screen.note

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifecanvas.R
import com.example.lifecanvas.audio.AudioRecorder
import com.example.lifecanvas.helper.deleteBitmapFile
import com.example.lifecanvas.helper.loadImageBitmap
import com.example.lifecanvas.model.NoteModel
import com.example.lifecanvas.screen.filter.isValidTitle
import com.example.lifecanvas.viewModel.NoteViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(noteViewModel: NoteViewModel, noteId: Int, navController: NavController,context: Context) {
    val noteModel by noteViewModel.get(noteId).observeAsState()
    var showEditNoteDialog by remember { mutableStateOf(false) }
    if(noteModel == null){
        Text(text = "Note Detail Not Found!")
        return
    }

    noteModel?.let { note ->
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Note Detail") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate("notesScreen")
                        }
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick ={
                            showEditNoteDialog = true
                        }) {
                        Icon(Icons.Default.Create, contentDescription = "Edit Title")
                    }
                    IconButton(
                        onClick ={
                            if(note.type == "Image"){
                                note.filePath?.let { deleteBitmapFile(it) }
                                Toast.makeText(context, "Image note is deleted!", Toast.LENGTH_SHORT).show()
                            }
                            else if(note.type == "Voice"){
                                File(note.filePath).delete()
                                Toast.makeText(context, "Voice note is deleted!", Toast.LENGTH_SHORT).show()
                            }
                            else
                            {
                                Toast.makeText(context, "Text note is deleted!", Toast.LENGTH_SHORT).show()
                            }
                            noteViewModel.delete(note)
                            navController.navigate("notesScreen")
                        }) {
                        Icon(painter = painterResource(id = R.drawable.delete_icon), contentDescription = "Delete")
                    }
                }
            )

            when (note.type) {
                "Text" -> TextFieldNoteScreen(
                    noteContent = note.content,
                    onContentChange = { updatedContent ->
                        noteViewModel.update(note.copy(content = updatedContent))
                    }
                )
                "Voice" -> VoiceRecordNoteScreen(noteViewModel, note, navController)
                "Image" -> ImageNoteScreen(noteViewModel, note)
                else -> Text("Unknown note type, please go back!")
            }

            if (showEditNoteDialog) {
                EditNoteDialog(
                    note = note,
                    noteViewModel =noteViewModel,
                    onDismiss = { showEditNoteDialog = false },
                    onNoteEdited = { editedNote ->
                        noteViewModel.update(editedNote)
                        showEditNoteDialog = false
                        Toast.makeText(context, "Note is modified!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldNoteScreen(noteContent: String, onContentChange: (String) -> Unit) {
    var text by remember { mutableStateOf(noteContent) }

    Column(modifier = Modifier.imePadding()) {
            OutlinedTextField(
                value = text,
                onValueChange = { updatedText ->
                    text = updatedText
                    onContentChange(updatedText)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .fillMaxSize(),
                label = { Text("Note Content") }
            )
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VoiceRecordNoteScreen(noteViewModel: NoteViewModel, note: NoteModel, navController: NavController) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    val coroutineScope = rememberCoroutineScope()

    when {
        permissionsState.allPermissionsGranted -> {
            VoiceRecorderUI(noteViewModel, note,navController)
        }
        permissionsState.shouldShowRationale || !permissionsState.allPermissionsGranted -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Permission Required") },
                text = { Text("This app needs access to your microphone and storage to record audio.") },
                confirmButton = {
                    Button(onClick = {
                        coroutineScope.launch {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    }) {
                        Text("OK")
                    }
                }
            )
        }
        else -> {
            Text("Permissions were not granted. You cannot record audio without permissions!")
        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun VoiceRecorderUI(noteViewModel: NoteViewModel, note: NoteModel, navController: NavController) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var finishedRecord by remember {mutableStateOf(!note.filePath.isNullOrEmpty())}
    var timerValue by remember { mutableIntStateOf(0) }
    var playbackTimerValue by remember { mutableIntStateOf(0) }
    val audioRecorder = remember { AudioRecorder(context, "NOTES_${note.id}.amr").apply {
        setOnPlaybackCompleteListener {
            isPlaying = false
        }
    } }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = isRecording) {
        if (isRecording) {
            timerValue = 0
            while (isRecording) {
                delay(1000)
                timerValue++
            }
        }
    }
    LaunchedEffect(key1 = isPlaying) {
        if (isPlaying) {
            playbackTimerValue = 0
            while (isPlaying) {
                delay(1000)
                playbackTimerValue++
            }
        }
    }
    DisposableEffect(key1 = navController) {
        onDispose {
            if (isPlaying) {
                audioRecorder.stopPlaying()
                isPlaying = false
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isRecording) "${timerValue}s" else "${playbackTimerValue}s",
                style = MaterialTheme.typography.headlineLarge
            )
        }
        Spacer(modifier = Modifier.padding(16.dp))
        Row (

            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Button(
                onClick = {
                    if (isRecording) {
                        audioRecorder.stopRecording(noteViewModel, note)
                        isRecording = false
                        finishedRecord = true
                    } else {
                        audioRecorder.startRecording()
                        isRecording = true
                    }
                }
                ,
                enabled = !isPlaying && !finishedRecord
            ) {
                Text(if (isRecording) "Stop" else "Start")
            }
            Button(
                onClick = {
                    isPlaying = if (isPlaying) {
                        audioRecorder.stopPlaying()
                        false
                    } else {
                        audioRecorder.playRecording()
                        true
                    }
                }
                , enabled = !isRecording && finishedRecord
            ) {
                Text(if (isPlaying) "Stop Playing" else "Play")
            }
            Button(
                onClick = {
                    audioRecorder.deleteRecording()
                    isRecording = false
                    isPlaying = false
                    finishedRecord = false
                    timerValue = 0
                    val updatedNote = note.copy(filePath = null)
                    coroutineScope.launch {
                        noteViewModel.update(updatedNote)
                    }
                }
                , enabled = finishedRecord
            ) {
                Text("Delete")
            }
        }
    }
}

@Composable
fun ImageNoteScreen(noteViewModel: NoteViewModel, note: NoteModel) {
    var imageBitmap by remember { mutableStateOf(loadImageBitmap(note.filePath)) }
    val context = LocalContext.current

    val selectImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            val imagePath = imageUri?.let { uriToFilePath(it, context) }
            imagePath?.let {
                noteViewModel.update(note.copy(filePath = it))
                imageBitmap = loadImageBitmap(it)
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        imageBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Note Image",
                modifier = Modifier.fillMaxWidth()
            )
        }
        if(imageBitmap == null){
            ImageUploadSection(
                onImageSelected = {
                    selectImageLauncher.launch(Intent(Intent.ACTION_PICK).apply { type = "image/*" })
                }
            )
        }
        Button(onClick = {
            noteViewModel.update(note.copy(filePath = null))
            imageBitmap = null
        }) {
            Text("Delete Image")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageUploadSection(onImageSelected: () -> Unit) {
    val permissionsState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
    val placeholder = painterResource(id = R.drawable.upload_icon)
    val imageModifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .border(border = BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(4.dp))
        .clickable {
            when (permissionsState.status) {
                PermissionStatus.Granted -> {
                    onImageSelected()
                }

                else -> {
                    permissionsState.launchPermissionRequest()
                }
            }
        }
    Box(modifier = imageModifier, contentAlignment = Alignment.Center) {
            Icon(painter = placeholder, contentDescription = "Upload Placeholder")
    }
    if (permissionsState.status.shouldShowRationale) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Permission Required") },
            text = { Text("This action requires permission to access your device's storage. Please grant permission to continue.") },
            confirmButton = {
                Button(onClick = {
                    permissionsState.launchPermissionRequest()
                }) {
                    Text("OK")
                }
            }
        )
    }
}
fun uriToFilePath(uri: Uri, context: Context): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val index = it.getColumnIndex(MediaStore.Images.Media.DATA)
            return it.getString(index)
        }
    }
    return null
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteDialog(
    note: NoteModel,
    noteViewModel: NoteViewModel,
    onDismiss: () -> Unit,
    onNoteEdited: (NoteModel) -> Unit
) {
    var title by remember { mutableStateOf(note.title) }
    var isPublic by remember { mutableStateOf(note.isPublic) }
    val currentDateTime = remember { Date() }
    val isTitleValid = remember(title) { isValidTitle(title) }
    val isTitleUsedLiveData = noteViewModel.isTitleUsed(title,note.id)
    val isTitleUsed by isTitleUsedLiveData.observeAsState(initial = false)
    val isEditButtonEnabled = isTitleValid && !isTitleUsed ||
            (isTitleValid && isTitleUsed && title == note.title)


    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit Note") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    isError = (!isTitleValid && title.isNotEmpty()) || (isTitleUsed && title != note.title)
                )
                if (!isTitleValid && title.isNotEmpty()) {
                    Text("Title must be at least 3 characters and start with a letter.", color = MaterialTheme.colorScheme.secondary)
                } else if (isTitleUsed && title != note.title) {
                    Text("Title name is already used!", color = MaterialTheme.colorScheme.secondary)
                }
                if(isPublic){
                    Text("Current note visibility is public.", color = MaterialTheme.colorScheme.primary)
                }
                else{
                    Text("Current note visibility is private.", color = MaterialTheme.colorScheme.primary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it }
                    )
                    Text("Change visibility ")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onNoteEdited(
                        note.copy(
                            type = note.type,
                            title = title,
                            content = note.content,
                            isPublic = isPublic,
                            filePath = note.filePath,
                            createdDate = note.createdDate,
                            modifiedDate = currentDateTime
                        )
                    )
                    onDismiss()
                },
                enabled = isEditButtonEnabled
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}