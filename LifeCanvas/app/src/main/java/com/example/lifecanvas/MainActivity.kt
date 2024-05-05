package com.example.lifecanvas

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.lifecanvas.database.AppDatabase
import com.example.lifecanvas.migration.MigrationManager
import com.example.lifecanvas.repository.EventRepository
import com.example.lifecanvas.repository.NoteRepository
import com.example.lifecanvas.repository.SketchRepository
import com.example.lifecanvas.screen.calendarEvent.CalendarScreen
import com.example.lifecanvas.screen.calendarEvent.DayDetailScreen
import com.example.lifecanvas.screen.calendarEvent.EventEditScreen
import com.example.lifecanvas.screen.main.MainScreen
import com.example.lifecanvas.screen.note.NoteDetailScreen
import com.example.lifecanvas.screen.note.NotesScreen
import com.example.lifecanvas.screen.register.RegisterScreen
import com.example.lifecanvas.screen.sketch.SketchDetailScreen
import com.example.lifecanvas.screen.sketch.SketchScreen
import com.example.lifecanvas.screen.register.WelcomeScreen
import com.example.lifecanvas.viewModel.UserViewModel
import com.example.lifecanvas.ui.theme.LifeCanvasTheme
import com.example.lifecanvas.viewModel.EventViewModel
import com.example.lifecanvas.viewModel.NoteViewModel
import com.example.lifecanvas.viewModel.SketchViewModel
import com.example.lifecanvas.viewModel.ThemeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private val userViewModel = UserViewModel()
    private val userPreferencesManager = UserPreferencesManager()
    private var isUserValid by mutableStateOf(false)
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var sketchViewModel: SketchViewModel
    private lateinit var eventViewModel: EventViewModel
    private lateinit var themeViewModel: ThemeViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(this, AppDatabase::class.java,
            "AppDB").addMigrations(MigrationManager.MIGRATION_1_2,MigrationManager.MIGRATION_2_3).allowMainThreadQueries().build()
        val noteRepository = NoteRepository(db.noteDao())
        val sketchRepository = SketchRepository(db.sketchDao())
        val eventRepository = EventRepository(db.eventDao())
        isUserValid = userPreferencesManager.loadData(userViewModel,this)
        noteViewModel = NoteViewModel(noteRepository)
        sketchViewModel = SketchViewModel(sketchRepository)
        eventViewModel = EventViewModel(eventRepository)
        themeViewModel = ThemeViewModel(userViewModel)
        setContent {
            LifeCanvasApp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun LifeCanvasApp(){
        val darkThemeEnabled by themeViewModel.darkThemeEnabled.observeAsState(
            initial = false)
        LifeCanvasTheme(darkTheme = darkThemeEnabled) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                NavigateStartApp(
                    isUserValid = isUserValid,this)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun NavigateStartApp(isUserValid: Boolean, context: Context) {
        val navController = rememberNavController()
        var startDestination = "welcomeScreen"

        if (isUserValid) {
            startDestination = "mainScreen"
        }
        NavHost(navController = navController, startDestination = startDestination) {
            composable("welcomeScreen") { WelcomeScreen(navController) }
            composable("registerScreen") { RegisterScreen(navController, userViewModel,userPreferencesManager,context) }
            composable("mainScreen"){ MainScreen(navController, userViewModel,noteViewModel,userPreferencesManager,themeViewModel,context) }
            composable("notesScreen"){ NotesScreen(noteViewModel,userViewModel,navController) }
            composable("noteDetailScreen/{noteId}") { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull() ?: return@composable
                NoteDetailScreen(noteViewModel, noteId, navController,context)
            }
            composable("sketchesScreen"){ SketchScreen(
                sketchViewModel = sketchViewModel,
                userViewModel = userViewModel,
                navController = navController
            )
            }
            composable("sketchDetailScreen/{sketchId}") { backStackEntry ->
                val sketchId = backStackEntry.arguments?.getString("sketchId")?.toIntOrNull() ?: return@composable
                SketchDetailScreen(sketchViewModel, sketchId, navController, context)
            }
            composable("calendarScreen"){ CalendarScreen(eventViewModel,navController,context) }
            composable("dayDetailScreen/{day}", arguments = listOf(
                navArgument("day") { type = NavType.StringType }
            )) { backStackEntry ->
                val dayString = backStackEntry.arguments?.getString("day")
                val day = dayString?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd")) }

                if (day != null) {
                    DayDetailScreen(day, eventViewModel, navController,context)
                }
            }
            composable(
                "eventEditScreen/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.IntType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getInt("eventId") ?: return@composable
                EventEditScreen(eventId, eventViewModel, navController, context)
            }
        }
    }
}
