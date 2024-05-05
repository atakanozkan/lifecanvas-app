package com.example.lifecanvas.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.lifecanvas.model.NoteModel
import java.util.Date

@Dao
interface NoteDao {
    @Insert
    fun insert(note: NoteModel)

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun get(noteId: Int): NoteModel

    @Update
    fun update(note: NoteModel)

    @Delete
    fun delete(note: NoteModel)

    @Query("SELECT COUNT(*) FROM notes WHERE title = :title AND (:excludeNoteId IS NULL OR id != :excludeNoteId)")
    fun isTitleUsed(title: String, excludeNoteId: Int?): Int

    @Query("DELETE FROM notes")
    fun deleteAllNotes()

    @Query("DELETE FROM notes WHERE isPublic = 1")
    fun deletePublicNotes()

    @Query("DELETE FROM notes WHERE isPublic = 0")
    fun deletePrivateNotes()

    @Query("SELECT * FROM notes")
    fun getAllNotes(): List<NoteModel>

    @Query("SELECT * FROM notes WHERE isPublic = 1")
    fun getPublicNotes(): List<NoteModel>

    @Query("SELECT * FROM notes WHERE isPublic = 0")
    fun getPrivateNotes(): List<NoteModel>

    @Query("""
    SELECT * FROM notes 
    WHERE title LIKE :searchQuery 
    AND (:isPublic IS NULL OR isPublic = :isPublic)
    AND (:noteType IS NULL OR type = :noteType)
    AND (:createdDateStart IS NULL OR createdDate >= :createdDateStart)
    AND (:createdDateEnd IS NULL OR createdDate <= :createdDateEnd)
    AND (:modifiedDateStart IS NULL OR modifiedDate >= :modifiedDateStart)
    AND (:modifiedDateEnd IS NULL OR modifiedDate <= :modifiedDateEnd)
""")
    fun searchNotes(
        searchQuery: String,
        isPublic: Boolean?,
        noteType: String?,
        createdDateStart: Date?,
        createdDateEnd: Date?,
        modifiedDateStart: Date?,
        modifiedDateEnd: Date?
    ): List<NoteModel>
}
