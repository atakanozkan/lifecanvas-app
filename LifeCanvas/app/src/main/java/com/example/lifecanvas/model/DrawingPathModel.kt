package com.example.lifecanvas.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class DrawingPathModel(
    val color: Color,
    val path: Path,
    val stroke: Float
)
