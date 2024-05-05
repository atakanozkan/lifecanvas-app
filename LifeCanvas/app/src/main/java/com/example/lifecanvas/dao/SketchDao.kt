package com.example.lifecanvas.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.lifecanvas.model.SketchModel
import java.util.Date

@Dao
interface SketchDao {
    @Query("SELECT * FROM sketches")
    fun getAllSketches(): List<SketchModel>

    @Query("SELECT * FROM sketches WHERE id = :sketchId")
    fun get(sketchId: Int): SketchModel

    @Insert
    fun insert(sketch: SketchModel)

    @Update
    fun update(sketch: SketchModel)

    @Delete
    fun delete(sketch: SketchModel)

    @Query("SELECT COUNT(*) FROM sketches WHERE title = :title AND (:excludeNoteId IS NULL OR id != :excludeNoteId)")
    fun isTitleUsed(title: String, excludeNoteId: Int?): Int


    @Query("""
    SELECT * FROM sketches 
    WHERE title LIKE :searchQuery
    AND (:createdDateStart IS NULL OR createdDate >= :createdDateStart)
    AND (:createdDateEnd IS NULL OR createdDate <= :createdDateEnd)
    AND (:modifiedDateStart IS NULL OR modifiedDate >= :modifiedDateStart)
    AND (:modifiedDateEnd IS NULL OR modifiedDate <= :modifiedDateEnd)
""")
    fun searchSketches(
        searchQuery: String,
        createdDateStart: Date?,
        createdDateEnd: Date?,
        modifiedDateStart: Date?,
        modifiedDateEnd: Date?
    ): List<SketchModel>
}
