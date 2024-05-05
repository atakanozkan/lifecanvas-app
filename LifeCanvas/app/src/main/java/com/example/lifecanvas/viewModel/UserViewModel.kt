package com.example.lifecanvas.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.lifecanvas.model.UserModel
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

class UserViewModel : ViewModel() {
    private var userModel: UserModel = UserModel("", "", "",ByteArray(16),false)
    @RequiresApi(Build.VERSION_CODES.O)
    fun initUser(firstName: String, lastName: String, password: String,isDarkThemeOn: Boolean) {
        val salt = generateSalt()
        userModel = UserModel(
            firstName = firstName,
            lastName = lastName,
            passwordHash = hashPassword(password, salt),
            salt = salt,
            isDarkThemeOn =isDarkThemeOn
        )
    }

    fun getFirstName(): String{
        return userModel.firstName
    }

    fun updateFullName(firstName: String, lastName: String) {
        userModel.firstName = firstName
        userModel.lastName = lastName
    }

    fun updateUserThemePreference(isDarkThemeOn: Boolean){
        userModel.isDarkThemeOn = isDarkThemeOn
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updatePassword(newPassword: String) {
        val newSalt = generateSalt()
        userModel.salt = newSalt
        userModel.passwordHash = hashPassword(newPassword, newSalt)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun verifyPassword(inputPassword: String): Boolean {
        return userModel.passwordHash == hashPassword(inputPassword, userModel.salt)
    }

    private fun generateSalt(): ByteArray {
        val sr = SecureRandom.getInstance("SHA1PRNG")
        val salt = ByteArray(16)
        sr.nextBytes(salt)
        return salt
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun hashPassword(password: String, salt: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val hashedPassword = md.digest(password.toByteArray())
        return Base64.getEncoder().encodeToString(hashedPassword)
    }

    fun getUserModel (): UserModel {
        return userModel
    }

    fun getUserDarkThemePreference(): Boolean{
        return userModel.isDarkThemeOn
    }

    fun resetUser(){
        userModel = UserModel("", "", "",ByteArray(16),false)
    }
}
