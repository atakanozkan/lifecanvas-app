package com.example.lifecanvas.dao

import androidx.room.*
import androidx.lifecycle.LiveData
import com.example.lifecanvas.model.EventModel
import java.util.Date

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): List<EventModel>

    @Query("SELECT * FROM events WHERE id = :eventId")
    fun getEventById(eventId: Int): EventModel

    @Query("SELECT * FROM events WHERE startTime >= :startTime AND endTime <= :endTime")
    fun getEventsByDateInterval(startTime: Date, endTime: Date): LiveData<List<EventModel>>

    @Query("SELECT COUNT(*) FROM events WHERE  startTime > :selectedDateBefore AND startTime < :selectedDateAfter")
    fun getEventStartCountForDay(selectedDateBefore: Date,selectedDateAfter: Date): LiveData<Int>

    @Query("SELECT COUNT(*) FROM events WHERE endTime < :selectedDateAfter AND endTime > :selectedDateBefore")
    fun getEventEndCountForDay(selectedDateBefore: Date,selectedDateAfter: Date): LiveData<Int>

    @Query("SELECT * FROM events WHERE  startTime > :selectedDateBefore AND startTime < :selectedDateAfter")
    fun getEventsStartForDay(selectedDateBefore: Date,selectedDateAfter: Date): LiveData<List<EventModel>>

    @Query("SELECT * FROM events WHERE endTime < :selectedDateAfter AND endTime > :selectedDateBefore")
    fun getEventsEndForDay(selectedDateBefore: Date,selectedDateAfter: Date): LiveData<List<EventModel>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: EventModel)

    @Update
    fun update(event: EventModel)

    @Delete
    fun delete(event: EventModel)
}
