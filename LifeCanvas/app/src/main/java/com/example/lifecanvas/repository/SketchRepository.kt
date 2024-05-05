package com.example.lifecanvas.repository

import com.example.lifecanvas.dao.SketchDao
import com.example.lifecanvas.model.SketchModel
import java.util.Date

class SketchRepository(private val sketchDAO: SketchDao) {
    fun getAllSketches(): List<SketchModel> {
        return sketchDAO.getAllSketches()
    }

    fun get(id: Int): SketchModel {
        return sketchDAO.get(id)
    }

    fun insert(sketch: SketchModel) {
        sketchDAO.insert(sketch)
    }

    fun update(sketch: SketchModel) {
        sketchDAO.update(sketch)
    }

    fun delete(sketch: SketchModel) {
        sketchDAO.delete(sketch)
    }

    fun isTitleUsed(title: String, excludeNoteId: Int? = null): Int {
        return sketchDAO.isTitleUsed(title,excludeNoteId)
    }
    fun searchSketches(
        searchQuery: String,
        createdDateStart: Date?,
        createdDateEnd: Date?,
        modifiedDateStart: Date?,
        modifiedDateEnd: Date?
    ): List<SketchModel>{
        return sketchDAO.searchSketches(searchQuery, createdDateStart,
            createdDateEnd,modifiedDateStart,modifiedDateEnd)
    }
}
