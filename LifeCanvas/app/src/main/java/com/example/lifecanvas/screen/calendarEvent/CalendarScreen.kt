package com.example.lifecanvas.screen.calendarEvent

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifecanvas.api.fetchHolidaysByYear
import com.example.lifecanvas.model.EventModel
import com.example.lifecanvas.model.HolidayModel
import com.example.lifecanvas.screen.filter.DatePicker
import com.example.lifecanvas.screen.filter.isValidTitle
import com.example.lifecanvas.viewModel.EventViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(eventViewModel: EventViewModel,navController: NavController,context: Context) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val currentYear = currentMonth.year.toString()
    val daysInMonth = getDaysInMonth(currentMonth)
    var showAddEventDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var holidays by remember { mutableStateOf<List<HolidayModel>>(emptyList()) }
    
    LaunchedEffect(currentYear) {
        coroutineScope.launch {
            holidays = fetchHolidaysByYear(context,currentYear)
        }
    }

    Column {
        TopAppBar(
            title = { Text(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))) },
            navigationIcon = {
                IconButton(onClick = { navController.navigate("mainScreen") }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(Icons.Filled.ArrowBack, "Previous Month")
                }
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.Filled.ArrowForward, "Next Month")
                }
                IconButton(onClick = {showAddEventDialog = true } ) {
                    Icon(Icons.Filled.Add, "Add Event")
                }
            }
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(daysInMonth.size) { index ->
                val day = daysInMonth[index]

                val startOfDay = day.atStartOfDay(ZoneId.systemDefault())
                val startDate = Date.from(startOfDay.toInstant())
                val endOfDay = day.atTime(23, 59, 59, 999_999_999).atZone(ZoneId.systemDefault())
                val endDate = Date.from(endOfDay.toInstant())

                val eventStartCount by eventViewModel.getEventStartCountForDay(startDate,endDate).observeAsState(0)
                val eventEndCount by eventViewModel.getEventEndCountForDay(startDate,endDate).observeAsState(0)
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = formatter.format(startDate)

                val holidayCount = holidays.count { it.date == formattedDate }


                val eventCount = eventStartCount + eventEndCount + holidayCount

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DayCard(day = daysInMonth[index], eventCount = eventCount,
                        onClick = { navController.navigate("dayDetailScreen/$day") })
                }
            }
        }
        if (showAddEventDialog) {
            AddEventDialog(
                onDismiss = { showAddEventDialog = false },
                onEventAdded = {
                    event ->
                    eventViewModel.insert(event)
                    showAddEventDialog = false
                    Toast.makeText(context, "Event is created!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayCard(day: LocalDate,eventCount:Int,onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .height(75.dp)
            .clickable(onClick = onClick)
    ) {
        Column (modifier = Modifier.align(Alignment.CenterHorizontally)){
            Box(contentAlignment = Alignment.Center, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = day.dayOfMonth.toString(), modifier = Modifier.align(Alignment.Center))

            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                if (eventCount > 0) {

                    Text(text = "+$eventCount", color= MaterialTheme.colorScheme.primary ,
                        style = MaterialTheme.typography.labelMedium,modifier = Modifier.align(Alignment.Center))
                }

            }
        }


    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDaysInMonth(month: YearMonth): List<LocalDate> {
    val daysInMonth = mutableListOf<LocalDate>()
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, month.year)
    calendar.set(Calendar.MONTH, month.monthValue - 1)
    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    for (day in 1..maxDay) {
        daysInMonth.add(LocalDate.of(month.year, month.month, day))
    }

    return daysInMonth
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onEventAdded: (EventModel) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val isTitleValid = remember(title) { isValidTitle(title) }
    var eventStartDate by remember { mutableStateOf<Date?>(null) }
    var eventEndDate by remember { mutableStateOf<Date?>(null) }
    val currentDateTime = remember { Date() }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add New Event") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Title") },
                    isError = !isTitleValid && title.isNotEmpty()
                )
                if (!isTitleValid && title.isNotEmpty()) {
                    Text("Title must be at least 3 characters and start with a letter.", color = MaterialTheme.colorScheme.secondary)
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                DatePicker(label = "Start Date", selectedDate = eventStartDate, icon = Icons.Default.DateRange){ newDate ->
                    eventStartDate = newDate
                }
                DatePicker(label = "End Date" , selectedDate = eventEndDate, icon = Icons.Default.DateRange){ newDate ->
                    eventEndDate = newDate
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    eventStartDate?.let {
                        eventEndDate?.let { it1 ->
                            EventModel(
                                title = title,
                                description = description,
                                createdDate = currentDateTime,
                                modifiedDate = currentDateTime,
                                startTime = it,
                                endTime = it1
                            )
                        }
                    }?.let {
                        onEventAdded(
                            it
                        )
                    }
                    onDismiss()
                },
                enabled = isTitleValid
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
