package com.example.lifecanvas.model

data class UserModel(
    var firstName: String,
    var lastName: String,
    var passwordHash: String,
    var salt: ByteArray,
    var isDarkThemeOn: Boolean,
)
