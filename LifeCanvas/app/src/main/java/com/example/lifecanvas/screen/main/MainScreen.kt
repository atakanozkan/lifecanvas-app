package com.example.lifecanvas.screen.main

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.lifecanvas.R
import com.example.lifecanvas.UserPreferencesManager
import com.example.lifecanvas.viewModel.NoteViewModel
import com.example.lifecanvas.viewModel.ThemeViewModel
import com.example.lifecanvas.viewModel.UserViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController,
               userViewModel: UserViewModel,
               noteViewModel: NoteViewModel,
               userPreferencesManager: UserPreferencesManager,
               themeViewModel: ThemeViewModel,
               context: Context) {
    var showMenu by remember { mutableStateOf(false) }
    var agreeAlertDialog by remember { mutableStateOf(false) }
    var changePasswordDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Welcome ${userViewModel.getFirstName()},") },
                actions = {
                    DarkModeSwitch(themeViewModel,
                        onThemeChanged = {
                            userPreferencesManager.saveUser(context,userViewModel.getUserModel(),"")
                        }
                        )
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Change Password") },
                                onClick = {
                                    showMenu = false
                                    changePasswordDialog =true
                                })
                            DropdownMenuItem(
                                text = { Text("Delete Account") },
                                onClick = {
                                    showMenu = false
                                    agreeAlertDialog =true
                                })
                        }
                    }

                }
            )
        }
    ) {
            innerPadding ->
        Column(modifier = Modifier.padding(innerPadding),verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Spacer(modifier = Modifier.height(40.dp))
            FeatureCard(featureName = "Notes", onClick = { navController.navigate("notesScreen") })
            FeatureCard(featureName = "Calendar and Events", onClick = { navController.navigate("calendarScreen")})
            FeatureCard(featureName = "Sketch", onClick = { navController.navigate("sketchesScreen")})
            if(agreeAlertDialog && !changePasswordDialog){
                ShowAgreeAlert(
                    onDismiss = {agreeAlertDialog = false},
                    onClickAgree = {
                        agreeAlertDialog = false
                        DeleteUserAndContents(userViewModel,userPreferencesManager,noteViewModel,context)
                        navController.navigate("welcomeScreen")
                    }

                )
            }
            if (changePasswordDialog && !agreeAlertDialog){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ChangePasswordDialog(
                        userViewModel = userViewModel,
                        onDismiss = { changePasswordDialog = false },
                        onPasswordChanged =
                        {userPreferencesManager.saveUser(context,userViewModel.getUserModel(),"New Password Saved!")}
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureCard(featureName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = featureName, style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun ShowAgreeAlert(
    onDismiss: () -> Unit,
    onClickAgree: () -> Unit){

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Are you sure") },
        text = {
            Text(text = "You are deleting you account with your saved contents, are you sure?")
        },
        confirmButton = {
            Button(
                onClick = {
                    onClickAgree()
                    onDismiss()
                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("No")
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordDialog(
    userViewModel: UserViewModel,
    onDismiss: () -> Unit,
    onPasswordChanged: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPasswordError by remember { mutableStateOf(newPassword != confirmPassword) }
    var showConfirmationError by remember { mutableStateOf(false) }
    var showCurrentPasswordError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Change Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                if (showPasswordError) {
                    Text("Passwords do not match", color = Color.Red)
                }
                if (showCurrentPasswordError) {
                    Text("Current password is incorrect", color = Color.Red)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    showPasswordError = newPassword != confirmPassword
                    showCurrentPasswordError = !userViewModel.verifyPassword(currentPassword)
                    if (!showPasswordError && !showCurrentPasswordError && newPassword.isNotEmpty()) {
                        userViewModel.updatePassword(newPassword)
                        onPasswordChanged()
                        onDismiss()
                    }
                }
            ) {
                Text("Change")
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
fun DarkModeSwitch(
    themeViewModel: ThemeViewModel,
    onThemeChanged: () -> Unit
) {
    val isDarkThemeEnabled by themeViewModel.darkThemeEnabled.observeAsState(initial = false)
    Icon(painterResource(R.drawable.dark_theme_icon), contentDescription = "Dark Theme",modifier = Modifier.size(40.dp))
    Spacer(modifier = Modifier.width(10.dp))
    Switch(
        checked = isDarkThemeEnabled,
        onCheckedChange = { isEnabled ->
            themeViewModel.toggleDarkTheme(isEnabled)
            onThemeChanged()
        }
    )
}

fun DeleteUserAndContents(
    userViewModel: UserViewModel,
    userPreferencesManager: UserPreferencesManager,
    noteViewModel: NoteViewModel,
    context: Context){
    userViewModel.resetUser()
    userPreferencesManager.deleteUser(context)
    noteViewModel.deleteAllFilesInNotes()
    noteViewModel.deleteAllNotes()

}

