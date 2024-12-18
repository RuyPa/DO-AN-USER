package com.surendramaran.yolov8tflite.api
import com.surendramaran.yolov8tflite.model.ApiResponse
import com.surendramaran.yolov8tflite.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiService {
    @GET("api/traffic_signs/search")
    fun searchTrafficSigns(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
        @Query("category_id") categoryId: Int // Fetch by categoryId
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("login") // Thay bằng endpoint phù hợp
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>
}
