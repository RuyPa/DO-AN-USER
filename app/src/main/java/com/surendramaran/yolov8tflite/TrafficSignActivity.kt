package com.surendramaran.yolov8tflite

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.surendramaran.yolov8tflite.adapter.TrafficSignAdapter
import com.surendramaran.yolov8tflite.model.TrafficSign

class TrafficSignActivity : AppCompatActivity() {

//    private lateinit var recyclerView: RecyclerView
//    private lateinit var trafficSignAdapter: TrafficSignAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.traffic_sign) // Bind to the XML layout with RecyclerView
//
//        // Initialize RecyclerView
//        recyclerView = findViewById(R.id.food_list)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        // Sample list of traffic signs
//        val trafficSignList = listOf(
//            TrafficSign("Stop Sign", "001", "A sign indicating vehicles must stop", "https://static.vecteezy.com/system/resources/previews/003/809/110/non_2x/cute-police-chibi-character-design-vector.jpg"),
//            TrafficSign("Yield Sign", "002", "A sign indicating vehicles must yield", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR-vj1eoRw2d-3o2AKCBG85fk_NK74Jgd-meA&s"),
//            TrafficSign("No Entry", "003", "A sign indicating no entry", "https://media.istockphoto.com/id/883910278/vector/no-entry-traffic-sign-isolated-on-the-white-illustration-vector.jpg?s=612x612&w=0&k=20&c=xvqwAkqtuhfsg0mC71g3y9StAggaOGHjIjqSXcC6Xbg="),
//            TrafficSign("Speed Limit 50", "004", "A sign indicating speed limit of 50", "https://media.istockphoto.com/id/1191601898/vector/speed-limit-50-sign.jpg?s=1024x1024&w=is&k=20&c=LKiW0N_YjduUVJDS1mlQ_Dwhc6jovY80OKWmqnoDMxM=")
//        )
//
//        // Set up adapter
//        trafficSignAdapter = TrafficSignAdapter(trafficSignList) { trafficSign ->
//            // Handle click events here, e.g., open a new activity or show a Toast
//            // For example, to show a Toast:
//            // Toast.makeText(this, "Clicked: ${trafficSign.name}", Toast.LENGTH_SHORT).show()
//        }
//
//        // Bind adapter to RecyclerView
//        recyclerView.adapter = trafficSignAdapter
//    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var trafficSignAdapter: TrafficSignAdapter

    private lateinit var btnMainDishes: Button
    private lateinit var btnSalad: Button
    private lateinit var btnDrinks: Button
    private lateinit var backIcon: ImageView  // Khai báo nút back icon


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.traffic_sign) // Bind to the XML layout with RecyclerView

        // Make the app fullscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.food_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Find buttons for categories
        btnMainDishes = findViewById(R.id.btn_main_dishes)
        btnSalad = findViewById(R.id.btn_salad)
        btnDrinks = findViewById(R.id.btn_drinks)
        backIcon = findViewById(R.id.back_icon) // Thêm nút back icon

        backIcon.setOnClickListener {
            finish()  // Quay lại màn hình trước (MainActivity)
        }


        // Load default list (Biển báo cấm)
        loadTrafficSignList(getProhibitedSigns())
        highlightSelectedButton(btnMainDishes)

        // Set up category button click listeners
        btnMainDishes.setOnClickListener {
            loadTrafficSignList(getProhibitedSigns())
            highlightSelectedButton(btnMainDishes)
        }

        btnSalad.setOnClickListener {
            loadTrafficSignList(getGuideSigns())
            highlightSelectedButton(btnSalad)
        }

        btnDrinks.setOnClickListener {
            loadTrafficSignList(getWarningSigns())
            highlightSelectedButton(btnDrinks)
        }
    }

    private fun loadTrafficSignList(trafficSignList: List<TrafficSign>) {
        trafficSignAdapter = TrafficSignAdapter(trafficSignList) { trafficSign ->
            // Handle click events here, e.g., show a Toast with the traffic sign name
            Toast.makeText(this, "Clicked: ${trafficSign.name}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = trafficSignAdapter
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun highlightSelectedButton(selectedButton: Button) {
        // Reset button colors
        btnMainDishes.setBackgroundColor(getColor(R.color.default_button_color))
        btnSalad.setBackgroundColor(getColor(R.color.default_button_color))
        btnDrinks.setBackgroundColor(getColor(R.color.default_button_color))

        // Set highlighted color for the selected button
        selectedButton.setBackgroundColor(getColor(R.color.selected_button_color))
    }
    // Danh sách biển báo cấm
    private fun getProhibitedSigns(): List<TrafficSign> {
        return listOf(
            TrafficSign("Stop Sign", "001", "A sign indicating vehicles must stop", "https://thumbs.dreamstime.com/b/cartoon-bodyguard-stop-cartoon-illustration-bodyguard-holding-up-stop-sign-135524686.jpg"),
            TrafficSign("No Entry", "002", "A sign indicating no entry", "https://files.oaiusercontent.com/file-qnNf626TzmPTbLq1VZZ8UCOI?se=2024-10-19T13%3A26%3A01Z&sp=r&sv=2024-08-04&sr=b&rscc=max-age%3D604800%2C%20immutable%2C%20private&rscd=attachment%3B%20filename%3D74891ffe-e07e-41ee-b34b-a07ed5e74399.webp&sig=Uo0ZAheBzVF4LCjw9d/nfuL8nt5tnFuacMeQnHqAht4%3D")
        )
    }

    // Danh sách biển chỉ dẫn
    private fun getGuideSigns(): List<TrafficSign> {
        return listOf(
            TrafficSign("Go Ahead", "101", "A guide sign", "https://hondamydinh.com.vn/wp-content/uploads/2021/07/Logo-bie%CC%82%CC%89n-ba%CC%81o-chi%CC%89-da%CC%82%CC%83n.jpeg"),
            TrafficSign("Car Go Ahead", "102", "Another guide sign", "https://daotaolaixehd.com.vn/wp-content/uploads/2016/12/bien-chi-dan-412b.png")
        )
    }

    // Danh sách biển nguy hiểm
    private fun getWarningSigns(): List<TrafficSign> {
        return listOf(
            TrafficSign("No Cross", "201", "A warning sign", "https://trungtamthanhcong.net/wp-content/uploads/2015/07/w.225.jpg"),
            TrafficSign("Another Warning", "202", "Another warning sign", "https://bienbaocongtrinh.com/thumb1/500x500/2/upload/thuoctinh/bienbaogiao-thongnguyhiem610-12-5692.jpg")
        )
    }
}
