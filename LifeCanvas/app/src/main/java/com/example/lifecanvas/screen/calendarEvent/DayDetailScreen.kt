package com.example.lifecanvas.screen.calendarEvent

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifecanvas.api.getHolidayForDay
import com.example.lifecanvas.model.EventModel
import com.example.lifecanvas.model.HolidayModel
import com.example.lifecanvas.viewModel.EventViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayDetailScreen(day: LocalDate, eventViewModel: EventViewModel, navController: NavController,context: Context) {
    var showAddEventDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var holidays by remember { mutableStateOf<List<HolidayModel>>(emptyList()) }
    val startOfDay = day.atStartOfDay(ZoneId.systemDefault())
    val endOfDay = day.atTime(23, 59, 59, 999_999_999).atZone(ZoneId.systemDefault())
    val startDate = Date.from(startOfDay.toInstant())
    val endDate = Date.from(endOfDay.toInstant())
    val eventsStartDay by eventViewModel.getEventsStartForDay(startDate, endDate).observeAsState(initial = emptyList())
    val eventsEndDay by eventViewModel.getEventsEndForDay(startDate, endDate).observeAsState(initial = emptyList())

    LaunchedEffect(day) {
        coroutineScope.launch {
            holidays = getHolidayForDay(context, startDate)
        }
    }

    Column {
        TopAppBar(
            title = {Text(day.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")))},
            navigationIcon = {
                IconButton(onClick = { navController.navigate("calendarScreen") }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { showAddEventDialog = true }) {
                    Icon(Icons.Filled.Add, "Add Event")
                }
            }
        )

        LazyColumn {
            items(holidays) { holiday ->
                HolidayCard(holiday)
            }
            items(eventsStartDay) { event ->
                EventCard(event = event, label = "Event Start",navController)
            }
            items(eventsEndDay) { event ->
                EventCard(event = event, label = "Event End",navController)
            }
        }

        if (showAddEventDialog) {
            AddEventDialog(
                onDismiss = { showAddEventDialog = false },
                onEventAdded = { event ->
                    eventViewModel.insert(event)
                    showAddEventDialog = false
                    Toast.makeText(context, "Event is updated!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun EventCard(event: EventModel, label: String,navController: NavController) {
    Card(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Event Title: ${event.title}", style = MaterialTheme.typography.bodyLarge, color =  MaterialTheme.colorScheme.primary)
            Text(text = "Description: " + event.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Label: $label" ,style = MaterialTheme.typography.bodyMedium,color = MaterialTheme.colorScheme.secondary)
            Text(text = "Start Date: ${event.startTime}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "End Date: ${event.endTime}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            IconButton(onClick = { navController.navigate("eventEditScreen/${event.id}") }, modifier = Modifier.fillMaxSize()) {
                Icon(
                    Icons.Default.Create,
                    contentDescription = "Edit",
                    tint = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun HolidayCard(holiday: HolidayModel) {
    Card(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Holiday: ${holiday.name}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            Text(text = "Local Name: ${holiday.localName}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Date: ${holiday.date}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}