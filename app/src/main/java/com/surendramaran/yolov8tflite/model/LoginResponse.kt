package com.surendramaran.yolov8tflite.model

data class LoginResponse(
    val access_token: String,
    val message: String?,
    val name: String?,
    val refresh_token: String?,
    val role: String?
)
