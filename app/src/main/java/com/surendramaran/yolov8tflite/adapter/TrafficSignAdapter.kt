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
    private var items: List<TrafficSign>,
    private val onItemClick: (TrafficSign) -> Unit // Callback khi nhấn vào mục
) : RecyclerView.Adapter<TrafficSignAdapter.TrafficSignViewHolder>() {
    class TrafficSignViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.sign_name)
        val stt: TextView = view.findViewById(R.id.sign_stt)
        val image: ImageView = view.findViewById(R.id.sign_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrafficSignViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.traffic_sign_item, parent, false)
        return TrafficSignViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrafficSignViewHolder, position: Int) {
        val trafficSign = items[position]
        holder.stt.text = "STT: ${position + 1}"  // Hiển thị STT
        holder.name.text = trafficSign.name       // Hiển thị tên biển báo

        Glide.with(holder.image.context)
            .load(trafficSign.path)         // Tải hình ảnh từ URL
            .into(holder.image)

        // Gọi callback khi nhấn vào item
        holder.itemView.setOnClickListener {
            onItemClick(trafficSign)
        }
    }

    override fun getItemCount(): Int = items.size

    // Thêm hàm cập nhật dữ liệu nếu danh sách thay đổi
    fun updateData(newItems: List<TrafficSign>) {
        items = newItems
        notifyDataSetChanged()
    }
}
