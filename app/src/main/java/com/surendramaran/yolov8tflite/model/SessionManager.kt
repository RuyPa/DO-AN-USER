package com.surendramaran.yolov8tflite.model

object SessionManager {
    var accessToken: String? = null

    fun saveToken(token: String) {
        accessToken = token
    }

    fun getToken(): String? {
        return accessToken
    }

    fun clearToken() {
        accessToken = null
    }
}
