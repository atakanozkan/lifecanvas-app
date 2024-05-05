package com.example.lifecanvas.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "events")
data class EventModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val startTime: Date,
    val endTime: Date,
    val createdDate: Date,
    val modifiedDate: Date
)
