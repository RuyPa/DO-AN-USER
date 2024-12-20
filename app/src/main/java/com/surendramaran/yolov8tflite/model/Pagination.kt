package com.surendramaran.yolov8tflite.model

data class Pagination(
    val current_page: Int,
    val page_size: Int,
    val total_items: Int,
    val total_pages: Int
)