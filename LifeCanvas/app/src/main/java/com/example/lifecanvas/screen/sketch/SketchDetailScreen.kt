package com.example.lifecanvas.screen.sketch

import android.content.Context
import android.graphics.Bitmap
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.lifecanvas.R
import com.example.lifecanvas.helper.*
import com.example.lifecanvas.helper.loadImageBitmap
import com.example.lifecanvas.model.DrawingPathModel
import com.example.lifecanvas.model.NoteModel
import com.example.lifecanvas.model.SketchModel
import com.example.lifecanvas.screen.filter.isValidTitle
import com.example.lifecanvas.screen.note.EditNoteDialog
import com.example.lifecanvas.viewModel.SketchViewModel
import java.io.File
import java.util.Date

@Composable
fun SketchDetailScreen(
    sketchViewModel: SketchViewModel,
    sketchId: Int,
    navController: NavController,
    context: Context
) {
    val sketch by sketchViewModel.get(sketchId).observeAsState()
    var paths by remember { mutableStateOf<List<DrawingPathModel>>(listOf()) }
    var currentStrokeSize by remember { mutableFloatStateOf(5f) }
    var currentEraserUse by remember { mutableStateOf(false) }
    val currentColor = remember { mutableStateOf(Color.Black) }
    val backgroundBitmap = sketch?.filePath?.let { path ->
        loadImageBitmap(path)
    }
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    var showEditSketchDialog by remember { mutableStateOf(false) }


    Box {
        Column(modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
        ) {
            SketchDetailContent(
                sketch = sketch,
                onBack = {
                    if (sketch?.filePath.isNullOrEmpty()) {
                        val bitmap = createBitmapFromPaths(paths, screenWidth.toInt(), screenHeight.toInt())
                        val savedPath = sketch?.let { saveBitmapToFile(bitmap, it.title, context) }
                        savedPath?.let {
                            val updatedSketch = sketch!!.copy(filePath = it)
                            sketchViewModel.update(updatedSketch)
                            Toast.makeText(context, "Sketch saved!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val existingBitmap = loadImageBitmap(sketch?.filePath)
                        val bitmap = createBitmapFromPaths(paths, screenWidth.toInt(), screenHeight.toInt(),existingBitmap)
                        sketch!!.filePath?.let { File(it).name }
                            ?.let { saveBitmapToFile(bitmap, it, context) }
                        Toast.makeText(context, "Sketch updated!", Toast.LENGTH_SHORT).show()
                    }
                    navController.navigate("sketchesScreen") },
                onDelete = {

                    sketch?.let {
                        sketch!!.filePath?.let { it1 -> deleteBitmapFile(it1) }
                        sketchViewModel.delete(it)
                        navController.navigate("sketchesScreen")
                    }
                           }
                , onEdit = {
                    showEditSketchDialog = true
                }
            )

        }
        Spacer(modifier = Modifier.height(15.dp))


        DrawingCanvas(
            paths = paths,
            backgroundBitmap = backgroundBitmap,
            onPathAdded = { path -> paths = paths + path },
            currentColor = currentColor.value,
            modifier = Modifier.matchParentSize(),
            width = screenWidth.toInt(),
            height = screenHeight.toInt(),
            stroke = currentStrokeSize
        )
        Column(modifier = Modifier.align(Alignment.BottomCenter).
            fillMaxWidth()
            .zIndex(1f)) {
            CanvasFeatures(
                onSizeSelected = { size ->
                    currentStrokeSize = size
                    currentEraserUse = false
                },
                onColorSelected = { color ->
                    currentColor.value = color
                    currentEraserUse = false
                },
                onEraserSelected = { isEraser ->
                    currentEraserUse = isEraser
                    if (isEraser) {
                        currentColor.value = Color.White
                    }
                }
            )
        }
        if (showEditSketchDialog) {
            sketch?.let {
                EditSketchDialog(
                    sketch = it,
                    sketchViewModel = sketchViewModel,
                    onDismiss = { showEditSketchDialog = false },
                    onSketchEdited = { editedSketch ->
                        sketchViewModel.update(editedSketch)
                        showEditSketchDialog = false
                        Toast.makeText(context, "Sketch title is modified!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SketchDetailContent(
    sketch: SketchModel?,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                if (sketch != null) {
                    Text("Title: ${sketch.title}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
                else{
                    Text("Failed to open the sketch!", color = Color.Red)
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(
                    onClick = {onEdit()}) {
                    Icon(Icons.Default.Create, contentDescription = "Edit Title")
                }
                IconButton(onClick = onDelete) {
                    Icon(painter = painterResource(id = R.drawable.delete_icon), contentDescription = "Delete")
                }
            }
        )
    }
}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawingCanvas(
    paths: List<DrawingPathModel>,
    backgroundBitmap: Bitmap?,
    onPathAdded: (DrawingPathModel) -> Unit,
    currentColor: Color = Color.Black,
    modifier: Modifier,
    width: Int,
    height: Int,
    stroke: Float,
) {
    var currentPath by remember { mutableStateOf(Path()) }
    var drawPoint by remember { mutableStateOf<Offset?>(null) }

    Canvas(modifier = modifier
        .fillMaxSize()
        .pointerInteropFilter { event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    currentPath.moveTo(event.x, event.y)
                    drawPoint = Offset(event.x, event.y)
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    drawPoint?.let {
                        currentPath.lineTo(event.x, event.y)
                        drawPoint = Offset(event.x, event.y)
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    drawPoint = null
                    onPathAdded(DrawingPathModel(currentColor, currentPath,stroke))
                    currentPath = Path()
                    true
                }

                else -> false
            }
        }) {
        backgroundBitmap?.let { bitmap ->
            drawImage(bitmap.asImageBitmap(), dstSize = IntSize(width, height))
        }
        paths.forEach { drawingPath ->
            drawPath(path = drawingPath.path, color = drawingPath.color, style = Stroke(drawingPath.stroke))
        }
        drawPoint?.let {
            drawPath(path = currentPath, color = currentColor, style = Stroke(stroke))
        }
    }
}

@Composable
fun CanvasFeatures(
    onSizeSelected: (Float) -> Unit,
    onColorSelected: (Color) -> Unit,
    onEraserSelected: (Boolean) -> Unit
){
    var sliderPosition by remember { mutableFloatStateOf(5f) }
    val colors = listOf(Color.Black, Color.Red, Color.Green, Color.Blue, Color.Cyan, Color.Magenta,
        Color.Yellow,Color.DarkGray,Color.Gray)
    Column {
        Slider(
            value = sliderPosition,
            onValueChange = { newValue ->
                sliderPosition = newValue
                onSizeSelected(newValue)
            },
            valueRange = 1f..30f
        )
        Row {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color)
                        .clickable { onColorSelected(color) },
                )
            }
            IconButton(onClick = { onEraserSelected(true) }) {
                Icon(painterResource(R.drawable.eraser_icon),"Eraser")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSketchDialog(
    sketch: SketchModel,
    sketchViewModel: SketchViewModel,
    onDismiss: () -> Unit,
    onSketchEdited: (SketchModel) -> Unit
) {
    var title by remember { mutableStateOf(sketch.title) }
    val currentDateTime = remember { Date() }
    val isTitleValid = remember(title) { isValidTitle(title) }
    val isTitleUsedLiveData = sketchViewModel.isTitleUsed(title,sketch.id)
    val isTitleUsed by isTitleUsedLiveData.observeAsState(initial = false)
    val isEditButtonEnabled = isTitleValid && !isTitleUsed ||
            (isTitleValid && isTitleUsed && title == sketch.title)

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit Sketch Title") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    isError = (!isTitleValid && title.isNotEmpty()) || (isTitleUsed && title != sketch.title)
                )
                if (!isTitleValid && title.isNotEmpty()) {
                    Text("Title must be at least 3 characters and start with a letter.", color = MaterialTheme.colorScheme.secondary)
                } else if (isTitleUsed && title != sketch.title) {
                    Text("Title name is already used!", color = MaterialTheme.colorScheme.secondary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSketchEdited(
                        sketch.copy(
                            title = title,
                            filePath =sketch.filePath,
                            createdDate = sketch.createdDate,
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