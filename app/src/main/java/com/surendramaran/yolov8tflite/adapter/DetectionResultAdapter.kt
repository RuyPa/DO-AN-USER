package com.surendramaran.yolov8tflite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.surendramaran.yolov8tflite.R
import com.surendramaran.yolov8tflite.model.DetectionResult

class DetectionResultAdapter(private val detectionResults: List<DetectionResult>) :
    RecyclerView.Adapter<DetectionResultAdapter.DetectionResultViewHolder>() {

    class DetectionResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSerialNumber: TextView = view.findViewById(R.id.tvSerialNumber)
        val tvSignName: TextView = view.findViewById(R.id.tvSignName)
        val imgSign: ImageView = view.findViewById(R.id.imgSign)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetectionResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detection_result, parent, false)
        return DetectionResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetectionResultViewHolder, position: Int) {
        val result = detectionResults[position]
        holder.tvSerialNumber.text = (position + 1).toString()
        holder.tvSignName.text = result.name
        holder.imgSign.setImageResource(result.imageRes) // Fake data: replace with actual image
    }

    override fun getItemCount() = detectionResults.size
}
