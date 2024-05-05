package com.example.lifecanvas.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifecanvas.model.EventModel
import com.example.lifecanvas.model.NoteModel
import com.example.lifecanvas.repository.EventRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    fun insert(event: EventModel) = viewModelScope.launch {
        repository.insert(event)
    }

    fun update(event: EventModel) = viewModelScope.launch {
        repository.update(event)
    }

    fun delete(event: EventModel) = viewModelScope.launch {
        repository.delete(event)
    }

    fun getEventById(eventId: Int): LiveData<EventModel> {
        val liveData = MutableLiveData<EventModel>()
        viewModelScope.launch {
            liveData.value = repository.getEventById(eventId)
        }
        return liveData
    }

    fun getAllEvents(eventId: Int): LiveData<List<EventModel>> {
        val liveData = MutableLiveData<List<EventModel>>()
        viewModelScope.launch {
            liveData.value = repository.getAllEvents()
        }
        return liveData
    }


    fun getEventsByDateInterval(startTime: Date, endTime: Date): LiveData<List<EventModel>> {
        return repository.getEventsByDateInterval(startTime, endTime)
    }

    fun getEventStartCountForDay(selectedDateBefore: Date,selectedDateAfter: Date): LiveData<Int> {
        return repository.getEventStartCountForDay(selectedDateBefore,selectedDateAfter)
    }

    fun getEventEndCountForDay(selectedDateBefore: Date,selectedDateAfter: Date): LiveData<Int> {
        return repository.getEventEndCountForDay(selectedDateBefore,selectedDateAfter)
    }

    fun getEventsEndForDay(selectedDateBefore: Date,selectedDateAfter: Date): LiveData<List<EventModel>>{
        return repository.getEventsEndForDay(selectedDateBefore,selectedDateAfter)
    }

    fun getEventsStartForDay(selectedDateBefore: Date,selectedDateAfter: Date): LiveData<List<EventModel>>{
        return repository.getEventsStartForDay(selectedDateBefore,selectedDateAfter)
    }
}