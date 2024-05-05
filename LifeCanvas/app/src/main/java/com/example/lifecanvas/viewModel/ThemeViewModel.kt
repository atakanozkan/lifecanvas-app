package com.example.lifecanvas.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ThemeViewModel(private val userViewModel: UserViewModel) : ViewModel() {
    private val _darkThemeEnabled = MutableLiveData<Boolean>(userViewModel.getUserDarkThemePreference())
    val darkThemeEnabled: LiveData<Boolean> = _darkThemeEnabled

    fun toggleDarkTheme(isEnabled: Boolean) {
        _darkThemeEnabled.value = isEnabled
        userViewModel.updateUserThemePreference(isEnabled)
    }
}