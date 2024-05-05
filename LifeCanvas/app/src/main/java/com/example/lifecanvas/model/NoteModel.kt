package com.example.lifecanvas.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class NoteModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val title: String,
    var content: String,
    val isPublic: Boolean,
    val filePath: String? = null,
    val createdDate: Date,
    var modifiedDate: Date
    )