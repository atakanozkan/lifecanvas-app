package com.example.lifecanvas.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "sketches")
data class SketchModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val filePath: String? = null,
    val createdDate: Date,
    var modifiedDate: Date
)
