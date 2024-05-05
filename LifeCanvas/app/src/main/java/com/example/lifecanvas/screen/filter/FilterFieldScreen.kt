package com.example.lifecanvas.screen.filter

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onSearchButtonClicked: () -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        label = { Text("Search Notes") },
        trailingIcon = {
            IconButton(
                onClick = { onSearchButtonClicked() },
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
            }
        }
    )
}
@Composable
fun ExpandableFilterPanel(
    onResetFilters: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(if (expanded) "Hide Filters" else "Advanced Filter")
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(8.dp), content = content)
        }

        if (expanded) {
            Button(
                onClick = onResetFilters,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Reset")
            }
        }
    }
}


@Composable
fun PublicPrivateToggleFilter(isPublicFilter: Boolean?, onValueChange: (Boolean?) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Private")
        Switch(
            checked = isPublicFilter ?: false,
            onCheckedChange = onValueChange
        )
        Text("Public")
    }
}
@SuppressLint("SimpleDateFormat")
@Composable
fun DatePicker(
    label: String,
    selectedDate: Date?,
    icon: ImageVector,
    onDateSelected: (Date?) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    selectedDate?.let {
        calendar.time = it
    }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(context, { _, year, month, dayOfMonth ->
        val newDate = Calendar.getInstance()
        newDate.set(year, month, dayOfMonth)
        onDateSelected(newDate.time)
    }, year, month, day)

    var formatted =selectedDate?.toString() ?: label
    if(selectedDate!= null){
        val formatter = SimpleDateFormat("yyyy.MM.dd")
        formatted = formatter.format(selectedDate)
    }


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { datePickerDialog.show() }
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = formatted
        )
    }
}

@Composable
fun NoteTypeDropdownFilter(noteTypeFilter: String?, onTypeSelected: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val noteTypes = listOf("All", "Text", "Voice", "Image")

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        Text(
            text = noteTypeFilter ?: "Select Type",
            modifier = Modifier
                .clickable { expanded = true }
                .padding(16.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            noteTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        expanded = false
                        onTypeSelected(if (type == "All") null else type)
                    }
                )
            }
        }
    }
}

fun isValidTitle(title: String): Boolean {
    val pattern = Regex("^[A-Za-z][A-Za-z0-9\\s]{2,}$")
    return pattern.matches(title)
}