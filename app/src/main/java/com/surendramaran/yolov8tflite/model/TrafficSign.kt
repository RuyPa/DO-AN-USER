package com.surendramaran.yolov8tflite.model

data class TrafficSign(
    val id : Int,
    val name: String,        // Tên biển báo
    val code: String,        // Mã biển báo
    val description: String, // Mô tả biển báo
    val imagePath: String    // Đường dẫn hình ảnh biển báo
)


