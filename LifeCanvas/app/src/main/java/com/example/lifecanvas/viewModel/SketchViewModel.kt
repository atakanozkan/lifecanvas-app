package com.example.lifecanvas.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifecanvas.model.NoteModel
import com.example.lifecanvas.model.SketchModel
import com.example.lifecanvas.repository.SketchRepository
import kotlinx.coroutines.launch
import java.util.Date

class SketchViewModel(private val repository: SketchRepository) : ViewModel() {
    fun getAllSketches(): LiveData<List<SketchModel>>{
        val liveData = MutableLiveData<List<SketchModel>>()
        viewModelScope.launch {
            liveData.value = repository.getAllSketches()
        }
        return liveData
    }

    fun get(id: Int): LiveData<SketchModel> {
        val liveData = MutableLiveData<SketchModel>()
        viewModelScope.launch {
            liveData.value = repository.get(id)
        }
        return liveData
    }

    fun insert(sketch: SketchModel) = viewModelScope.launch {
        repository.insert(sketch)
    }

    fun update(sketch: SketchModel) = viewModelScope.launch {
        sketch.modifiedDate = Date()
        repository.update(sketch)
    }

    fun delete(sketch: SketchModel) = viewModelScope.launch {
        repository.delete(sketch)
    }

    fun isTitleUsed(title: String, excludeNoteId: Int? = null): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        viewModelScope.launch {
            liveData.value = repository.isTitleUsed(title, excludeNoteId) > 0
        }
        return liveData
    }

    fun searchSketchesWithFilters(
        searchText: String,
        createdDateStart: Date? = null,
        createdDateEnd: Date? = null,
        modifiedDateStart: Date? = null,
        modifiedDateEnd: Date? = null
    ) : LiveData<List<SketchModel>>{
        val liveData = MutableLiveData<List<SketchModel>>()
        val searchQuery = if (searchText.isBlank()) "%" else "%$searchText%"
        viewModelScope.launch {
            liveData.value = repository.searchSketches(searchQuery,
                createdDateStart,createdDateEnd,modifiedDateStart, modifiedDateEnd)
        }
        return liveData
    }
}