package com.example.lifecanvas.repository

import androidx.lifecycle.LiveData
import com.example.lifecanvas.dao.EventDao
import com.example.lifecanvas.model.EventModel
import java.util.Date

class EventRepository(private val eventDao: EventDao) {
    fun getAllEvents(): List<EventModel> = eventDao.getAllEvents()

    fun getEventById(eventId: Int): EventModel = eventDao.getEventById(eventId)

    fun getEventsByDateInterval(startTime: Date, endTime: Date): LiveData<List<EventModel>> {
        return eventDao.getEventsByDateInterval(startTime, endTime)
    }

    fun getEventStartCountForDay(selectedDateBefore: Date,selectedDateAfter: Date): LiveData<Int> {
        return eventDao.getEventStartCountForDay(selectedDateBefore,selectedDateAfter)
    }

    fun getEventEndCountForDay(selectedDateBefore: Date,selectedDateAfter: Date):  LiveData<Int> {
        return eventDao.getEventEndCountForDay(selectedDateBefore,selectedDateAfter)
    }

    fun getEventsEndForDay(selectedDateBefore: Date,selectedDateAfter: Date): LiveData<List<EventModel>>{
        return eventDao.getEventsEndForDay(selectedDateBefore,selectedDateAfter)
    }

    fun getEventsStartForDay(selectedDateBefore: Date,selectedDateAfter: Date): LiveData<List<EventModel>>{
        return eventDao.getEventsStartForDay(selectedDateBefore,selectedDateAfter)
    }

    fun insert(event: EventModel) {
        eventDao.insert(event)
    }

    fun update(event: EventModel) {
        eventDao.update(event)
    }

    fun delete(event: EventModel) {
        eventDao.delete(event)
    }
}
