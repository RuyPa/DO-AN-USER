package com.surendramaran.yolov8tflite.api
import com.surendramaran.yolov8tflite.model.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Header


interface ApiService {
    @GET("api/traffic_signs/search")
    fun searchTrafficSigns(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
        @Query("category_id") categoryId: Int // Fetch by categoryId
    ): Call<ApiResponse>
}
