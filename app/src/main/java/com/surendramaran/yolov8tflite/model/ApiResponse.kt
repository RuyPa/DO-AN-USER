package com.surendramaran.yolov8tflite.model



data class ApiResponse(
    val data: List<TrafficSign>,
    val pagination: Pagination
)
