package com.example.lifecanvas.screen.register

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.lifecanvas.R
import com.example.lifecanvas.UserPreferencesManager
import com.example.lifecanvas.viewModel.UserViewModel

@Composable
fun WelcomeScreen(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Welcome to LifeCanvas", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("registerScreen"); }) {
            Text(text = "Start")
        }
    }
}

@Composable
fun RegisterScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    userPreferencesManager: UserPreferencesManager,
    context: Context
) {
    var currentStep by remember { mutableIntStateOf(1) }
    val maxSteps = 3
    val progress = (currentStep.toFloat() / maxSteps.toFloat())

    LaunchedEffect(currentStep) {
        if (currentStep > maxSteps) {
            saveProfileAndNavigate(userViewModel, userPreferencesManager, context, navController)
        }
    }

    Column {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )
        when (currentStep) {
            1 -> NameSurnameStep(userViewModel) { currentStep = 2 }
            2 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SetPasswordStep(userViewModel) { currentStep = 3 }
            }

            3 -> TermsAndConditionsStep{ currentStep = 4 }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameSurnameStep(userViewModel: UserViewModel, onNextStep: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var isNameValid by remember { mutableStateOf(false) }
    var isSurnameValid by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "LifeCanvas", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(15.dp))
            TextField(
                value = name,
                onValueChange = {
                    name = it
                    isNameValid  = try {
                        it.isNotBlank() && it != ""
                    } catch (e: Exception) { false } },
                label = { Text("Name") }
            )
            TextField(
                value = surname,
                onValueChange = {
                    surname = it
                    isSurnameValid  = try {
                        it.isNotBlank() && it != ""
                    } catch (e: Exception) { false }
                },
                label = { Text("Surname") }
            )
            if (!isNameValid || !isSurnameValid) {
                Text("Please enter your name and surname", color = Color.Gray)
            }
            Button(
                onClick = {
                    if (isNameValid && isSurnameValid) {
                        userViewModel.updateFullName(name, surname)
                        onNextStep()
                    }
                },
                enabled = isNameValid && isSurnameValid
            ) {
                Text("Next")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetPasswordStep(userViewModel: UserViewModel, onNextStep: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isPasswordValid by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Set a Password for Private Notes", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = {
                password = it
                isPasswordValid = password.length >= 6
            },
            label = { Text("Password") },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (isPasswordVisible)
                    R.drawable.visibility_icon
                else
                    R.drawable.visibility_off_icon

                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }, modifier = Modifier.size(20.dp)) {
                    Icon(painterResource(image), "Toggle password visibility")
                }
            }
        )
        if (!isPasswordValid) {
            Text("Password must be at least 6 characters", color = Color.Red)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (isPasswordValid) {
                    userViewModel.updatePassword(password)
                    onNextStep()
                }
            },
            enabled = isPasswordValid
        ) {
            Text("Next")
        }
    }
}


@Composable
fun TermsAndConditionsStep(onFinish: () -> Unit ) {
    var isAgreed by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = """
                           Terms and Conditions
                    
                         By using this mobile app, you agree to the following terms:
                    
                         License: You are allowed to download and use this app on your mobile device for personal, non-commercial use. Please follow the app's instructions.
                    
                         What You Can't Do: You cannot copy, modify, or attempt to understand the app's code. You also cannot remove trademarks or share the app with others.
                    
                         Please read this carefully. If you don't agree, do not use the app.
                    """.trimIndent(), color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Checkbox(
            checked = isAgreed,
            onCheckedChange = { isAgreed = it }
        )
        Text(text = "I agree to the Terms and Conditions")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onFinish()},
            enabled = isAgreed
        ) {
            Text("Use the App Now")
        }
    }
}

fun saveProfileAndNavigate(
    userViewModel: UserViewModel,
    userPreferencesManager: UserPreferencesManager,
    context: Context,
    navController: NavHostController
) {
    val userModel = userViewModel.getUserModel()
    userPreferencesManager.saveUser(context, userModel, "Your profile is saved!")
    navController.navigate("mainScreen")
}
