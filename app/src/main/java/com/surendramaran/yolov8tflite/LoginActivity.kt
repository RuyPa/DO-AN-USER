package com.surendramaran.yolov8tflite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.surendramaran.yolov8tflite.api.RetrofitClient
import com.surendramaran.yolov8tflite.model.ApiResponse
import com.surendramaran.yolov8tflite.model.LoginResponse
import com.surendramaran.yolov8tflite.model.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: AutoCompleteTextView
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var cbRememberMe: CheckBox
    private lateinit var progressBar: ProgressBar
    private val sharedPreferences by lazy {
        getSharedPreferences("SavedAccounts", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        cbRememberMe = findViewById(R.id.cb_remember_me)
        progressBar = findViewById(R.id.progressBar)

        loadSavedAccount() // Tải tài khoản cuối cùng đã lưu

        btnLogin.setOnClickListener {
            val email = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (cbRememberMe.isChecked) saveAccount(email, password)
                login(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Tải tài khoản cuối cùng đã lưu
    private fun loadSavedAccount() {
        val email = sharedPreferences.getString("LAST_EMAIL", "")
        val password = sharedPreferences.getString("LAST_PASSWORD", "")
        val isRememberMeChecked = sharedPreferences.getBoolean("REMEMBER_ME", false)

        if (isRememberMeChecked) {
            etUsername.setText(email)
            etPassword.setText(password)
            cbRememberMe.isChecked = true
        }
    }

    // Lưu tài khoản vào SharedPreferences
    private fun saveAccount(email: String, password: String) {
        sharedPreferences.edit().apply {
            putString("LAST_EMAIL", email)
            putString("LAST_PASSWORD", password)
            putBoolean("REMEMBER_ME", true)
            apply()
        }
    }

    private fun login(email: String, password: String) {
        // Hiển thị ProgressDialog
        progressBar.visibility = View.VISIBLE

        RetrofitClient.apiService.login(email, password)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    progressBar.visibility = View.GONE // Ẩn ProgressBar khi API trả về

                    if (response.isSuccessful) {
                        val accessToken = response.body()?.access_token ?: ""
                        val name = response.body()?.name ?: "Guest"

                        SessionManager.saveToken(accessToken) // Lưu token

                        Toast.makeText(
                            this@LoginActivity,
                            "Đăng nhập thành công",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Chuyển sang MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                            putExtra("USER_NAME", name)
                        }
                        startActivity(intent)
                        finish() // Đóng LoginActivity để không quay lại được bằng nút Back
                    } else {
                        Toast.makeText(this@LoginActivity, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE // Ẩn ProgressBar khi có lỗi

                    Toast.makeText(
                        this@LoginActivity,
                        "Lỗi: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
