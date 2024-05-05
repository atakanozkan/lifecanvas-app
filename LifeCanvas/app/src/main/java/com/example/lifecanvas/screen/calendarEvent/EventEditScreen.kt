package com.example.lifecanvas.screen.calendarEvent

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.lifecanvas.R
import com.example.lifecanvas.model.EventModel
import com.example.lifecanvas.screen.filter.DatePicker
import com.example.lifecanvas.screen.filter.isValidTitle
import com.example.lifecanvas.viewModel.EventViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditScreen(eventId: Int, eventViewModel: EventViewModel, navController: NavController,context: Context) {
    val event by eventViewModel.getEventById(eventId).observeAsState()
    var title by remember { mutableStateOf(event?.title ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var startTime by remember { mutableStateOf(event?.startTime ?: Date()) }
    var endTime by remember { mutableStateOf(event?.endTime ?: Date()) }
    val isTitleValid = remember(title) { isValidTitle(title) }
    Column {
        TopAppBar(
            title = { Text("Edit Event") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = {
                    if (isTitleValid && endTime.after(startTime)) {
                        val updatedEvent = event?.let {
                            EventModel(
                                id = eventId,
                                title = title,
                                description = description,
                                startTime = startTime,
                                endTime = endTime,
                                createdDate = it.createdDate,
                                modifiedDate = Date()
                            )
                        }
                        if (updatedEvent != null) {
                            eventViewModel.update(updatedEvent)
                            Toast.makeText(context, "Event is updated!", Toast.LENGTH_SHORT).show()
                        }
                        navController.popBackStack()
                    }
                }) {
                    Icon(painterResource(R.drawable.save_icon), contentDescription = "Save")
                }
                IconButton(onClick = {
                    event?.let { eventViewModel.delete(it) }
                    Toast.makeText(context, "Event is deleted!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }) {
                    Icon(painterResource(R.drawable.delete_icon), contentDescription = "Delete")
                }
            }
        )
        Column {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                isError = !isTitleValid
            )
            if (!isTitleValid) {
                Text("Title must be at least 3 characters and start with a letter", color = Color.Red)
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") }
            )

            DatePicker(
                label = "Start Time",
                selectedDate = startTime,
                icon = Icons.Default.DateRange,
                onDateSelected = { newDate -> startTime = newDate ?: startTime }
            )

            DatePicker(
                label = "End Time",
                selectedDate = endTime,
                icon = Icons.Default.DateRange,
                onDateSelected = { newDate -> endTime = newDate ?: endTime }
            )

        }
    }
}
