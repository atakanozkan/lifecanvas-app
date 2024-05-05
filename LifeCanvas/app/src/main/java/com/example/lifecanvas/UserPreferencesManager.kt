package com.example.lifecanvas

import android.content.Context
import android.os.Build
import android.widget.Toast
import com.example.lifecanvas.model.UserModel
import com.example.lifecanvas.viewModel.UserViewModel
import com.google.gson.Gson
class UserPreferencesManager {
    fun saveUser(context: Context, userModel: UserModel, saveMessage: String) {
        val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val userJson = gson.toJson(userModel)
        editor.putString("UserData", userJson)
        editor.apply()
        if(saveMessage != ""){
            Toast.makeText(context, saveMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun loadData(userViewModel: UserViewModel, context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val gson = Gson()

        val userJson = sharedPreferences.getString("UserData", null) ?: return false

        val userModel = gson.fromJson(userJson, UserModel::class.java)
        if (userModel != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                userViewModel.initUser(userModel.firstName,userModel.lastName,userModel.passwordHash,userModel.isDarkThemeOn)
            }
            return true
        }
        return false
    }

    fun deleteUser(context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
}
