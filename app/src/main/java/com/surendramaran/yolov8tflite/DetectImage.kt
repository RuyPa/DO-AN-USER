package com.surendramaran.yolov8tflite

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.surendramaran.yolov8tflite.Constants.LABELS_PATH
import com.surendramaran.yolov8tflite.Constants.MODEL_PATH

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.surendramaran.yolov8tflite.adapter.DetectionResultAdapter
import com.surendramaran.yolov8tflite.adapter.TrafficSignAdapter
import com.surendramaran.yolov8tflite.model.DetectionResult
import com.surendramaran.yolov8tflite.model.TrafficSign

import java.io.OutputStream
import kotlin.math.log

class DetectImage : AppCompatActivity(), Detector.DetectorListener {

    private lateinit var imageView: ImageView
    private lateinit var overlayView: OverlayView
    private lateinit var detector: Detector
    private var selectedBitmap: Bitmap? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detect_image)


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

        // Khởi tạo detector
        detector = Detector(baseContext, Constants.MODEL_PATH, Constants.LABELS_PATH, this)
        detector.setup()

        // Tham chiếu các view
        val btnSelectImage: Button = findViewById(R.id.btnSelectImage)
        val btnRecognize: Button = findViewById(R.id.btnRecognize)
        val btnDownload: Button = findViewById(R.id.btnDownload)
        imageView = findViewById(R.id.imageView)
        overlayView = findViewById(R.id.overlayView)

        // Khai báo các View
        val cardViewImage = findViewById<MaterialCardView>(R.id.cardViewImage)

