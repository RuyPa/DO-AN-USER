package com.surendramaran.yolov8tflite.model

data class DetectionResult(
    val name: String,
    val imageRes: Int // Fake resource ID for now
)
