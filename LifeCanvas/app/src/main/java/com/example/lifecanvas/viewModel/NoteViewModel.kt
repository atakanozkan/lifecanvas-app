package com.example.lifecanvas.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifecanvas.helper.deleteBitmapFile
import com.example.lifecanvas.model.NoteModel
import com.example.lifecanvas.repository.NoteRepository
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    fun insert(note: NoteModel) {
        viewModelScope.launch {
            repository.insert(note)
        }
    }

    fun get(noteId: Int): LiveData<NoteModel> {
        val liveData = MutableLiveData<NoteModel>()
        viewModelScope.launch {
            liveData.value = repository.get(noteId)
        }
        return liveData
    }

    fun update(note: NoteModel) {
        viewModelScope.launch {
            note.modifiedDate = Date()
            repository.update(note)
        }
    }

    fun delete(note: NoteModel) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }

    fun isTitleUsed(title: String, excludeNoteId: Int? = null): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        viewModelScope.launch {
            liveData.value = repository.isTitleUsed(title, excludeNoteId) > 0
        }
        return liveData
    }

    fun deleteAllFilesInPath(){

    }

    fun deleteAllNotes() {
        viewModelScope.launch {
            repository.deleteAllNotes()
        }
    }

    fun deleteAllFilesInNotes() {
        viewModelScope.launch {
            val notes = repository.getAllNotes()
            notes.forEach { note ->
                when (note.type) {
                    "Image" -> {
                        note.filePath?.let { deleteBitmapFile(it) }
                    }
                    "Voice" -> {
                        note.filePath?.let { File(it).delete() }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    fun getAllNotes(): LiveData<List<NoteModel>> {
        val liveData = MutableLiveData<List<NoteModel>>()
        viewModelScope.launch {
            liveData.value = repository.getAllNotes()
        }
        return liveData
    }

    fun getPublicNotes(): LiveData<List<NoteModel>> {
        val liveData = MutableLiveData<List<NoteModel>>()
        viewModelScope.launch {
            liveData.value = repository.getPublicNotes()
        }
        return liveData
    }

    fun getPrivateNotes(): LiveData<List<NoteModel>> {
        val liveData = MutableLiveData<List<NoteModel>>()
        viewModelScope.launch {
            liveData.value = repository.getPrivateNotes()
        }
        return liveData
    }

    fun searchNotesWithFilters(
        searchText: String,
        isPublic: Boolean? = null,
        noteType: String? = null,
        createdDateStart: Date? = null,
        createdDateEnd: Date? = null,
        modifiedDateStart: Date? = null,
        modifiedDateEnd: Date? = null
    ) : LiveData<List<NoteModel>>{
        val liveData = MutableLiveData<List<NoteModel>>()
        val searchQuery = if (searchText.isBlank()) "%" else "%$searchText%"
        viewModelScope.launch {
            liveData.value = repository.searchNotes(searchQuery, isPublic, noteType,
                createdDateStart,createdDateEnd,modifiedDateStart, modifiedDateEnd)
        }
        return liveData
    }

}