// Khi nhấn nút chọn ảnh
        btnSelectImage.setOnClickListener {
            // Mở bộ chọn ảnh hoặc thực hiện hành động chọn ảnh
            // Sau khi ảnh được chọn thành công, hiển thị cardViewImage
            cardViewImage.visibility = View.VISIBLE
        }

        // Xử lý khi bấm nút chọn ảnh
        btnSelectImage.setOnClickListener {
            openImagePicker()
        }

        // Xử lý khi bấm nút nhận diện
        btnRecognize.setOnClickListener {
            if (selectedBitmap != null) {
                overlayView.setDrawMode(true)
                detectImage(selectedBitmap!!)

                val detectedLabels = overlayView.getDetectedLabels()

            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh trước!", Toast.LENGTH_SHORT).show()
            }

        }

        // Xử lý khi bấm nút download
        btnDownload.setOnClickListener {
            if (selectedBitmap != null) {
                val bitmapWithBoxes = createBitmapWithOverlay(selectedBitmap!!)
//                val bitmapWithBoxes = createBitmapWithOverlay(selectedBitmap!!, marginLeft =300f, marginTop = 200f)

                saveImageToGallery(this, bitmapWithBoxes, "Detected_Image")
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh và nhận diện trước!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun mapLabelsToTrafficSigns(
        labels: List<String>,
        trafficSigns: List<TrafficSign>
    ): Map<Int, TrafficSign> {
        val mappedTrafficSigns = mutableMapOf<Int, TrafficSign>()

        labels.forEachIndexed { index, label ->
            // Tìm biển báo phù hợp với `label` (so sánh với `code`)
            val matchedSign = trafficSigns.find { it.code == label }
            if (matchedSign != null) {
                mappedTrafficSigns[index + 1] = matchedSign // Key là `stt`, value là TrafficSign
            }
        }

        return mappedTrafficSigns
    }

    // Hàm mở thư viện để chọn ảnh
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun createBitmapWithNumberedOverlayV2(originalBitmap: Bitmap): Bitmap {
        val bitmapWithOverlay = Bitmap.createBitmap(
            originalBitmap.width,
            originalBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmapWithOverlay)
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)

        // Vẽ số thứ tự lên bounding boxes
        overlayView.getResults().forEachIndexed { index, box ->
            val left = box.x1 * originalBitmap.width
            val top = box.y1 * originalBitmap.height

            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = 40f
                style = Paint.Style.FILL
            }
            val textBackgroundPaint = Paint().apply {
                color = Color.BLACK
                style = Paint.Style.FILL
            }
            val bounds = Rect()
            val numberText = (index + 1).toString()
            textPaint.getTextBounds(numberText, 0, numberText.length, bounds)

            // Vẽ nền cho số thứ tự
            canvas.drawRect(
                left,
                top,
                left + bounds.width() + 8,
                top + bounds.height() + 8,
                textBackgroundPaint
            )

            // Vẽ số thứ tự
            canvas.drawText(numberText, left, top + bounds.height(), textPaint)
        }

        return bitmapWithOverlay
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data ?: return
            val inputStream = contentResolver.openInputStream(selectedImageUri)
            selectedBitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(selectedBitmap)

            // Xóa các bounding boxes cũ
            overlayView.setResults(emptyList())
            val cardViewImage: MaterialCardView = findViewById(R.id.cardViewImage)
            cardViewImage.visibility = View.VISIBLE


            // Hiển thị các nút nhận diện và download
            val btnRecognize: Button = findViewById(R.id.btnRecognize)
            val btnDownload: Button = findViewById(R.id.btnDownload)
            btnRecognize.visibility = View.VISIBLE
            btnDownload.visibility = View.VISIBLE
        }
    }


    // Hàm xử lý nhận diện ảnh
    private fun detectImage(bitmap: Bitmap) {
        detector.detect(bitmap)
    }

    // Xử lý khi không phát hiện đối tượng nào
    override fun onEmptyDetect() {
        runOnUiThread {
            overlayView.setResults(emptyList())
            Toast.makeText(this, "Không phát hiện đối tượng nào!", Toast.LENGTH_SHORT).show()
        }
    }

    private val trafficSigns = listOf(
        TrafficSign(0, "Cấm đi ngược chiều", "cam_di_nguoc_chieu", "Biển báo này chỉ dẫn phương tiện không được phép di chuyển ngược chiều. Phạm vi áp dụng cho các đường một chiều và khu vực giao thông hạn chế.", "https://thietbigiaothong247.com/wp-content/uploads/2017/04/bien-cam-di-nguoc-chieu.jpg"),
        TrafficSign(1, "Cấm dừng và đỗ xe", "cam_dung_va_do_xe", "Biển báo này quy định các phương tiện không được dừng hoặc đỗ tại vị trí đặt biển, nhằm đảm bảo lưu thông thông thoáng và an toàn cho khu vực.", "https://bizweb.dktcdn.net/100/378/087/products/bien-bao-giao-thong.jpg?v=1583945216690"),
        TrafficSign(2, "Cấm rẽ trái", "cam_re_trai", "Biển này yêu cầu tất cả các phương tiện không được phép rẽ trái tại ngã tư hoặc vị trí cụ thể để tránh xung đột giao thông và tai nạn.", "https://www.thietbithinhphat.com/wp-content/uploads/2023/01/Bien-cam-re-trai-510x510-1.jpg"),
        TrafficSign(3, "Giới hạn tốc độ", "gioi_han_toc_do", "Biển báo này quy định tốc độ tối đa cho phép mà các phương tiện có thể di chuyển trong khu vực hoặc đoạn đường được chỉ định, nhằm bảo đảm an toàn giao thông.", "https://bizweb.dktcdn.net/100/415/690/files/cac-bien-bao-toc-do-2.jpg?v=1677145397513"),
        TrafficSign(4, "Biển báo cấm", "bien_bao_cam", "Biển này thông báo một lệnh cấm chung cho các phương tiện hoặc hành vi cụ thể như cấm xe máy, xe tải, hoặc cấm vượt tại khu vực nhất định.", "https://carpla.vn/blog/wp-content/uploads/2023/11/cac-loai-bien-bao-cam.jpg"),
        TrafficSign(5, "Biển báo nguy hiểm", "bien_nguy_hiem", "Biển này cảnh báo các tình huống giao thông nguy hiểm như đường cong gấp, lối đi có dốc lớn, hoặc khu vực dễ sạt lở, yêu cầu các tài xế giảm tốc độ và chú ý quan sát.", "https://image3.luatvietnam.vn/uploaded/images/original/2024/03/27/bien-bao-nguy-hiem-la-gi_2703152112.jpg"),
        TrafficSign(6, "Biển hiệu lệnh", "bien_hieu_lenh", "Biển này chỉ dẫn bắt buộc về hướng đi hoặc hành vi mà người điều khiển phương tiện cần tuân thủ, như đi thẳng, rẽ phải, hoặc đi vào khu vực hạn chế.", "https://cdn.thuvienphapluat.vn/uploads/tintuc/2022/10/15/bien-bao-R.301.jpg"),
        TrafficSign(14, "updateNamee", "updateCode", "updateDescription", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1730136225/tktbeonzg5x6t3ljuuys.png"),
        TrafficSign(18, "duy đỗ bá", "duy_đo_ba", "ddddddddddddddddddđ", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1730194939/n03eglmfwcrpq5lzqxeo.png")
    )


    // Xử lý khi phát hiện đối tượng
    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        runOnUiThread {
            overlayView.setResults(boundingBoxes)

            val detectedLabels = overlayView.getDetectedLabels()


            val mappedTrafficSigns = detectedLabels.mapNotNull { (stt, clsName) ->
                Log.d("Duyab", stt.toString())
                Log.d("Duyab", clsName)


                val trafficSign = trafficSigns.find { it.code == clsName }
                trafficSign?.let { Pair(stt, it) }
            }.sortedBy { it.first } // Sắp xếp theo STT

            // Hiển thị trong RecyclerView
            val recyclerView: RecyclerView = findViewById(R.id.detectionResultTable)
            recyclerView.layoutManager = LinearLayoutManager(this)
//            recyclerView.adapter = TrafficSignAdapter(mappedTrafficSigns.map { it.second }) // Chỉ truyền TrafficSign
            recyclerView.adapter = TrafficSignAdapter(mappedTrafficSigns.map { it.second }) { trafficSign ->
                showTrafficSignPopup(trafficSign) // Gọi hàm hiển thị popup khi nhấn vào mục
            }
            // Hiển thị bảng kết quả
            recyclerView.visibility = View.VISIBLE
            Toast.makeText(this, "Nhận diện xong: ${boundingBoxes.size} đối tượng!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showTrafficSignPopup(trafficSign: TrafficSign) {
        // Inflate layout custom dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_traffic_sign, null)

        // Ánh xạ các view trong layout
        val signName: TextView = dialogView.findViewById(R.id.dialog_sign_name)
        val signImage: ImageView = dialogView.findViewById(R.id.dialog_sign_image)
        val signDescription: TextView = dialogView.findViewById(R.id.dialog_sign_description)
        val closeButton: Button = dialogView.findViewById(R.id.dialog_close_button)

        // Gán dữ liệu
        signName.text = trafficSign.name
        signDescription.text = trafficSign.description
        Glide.with(this)
            .load(trafficSign.imagePath)
            .into(signImage)

        // Tạo AlertDialog với layout tùy chỉnh
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Xử lý sự kiện cho nút Đóng
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }





//    private fun createBitmapWithOverlay(originalBitmap: Bitmap): Bitmap {
//        val bitmapWithOverlay = Bitmap.createBitmap(
//            originalBitmap.width,
//            originalBitmap.height,
//            Bitmap.Config.ARGB_8888
//        )
//        val canvas = Canvas(bitmapWithOverlay)
//        canvas.drawBitmap(originalBitmap, 0f, 0f, null)
//
//        overlayView.drawV1(canvas)
//
//        return bitmapWithOverlay
//    }

    private fun createBitmapWithOverlay(originalBitmap: Bitmap): Bitmap {
        val bitmapWithOverlay = Bitmap.createBitmap(
            originalBitmap.width,
            originalBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmapWithOverlay)
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)

        // Duyệt qua danh sách bounding boxes trong `OverlayView` để vẽ
        overlayView.getResults().forEach { box ->
            val left = (box.x1 * originalBitmap.width)
            val top = (box.y1 * originalBitmap.height)
            val right = (box.x2 * originalBitmap.width)
            val bottom = (box.y2 * originalBitmap.height)

            val boxPaint = Paint().apply {
                color = Color.GREEN
                strokeWidth = 8f
                style = Paint.Style.STROKE
            }
            canvas.drawRect(left, top, right, bottom, boxPaint)

            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 40f
            }
            canvas.drawText(box.clsName, left, top - 10, textPaint)
        }

        return bitmapWithOverlay
    }



    // Lưu ảnh vào thư viện
    private fun saveImageToGallery(context: Context, bitmap: Bitmap, fileName: String) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
            outputStream.use {
                if (it != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                Toast.makeText(context, "Lưu ảnh thành công!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Không thể lưu ảnh!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.clear()
    }
}
