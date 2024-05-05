package com.example.lifecanvas.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toArgb
import com.example.lifecanvas.model.DrawingPathModel
import java.io.File
import java.io.FileOutputStream

fun createBitmapFromPaths(
    paths: List<DrawingPathModel>,
    width: Int,
    height: Int,
    existingBitmap: Bitmap? = null
): Bitmap {
    val mutableBitmap = existingBitmap?.run {
        Bitmap.createBitmap(width, height, config).also {
            Canvas(it).drawBitmap(this, 0f, 0f, null)
        }
    }
    val bitmap = mutableBitmap ?: Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    if (existingBitmap == null) {
        canvas.drawColor(Color.WHITE)
    }
    paths.forEach { pathModel ->
        canvas.drawPath(pathModel.path.asAndroidPath(), Paint().apply {
            color = pathModel.color.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = pathModel.stroke
            isAntiAlias = true
        })
    }

    return bitmap
}

fun saveBitmapToFile(bitmap: Bitmap, filename: String, context: Context): String? {
    val outputStream: FileOutputStream
    try {
        val file = File(context.getExternalFilesDir(null), filename)
        outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun loadImageBitmap(filePath: String?): Bitmap? {
    return filePath?.let {
        BitmapFactory.decodeFile(it)
    }
}
fun deleteBitmapFile(filePath: String): Boolean {
    return try {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        } else {
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}