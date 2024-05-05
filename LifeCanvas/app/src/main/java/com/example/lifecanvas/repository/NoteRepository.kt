package com.example.lifecanvas.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lifecanvas.dao.NoteDao
import com.example.lifecanvas.model.NoteModel
import kotlinx.coroutines.launch
import java.util.Date

class NoteRepository(private val noteDao: NoteDao) {

    fun insert(note: NoteModel) {
        noteDao.insert(note)
    }

    fun get(noteId: Int): NoteModel {
        return noteDao.get(noteId)
    }

    fun update(note: NoteModel) {
        noteDao.update(note)
    }

    fun delete(note: NoteModel) {
        noteDao.delete(note)
    }

    fun isTitleUsed(title: String, excludeNoteId: Int? = null): Int {
        return noteDao.isTitleUsed(title,excludeNoteId)
    }

    fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }

    fun deletePublicNotes() {
        noteDao.deletePublicNotes()
    }

    fun deletePrivateNotes() {
        noteDao.deletePrivateNotes()
    }

    fun getAllNotes(): List<NoteModel> {
        return noteDao.getAllNotes()
    }

    fun getPublicNotes(): List<NoteModel> {
        return noteDao.getPublicNotes()
    }

    fun getPrivateNotes(): List<NoteModel> {
        return noteDao.getPrivateNotes()
    }

    fun searchNotes(
        searchQuery: String,
        isPublic: Boolean?,
        noteType: String?,
        createdDateStart: Date?,
        createdDateEnd: Date?,
        modifiedDateStart: Date?,
        modifiedDateEnd: Date?
    ): List<NoteModel>{
        return noteDao.searchNotes(searchQuery, isPublic, noteType, createdDateStart,
            createdDateEnd,modifiedDateStart,modifiedDateEnd)
    }
}
