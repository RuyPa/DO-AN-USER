package com.surendramaran.yolov8tflite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surendramaran.yolov8tflite.R
import com.surendramaran.yolov8tflite.model.TrafficSign

class TrafficSignAdapter(
    private val trafficSignList: List<TrafficSign>,  // Danh sách các biển báo giao thông
    private val onItemClick: (TrafficSign) -> Unit   // Callback khi click vào item
) : RecyclerView.Adapter<TrafficSignAdapter.TrafficSignViewHolder>() {

    // ViewHolder class để giữ các view cho từng item
    class TrafficSignViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val signName: TextView = itemView.findViewById(R.id.sign_name)
        val signCode: TextView = itemView.findViewById(R.id.sign_code)
        val signDescription: TextView = itemView.findViewById(R.id.sign_description)
        val signImage: ImageView = itemView.findViewById(R.id.sign_image)

        // Gán dữ liệu cho ViewHolder
        fun bind(trafficSign: TrafficSign, onItemClick: (TrafficSign) -> Unit) {
            signName.text = trafficSign.name
            signCode.text = "Mã: ${trafficSign.code}"  // Hiển thị mã biển báo
            signDescription.text = trafficSign.description

            // Sử dụng Glide để load ảnh từ đường dẫn
            Glide.with(itemView.context)
                .load(trafficSign.imagePath)
                .into(signImage)

            // Xử lý sự kiện click vào item
            itemView.setOnClickListener {
                onItemClick(trafficSign)
            }
        }
    }

    // Tạo ViewHolder từ layout item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrafficSignViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.traffic_sign_item, parent, false)
        return TrafficSignViewHolder(view)
    }

    // Gán dữ liệu cho ViewHolder
    override fun onBindViewHolder(holder: TrafficSignViewHolder, position: Int) {
        val trafficSign = trafficSignList[position]
        holder.bind(trafficSign, onItemClick)
    }

    // Trả về số lượng biển báo trong danh sách
    override fun getItemCount(): Int = trafficSignList.size
}
