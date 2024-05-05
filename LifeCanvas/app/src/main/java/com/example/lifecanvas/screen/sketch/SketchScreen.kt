package com.example.lifecanvas.screen.sketch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.example.lifecanvas.model.SketchModel
import com.example.lifecanvas.screen.filter.DatePicker
import com.example.lifecanvas.screen.filter.ExpandableFilterPanel
import com.example.lifecanvas.screen.filter.SearchBar
import com.example.lifecanvas.screen.filter.isValidTitle
import com.example.lifecanvas.viewModel.SketchViewModel
import com.example.lifecanvas.viewModel.UserViewModel
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SketchScreen(sketchViewModel: SketchViewModel, userViewModel: UserViewModel, navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var createdDateStartFilter by remember { mutableStateOf<Date?>(null) }
    var createdDateEndFilter by remember { mutableStateOf<Date?>(null) }
    var modifiedDateStartFilter by remember { mutableStateOf<Date?>(null) }
    var modifiedDateEndFilter by remember { mutableStateOf<Date?>(null) }
    var sketchList by remember { mutableStateOf(sketchViewModel.getAllSketches())}

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("My Sketches") },
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
                    sketchList= sketchViewModel.searchSketchesWithFilters(
                        searchText,
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
                    createdDateStartFilter = null
                    createdDateEndFilter = null
                    modifiedDateStartFilter= null
                    modifiedDateEndFilter= null
                    sketchList = sketchViewModel.getAllSketches()
                }
            ) {
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
            SketchList(sketchList, userViewModel,navController)
        }
        Column(modifier = Modifier.fillMaxSize()) {
            AddSketchFloatingButton {
                showAddNoteDialog = true
            }

            if (showAddNoteDialog) {
                AddSketchDialog(
                    onDismiss = { showAddNoteDialog = false },
                    sketchViewModel= sketchViewModel,
                    onSketchAdded = { sketch ->
                        sketchViewModel.insert(sketch)
                        showAddNoteDialog = false
                        sketchList = sketchViewModel.getAllSketches()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSketchDialog(
    onDismiss: () -> Unit,
    sketchViewModel: SketchViewModel,
    onSketchAdded: (SketchModel) -> Unit
) {
    var title by remember { mutableStateOf("") }
    val currentDateTime = remember { Date() }
    val isTitleValid = remember(title) { isValidTitle(title) }
    val isTitleUsedLiveData = sketchViewModel.isTitleUsed(title)
    val isTitleUsed by isTitleUsedLiveData.observeAsState(initial = false)
    val isCreateButtonEnabled = isTitleValid && !isTitleUsed

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add New Sketch Canvas") },
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSketchAdded(
                        SketchModel(
                            title = title,
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
fun SketchList(sketches: LiveData<List<SketchModel>>, userViewModel: UserViewModel, navController: NavController) {
    val sketchList by sketches.observeAsState(initial = emptyList())
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        content = {
            items(sketchList.size) { index ->
                SketchItem(sketchList[index],userViewModel,navController)
            }
        }
    )
}


@Composable
fun SketchItem(sketch: SketchModel, userViewModel: UserViewModel,navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("sketchDetailScreen/${sketch.id}")
            },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = sketch.title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AddSketchFloatingButton(onAddNoteTypeSelected: ()-> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .zIndex(1f), contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { onAddNoteTypeSelected() },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Sketch")
        }
    }
}