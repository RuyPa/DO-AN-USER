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
        TrafficSign(0, "Cấm ô tô khách và ô tô tải", "cam_o_to_khach_va_o_to_tai", "Cấm ô tô chở khách và các loại ô tô tải kể cả máy kéo và xe máy thi công chuyên dùng, trừ xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688402/dk94razdmxz7ij4fml4n.png"),
        TrafficSign(1, "Cấm xe ô tô khách", "cam_xe_oto_khach", "Cấm ô tô khách và ô tô tải (cấm luôn cả máy kéo và xe máy chuyên dùng)", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/Vietnam_road_sign_P107.svg/1024px-Vietnam_road_sign_P107.svg.png"),
        TrafficSign(2, "Cấm đi xe đạp", "cam_di_xe_dap", "Cấm xe đạp đi qua. Biển không có giá trị cấm người dắt xe đạp.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688405/owbztgbhm6bs4z5lcpqn.png"),
        TrafficSign(3, "Cấm xe gắn máy", "cam_xe_gan_may", "Cấm xe gắn máy. Biển không có giá trị với xe đạp.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688407/nlfad9asdkbzwzfwj36q.png"),
        TrafficSign(4, "Cấm xe lam", "cam_xe_lam", "Cấm xe 3 bánh loại có động cơ như xe lam, xích lô máy...", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688408/km1rcg1xadvsyt4subnk.png"),
        TrafficSign(5, "Cấm người đi bộ", "cam_nguoi_đi_bo", "Cấm người đi bộ qua lại.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688411/wxsihgosqkt2rmuskjth.png"),
        TrafficSign(6, "Hạn chế chiều cao", "han_che_chieu_cao", "Cấm xe cơ giới và thô sơ có chiều cao vượt quá trị số ghi trên biển, kể cả các xe ưu tiên (chiều cao tính từ mặt đường, mặt cầu đến điểm cao nhất của xe hoặc hàng).", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732688416/wq0jsvqhkhy6x9bpjzam.png"),
        TrafficSign(7, "Cấm rẽ trái", "cam_re_trai", "Biển này yêu cầu tất cả các phương tiện không được phép rẽ trái tại ngã tư hoặc vị trí cụ thể để tránh xung đột giao thông và tai nạn.", "https://www.thietbithinhphat.com/wp-content/uploads/2023/01/Bien-cam-re-trai-510x510-1.jpg"),
        TrafficSign(8, "Cấm rẽ phải", "cam_re_phai", "Cấm các loại xe cơ giới và thô sơ rẽ phải ở những vị trí đường giao nhau, trừ xe ưu tiên. Biển này không có giá trị cấm xe quay đầu.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732689014/w3muc6ez9nxfwighko5j.png"),
        TrafficSign(9, "Cấm quay xe", "cam_quay_xe", "Cấm các loại xe cơ giới và thô sơ quay đầu (theo kiểu chữ U) trừ các xe ưu tiên. Biển này không cấm rẽ trái.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732689015/xcfulwu87gm8ndxdt7dg.png"),
        TrafficSign(10, "Cấm ô tô quay đầu xe", "cam_oto_quay_dau_xe", "Cấm ô tô quay đầu xe", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1e/Vietnam_road_sign_P124b1.svg/800px-Vietnam_road_sign_P124b1.svg.png"),
        TrafficSign(11, "Cấm rẽ trái và quay đầu xe", "cam_re_trai_va_quay_dau_xe", "Cấm rẽ trái và quay xe", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/06/Vietnam_road_sign_P124c.svg/800px-Vietnam_road_sign_P124c.svg.png"),
        TrafficSign(12, "Cấm ô tô rẽ trái và quay đầu xe", "cam_oto_re_trai_va_quay_dau_xe", "Cấm ô tô rẽ trái và quay xe", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e8/Vietnam_road_sign_P124e.svg/800px-Vietnam_road_sign_P124e.svg.png"),
        TrafficSign(13, "Cấm vượt", "cam_vuot", "Cấm các loại xe cơ giới vượt nhau kể cả xe ưu tiên.", "https://res.cloudinary.com/dkf74ju3o/image/upload/v1732689016/na2xextmmna5dfvvsu45.png"),
        TrafficSign(14, "Tốc độ tối đa cho phép 40", "toc_do_toi_da_cho_phep_40", "Tốc độ tối đa cho phép 40 km/h", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/Vietnam_road_sign_P127-40.svg/800px-Vietnam_road_sign_P127-40.svg.png"),
        TrafficSign(15, "Tốc độ tối đa cho phép 50", "toc_do_toi_da_cho_phep_50", "Tốc độ tối đa cho phép 50 km/h", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Vietnam_road_sign_P127-50.svg/800px-Vietnam_road_sign_P127-50.svg.png"),
        TrafficSign(16, "Tốc độ tối đa cho phép 60", "toc_do_toi_da_cho_phep_60", "Tốc độ tối đa cho phép 60 km/h", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a1/Vietnam_road_sign_P127-60.svg/800px-Vietnam_road_sign_P127-60.svg.png"),
        TrafficSign(17, "Tốc độ tối đa cho phép 80", "toc_do_toi_da_cho_phep_80", "Tốc độ tối đa cho phép 80 km/h", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/10/Vietnam_road_sign_P127-80.svg/800px-Vietnam_road_sign_P127-80.svg.png"),
        TrafficSign(18, "Cấm sử dụng còi", "cam_su_dung_coi", "Cấm sử dụng còi", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/27/Vietnam_road_sign_P128.svg/800px-Vietnam_road_sign_P128.svg.png"),
        TrafficSign(19, "Cấm dừng và đỗ xe", "cam_dung_va_do_xe", "Biển báo này quy định các phương tiện không được dừng hoặc đỗ tại vị trí đặt biển, nhằm đảm bảo lưu thông thông thoáng và an toàn cho khu vực.", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7b/Vietnam_road_sign_P130.svg/800px-Vietnam_road_sign_P130.svg.png"),
        TrafficSign(20, "Cấm đỗ xe", "cam_do_xe", "Cấm đỗ xe", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/22/Vietnam_road_sign_P131a.svg/800px-Vietnam_road_sign_P131a.svg.png"),
        TrafficSign(21, "Cấm đỗ xe vào ngày lẻ", "cam_do_xe_vao_ngay_le", "Cấm đỗ xe vào ngày lẻ", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/Vietnam_road_sign_P131b.svg/800px-Vietnam_road_sign_P131b.svg.png"),
        TrafficSign(22, "Cấm đỗ xe vào ngày chẵn", "cam_do_xe_vao_ngay_chan", "Cấm đỗ xe vào ngày chẵn", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cc/Vietnam_road_sign_P131c.svg/800px-Vietnam_road_sign_P131c.svg.png"),
        TrafficSign(23, "Cấm rẽ trái và rẽ phải", "cam_re_trai_va_re_phai", "Cấm rẽ trái và rẽ phải", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/70/Vietnam_road_sign_P137.svg/800px-Vietnam_road_sign_P137.svg.png"),
        TrafficSign(24, "Chỗ ngoặt nguy hiểm vòng bên trái", "cho_ngoat_nguy_hiem_vong_ben_trai", "Chỗ ngoặt nguy hiểm vòng bên trái", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/43/Vietnam_road_sign_W201a.svg/800px-Vietnam_road_sign_W201a.svg.png"),
        TrafficSign(25, "Chỗ ngoặt nguy hiểm vòng bên phải", "cho_ngoat_nguy_hiem_vong_ben_phai", "Chỗ ngoặt nguy hiểm vòng bên phải", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/03/Vietnam_road_sign_W201b.svg/800px-Vietnam_road_sign_W201b.svg.png"),
        TrafficSign(26, "Nhiều chỗ ngoặt nguy hiểm liên tiếp chỗ đầu tiên sang trái", "nhieu_cho_ngoat_nguy_hiem_lien_tiep_cho_dau_tien_sang_trai", "Nhiều chỗ ngoặt nguy hiểm liên tiếp chỗ đầu tiên sang trái", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Vietnam_road_sign_W202a.svg/800px-Vietnam_road_sign_W202a.svg.png"),
        TrafficSign(27, "Nhiều chỗ ngoặt nguy hiểm liên tiếp chỗ đầu tiên sang phải", "nhieu_cho_ngoat_nguy_hiem_lien_tiep_cho_dau_tien_sang_phai", "Nhiều chỗ ngoặt nguy hiểm liên tiếp chỗ đầu tiên sang phải", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e4/Vietnam_road_sign_W202b.svg/800px-Vietnam_road_sign_W202b.svg.png"),
        TrafficSign(28, "Đường giao nhau", "duong_giao_nhau", "Biển báo này chỉ dẫn khu vực giao nhau giữa các con đường.", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cf/Vietnam_road_sign_W205a.svg/800px-Vietnam_road_sign_W205a.svg.png"),
        TrafficSign(29, "Giao nhau với đường không ưu tiên", "giao_nhau_voi_duong_khong_uu_tien", "Biển báo này chỉ dẫn giao nhau với đường không ưu tiên.", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Vietnam_road_sign_W207a.svg/800px-Vietnam_road_sign_W207a.svg.png"),
        TrafficSign(30, "Giao nhau với đường ưu tiên", "giao_nhau_voi_duong_uu_tien", "Biển báo này chỉ dẫn giao nhau với đường ưu tiên.", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c8/Vietnam_road_sign_W208.svg/800px-Vietnam_road_sign_W208.svg.png"),
        TrafficSign(31, "Đường có gờ giảm tốc", "duong_co_go_giam_toc", "Biển báo này cảnh báo có gờ giảm tốc trên đường.", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f8/Vietnam_road_sign_W221b.svg/800px-Vietnam_road_sign_W221b.svg.png"),
        TrafficSign(32, "Đường người đi bộ cắt ngang", "duong_nguoi_di_bo_cat_ngang", "Biển báo này cảnh báo đường có người đi bộ cắt ngang.", "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Vietnam_road_sign_W224.svg/800px-Vietnam_road_sign_W224.svg.png"),
        TrafficSign(33, "Trẻ em", "tre_em", "Biển báo này cảnh báo khu vực có trẻ em.", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/Vietnam_road_sign_W225.svg/800px-Vietnam_road_sign_W225.svg.png"),
        TrafficSign(34, "Công trường", "cong_truong", "Biển báo này chỉ dẫn khu vực công trường, nơi đang thi công.", "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/Vietnam_road_sign_W227.svg/800px-Vietnam_road_sign_W227.svg.png"),
        TrafficSign(35, "Đi chậm", "di_cham", "Biển báo này yêu cầu phương tiện di chuyển chậm trong khu vực được chỉ định.", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/Vietnam_road_sign_W245a.svg/800px-Vietnam_road_sign_W245a.svg.png"),
        TrafficSign(36, "Các xe chỉ được đi thẳng", "cac_xe_chi_duoc_di_thang", "Biển báo này chỉ dẫn các xe chỉ được phép đi thẳng, không được rẽ.", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Vietnam_road_sign_R301a.svg/800px-Vietnam_road_sign_R301a.svg.png"),
        TrafficSign(37, "Các xe chỉ được rẽ phải", "cac_xe_chi_duoc_re_phai", "Biển báo này chỉ dẫn các xe chỉ được phép rẽ phải.", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/77/Vietnam_road_sign_R301b.svg/800px-Vietnam_road_sign_R301b.svg.png"),
        TrafficSign(38, "Các xe chỉ được rẽ trái", "cac_xe_chi_duoc_re_trai", "Biển báo này chỉ dẫn các xe chỉ được phép rẽ trái.", "https://upload.wikimedia.org/wikipedia/commons/thumb/5/57/Vietnam_road_sign_R301c.svg/800px-Vietnam_road_sign_R301c.svg.png"),
        TrafficSign(39, "Phải đi vòng sang bên trái", "phai_di_vong_sang_ben_trai", "Biển báo này yêu cầu phương tiện phải đi vòng sang bên trái.", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cb/Vietnam_road_sign_R301e.svg/800px-Vietnam_road_sign_R301e.svg.png"),
        TrafficSign(40, "Phải đi vòng sang bên phải", "phai_di_vong_sang_ben_phai", "Biển báo này yêu cầu phương tiện phải đi vòng sang bên phải.", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Vietnam_road_sign_R301d.svg/800px-Vietnam_road_sign_R301d.svg.png"),
        TrafficSign(41, "Nơi giao nhau chạy theo vòng xuyến", "noi_giao_nhau_chay_theo_vong_xuyen", "Biển báo này chỉ dẫn nơi giao nhau chạy theo vòng xuyến.", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1b/Vietnam_road_sign_R303.svg/800px-Vietnam_road_sign_R303.svg.png"),
        TrafficSign(42, "Đường một chiều", "duong_mot_chieu", "Biển báo này chỉ dẫn đường một chiều, các phương tiện chỉ được phép đi theo hướng đó.", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Vietnam_road_sign_I407a.svg/800px-Vietnam_road_sign_I407a.svg.png"),
        TrafficSign(43, "Nơi đỗ xe", "noi_do_xe", "Biển báo này chỉ dẫn nơi đỗ xe cho các phương tiện.", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/68/Vietnam_road_sign_I408.svg/800px-Vietnam_road_sign_I408.svg.png"),
        TrafficSign(44, "Chỗ quay xe", "cho_quay_xe", "Biển báo này chỉ dẫn các chỗ quay xe hợp pháp.", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/03/Vietnam_road_sign_I409.svg/800px-Vietnam_road_sign_I409.svg.png"),
        TrafficSign(45, "Biển gộp làn đường theo phương tiện", "bien_gop_lan_duong_theo_phuong_tien", "Biển báo này chỉ dẫn gộp làn đường theo từng loại phương tiện.", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/60/Vietnam_road_sign_R415a.svg/1280px-Vietnam_road_sign_R415a.svg.png"),
        TrafficSign(46, "Vị trí người đi bộ sang ngang bên phải", "vi_tri_nguoi_di_bo_sang_ngang_ben_phai", "Biển báo này chỉ dẫn vị trí người đi bộ sang ngang bên phải.", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/66/Vietnam_road_sign_I423b.svg/800px-Vietnam_road_sign_I423b.svg.png"),
        TrafficSign(47, "Bệnh viện", "benh_vien", "Biển báo này chỉ dẫn khu vực gần bệnh viện, yêu cầu chú ý giảm tốc độ.", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Vietnam_road_sign_I425.svg/800px-Vietnam_road_sign_I425.svg.png"),
        TrafficSign(48, "Bến xe buýt", "ben_xe_buyt", "Biển báo này chỉ dẫn khu vực bến xe buýt, nơi đón và trả hành khách.", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Vietnam_road_sign_I434a.svg/800px-Vietnam_road_sign_I434a.svg.png"),
        TrafficSign(49, "Chỉ hướng đường", "chi_huong_duong", "Biển báo này chỉ dẫn hướng đi của đường, yêu cầu các phương tiện đi theo hướng đó.", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Vietnam_road_sign_I414a.svg/1280px-Vietnam_road_sign_I414a.svg.png"),
        TrafficSign(50, "Hầm chui qua đường cho người đi bộ", "ham_chui_qua_duong_cho_nguoi_di_bo", "Biển báo này chỉ dẫn hầm chui qua đường cho người đi bộ.", "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Vietnam_road_sign_I424c.svg/800px-Vietnam_road_sign_I424c.svg.png"),
        TrafficSign(51, "Đường cấm", "duong_cam", "Biển báo này chỉ dẫn khu vực đường cấm, không cho phép phương tiện đi qua.", "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5b/Vietnam_road_sign_P101.svg/800px-Vietnam_road_sign_P101.svg.png"),
        TrafficSign(52, "Cấm đi ngược chiều", "cam_di_nguoc_chieu", "Biển báo này chỉ dẫn cấm đi ngược chiều trên các con đường.", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f3/Vietnam_road_sign_P102.svg/800px-Vietnam_road_sign_P102.svg.png"),
        TrafficSign(53, "Cấm xe ô tô", "cam_xe_oto", "Biển báo này chỉ dẫn cấm xe ô tô đi qua khu vực này.", "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9c/Vietnam_road_sign_P103a.svg/800px-Vietnam_road_sign_P103a.svg.png"),
        TrafficSign(54, "Cấm xe ô tô rẽ trái", "cam_xe_oto_re_trai", "Biển báo này chỉ dẫn cấm xe ô tô rẽ trái tại ngã tư hoặc vị trí này.", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2b/Vietnam_road_sign_P103c.svg/800px-Vietnam_road_sign_P103c.svg.png"),
        TrafficSign(55, "Cấm xe ô tô rẽ phải", "cam_xe_oto_re_phai", "Biển báo này chỉ dẫn cấm xe ô tô rẽ phải tại ngã tư hoặc vị trí này.", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/Vietnam_road_sign_P103b.svg/800px-Vietnam_road_sign_P103b.svg.png"),
        TrafficSign(56, "Cấm xe ô tô tải", "cam_xe_oto_tai", "Biển báo này chỉ dẫn cấm xe ô tô tải vào khu vực này.", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/27/Vietnam_road_sign_P106a.svg/800px-Vietnam_road_sign_P106a.svg.png"),
        TrafficSign(57, "Cấm xe ô tô tải có khối lượng giới hạn", "cam_xe_oto_tai_co_khoi_luong_gioi_han", "Biển báo này chỉ dẫn cấm xe ô tô tải có khối lượng vượt quá giới hạn vào khu vực này.", "https://upload.wikimedia.org/wikipedia/commons/thumb/3/31/Vietnam_road_sign_P106b.svg/800px-Vietnam_road_sign_P106b.svg.png"),
        TrafficSign(58, "Hướng đi trên mỗi làn đường theo vạch kẻ", "huong_di_tren_moi_lan_duong_theo_vach_ke", "Biển báo này chỉ dẫn hướng đi trên mỗi làn đường theo vạch kẻ.", "https://bizweb.dktcdn.net/100/352/036/files/bien-bao-411.jpg?v=1575519562405"),
        TrafficSign(59, "Biển báo cấm", "bien_bao_cam", "Biển báo này thông báo một lệnh cấm chung cho các phương tiện hoặc hành vi cụ thể.", "https://carpla.vn/blog/wp-content/uploads/2023/11/cac-loai-bien-bao-cam.jpg"),
        TrafficSign(60, "Biển nguy hiểm", "bien_nguy_hiem", "Biển báo này cảnh báo các tình huống giao thông nguy hiểm, yêu cầu tài xế giảm tốc độ.", "https://cache.baohoxanh.com/blog/wp-content/uploads/2021/09/bien-bao-nguy-hiem.jpg"),
        TrafficSign(61, "Biển hiệu lệnh", "bien_hieu_lenh", "Biển báo này chỉ dẫn hành vi bắt buộc về hướng đi hoặc các hành động mà phương tiện cần tuân thủ.", "https://cdn.thuvienphapluat.vn/uploads/tintuc/2022/10/15/bien-bao-R.301.jpg")
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
