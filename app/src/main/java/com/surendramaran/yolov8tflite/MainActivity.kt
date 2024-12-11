package com.surendramaran.yolov8tflite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.surendramaran.yolov8tflite.Constants.LABELS_PATH
import com.surendramaran.yolov8tflite.Constants.MODEL_PATH
import com.surendramaran.yolov8tflite.databinding.ActivityMainBinding
import java.util.LinkedList
import java.util.Locale
import java.util.Queue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), Detector.DetectorListener {
    private lateinit var binding: ActivityMainBinding
    private var isFrontCamera = false

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var detector: Detector
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var horizontalScrollView: HorizontalScrollView
    private lateinit var categoryLayout: LinearLayout

    private val scrollHandler = Handler(Looper.getMainLooper())
    private var currentScrollPosition = 0
    private val totalScrollDistance = 300 // Tổng khoảng cách cần cuộn (300 pixel)
    private val scrollStep = 5 // Cuộn 5 pixel mỗi bước
    private val scrollInterval = 10L // Thực hiện cuộn sau mỗi 10ms
    private lateinit var queue: Queue<String> // Hàng đợi chứa tên các biển báo
    private lateinit var tts: TextToSpeech // Text-to-Speech instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load giao diện detect.xml đầu tiên khi ứng dụng khởi động
        setContentView(R.layout.detect)
        val imageView: ImageView = findViewById(R.id.illustrationimage)
        imageView.setBackgroundColor(android.graphics.Color.TRANSPARENT) // Đặt nền trong suốt

        queue = LinkedList() // Khởi tạo hàng đợi
        tts = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val langResult = tts.setLanguage(Locale("vi", "VN"))
            }
        })
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

        horizontalScrollView = findViewById(R.id.horizontalScrollView)
        categoryLayout = findViewById(R.id.categories_layout)
        // Tìm nút detect_image
        val detectImageButton: Button = findViewById(R.id.detect_image)

        // Xử lý sự kiện khi bấm nút
        detectImageButton.setOnClickListener {
            // Mở giao diện DetectImage
            val intent = Intent(this, DetectImage::class.java)
            startActivity(intent)
        }

        val detectVideoButton: Button = findViewById(R.id.dectect_video)

        // Xử lý sự kiện khi bấm nút
        detectVideoButton.setOnClickListener {
            // Mở giao diện DetectImage
            val intent = Intent(this, DetectVideo::class.java)
            startActivity(intent)
        }


        startInfiniteScroll()


        // Nút để bắt đầu camera
        val detectButton = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.buttonDetect)

        val bienBaoCamLayout = findViewById<LinearLayout>(R.id.bien_bao_cam_linear)

        // Đặt sự kiện click cho LinearLayout
        bienBaoCamLayout.setOnClickListener {
            // Khởi động TrafficSignActivity khi bấm vào
            val intent = Intent(this, TrafficSignActivity::class.java)
            startActivity(intent)
        }

        // Khi bấm nút, chuyển sang giao diện camera và khởi động chức năng chính
        detectButton.setOnClickListener {
            startCameraFunctionality()
        }
    }

    private fun startInfiniteScroll() {
        scrollHandler.postDelayed(object : Runnable {
            override fun run() {
                // Cuộn mượt 300 pixel trong 2 giây
                smoothScroll()

                // Khi cuộn đến gần cuối, di chuyển phần tử đầu tiên xuống cuối
                if (horizontalScrollView.scrollX >= horizontalScrollView.getChildAt(0).width - horizontalScrollView.width) {
                    shiftFirstElementToEnd()
                    currentScrollPosition = 0
                    horizontalScrollView.scrollTo(0, 0)
                }

                // Tiếp tục sau mỗi 2 giây
                scrollHandler.postDelayed(this, 2000)
            }
        }, 2000) // Bắt đầu sau 2 giây
    }

    private fun smoothScroll() {
        val totalSteps = totalScrollDistance / scrollStep
        var stepCount = 0

        val scrollRunnable = object : Runnable {
            override fun run() {
                if (stepCount < totalSteps) {
                    // Cuộn từng bước nhỏ
                    horizontalScrollView.smoothScrollBy(scrollStep, 0)
                    stepCount++
                    // Tiếp tục cuộn cho đến khi đạt tổng khoảng cách
                    scrollHandler.postDelayed(this, scrollInterval)
                }
            }
        }

        // Bắt đầu cuộn mượt
        scrollHandler.post(scrollRunnable)
    }

    private fun shiftFirstElementToEnd() {
        val firstView = categoryLayout.getChildAt(0)
        categoryLayout.removeViewAt(0)
        categoryLayout.addView(firstView)
    }
    private fun startCameraFunctionality() {
        // Sau khi nhấn nút, chuyển sang giao diện camera
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo detector
        detector = Detector(baseContext, MODEL_PATH, LABELS_PATH, this)
        detector.setup()

        // Kiểm tra và yêu cầu quyền camera
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        val backIcon = findViewById<ImageView>(R.id.back_icon)
        backIcon.setOnClickListener {
            finish()
        }



        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        // Kiểm tra nếu binding đã được khởi tạo
        if (!::binding.isInitialized) {
            Log.e(TAG, "Binding has not been initialized yet.")
            return
        }

        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val rotation = binding.viewFinder.display.rotation

        val cameraSelector = CameraSelector
            .Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(rotation)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()

        imageAnalyzer?.setAnalyzer(cameraExecutor) { imageProxy ->
            val bitmapBuffer = Bitmap.createBitmap(
                imageProxy.width,
                imageProxy.height,
                Bitmap.Config.ARGB_8888
            )
            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
            imageProxy.close()

            val matrix = Matrix().apply {
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

                if (isFrontCamera) {
                    postScale(-1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat())
                }
            }

            val rotatedBitmap = Bitmap.createBitmap(
                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height, matrix, true
            )

            detector.detect(rotatedBitmap)
        }

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalyzer
            )

            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
        if (it[Manifest.permission.CAMERA] == true) { startCamera() }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        scrollHandler.removeCallbacksAndMessages(null)
//        detector.clear()
//        cameraExecutor.shutdown()
//    }
    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
        detector.clear()
        cameraExecutor.shutdown()
    }


    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    companion object {
        private const val TAG = "Camera"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).toTypedArray()
    }

    override fun onEmptyDetect() {
        binding.overlay.invalidate()
    }

//    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
//        runOnUiThread {
//            binding.inferenceTime.text = "${inferenceTime}ms"
//            binding.overlay.apply {
//                setResults(boundingBoxes)
//                invalidate()
//            }
//        }
//    }
override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
    runOnUiThread {
        // Thêm tên biển báo vào hàng đợi
        for (boundingBox in boundingBoxes) {
            var label = boundingBox.clsName // Lấy nhãn của biển báo

            // Chuyển nhãn biển báo sang tiếng Việt
            label = when (label) {
//                "cam_di_nguoc_chieu" -> "Cấm đi ngược chiều"
//                "cam_dung_va_do_xe" -> "Cấm dừng và đỗ xe"
//                "cam_re_trai" -> "Cấm rẽ trái"
//                "gioi_han_toc_do" -> "Giới hạn tốc độ"
//                "bien_bao_cam" -> "Biển báo cấm"
//                "bien_nguy_hiem" -> "Biển nguy hiểm"
//                "bien_hieu_lenh" -> "Biển hiệu lệnh"
                "cam_o_to_khach_va_o_to_tai" -> "Cấm ô tô khách và ô tô tải"
                "cam_xe_oto_khach" -> "Cấm xe ô tô khách"
                "cam_di_xe_dap" -> "Cấm đi xe đạp"
                "cam_xe_gan_may" -> "Cấm xe gắn máy"
                "cam_xe_lam" -> "Cấm xe lam"
                "cam_nguoi_đi_bo" -> "Cấm người đi bộ"
                "han_che_chieu_cao" -> "Hạn chế chiều cao"
                "cam_re_trai" -> "Cấm rẽ trái"
                "cam_re_phai" -> "Cấm rẽ phải"
                "cam_quay_xe" -> "Cấm quay xe"
                "cam_oto_quay_dau_xe" -> "Cấm ô tô quay đầu xe"
                "cam_re_trai_va_quay_dau_xe" -> "Cấm rẽ trái và quay đầu xe"
                "cam_oto_re_trai_va_quay_dau_xe" -> "Cấm ô tô rẽ trái và quay đầu xe"
                "cam_vuot" -> "Cấm vượt"
                "toc_do_toi_da_cho_phep_40" -> "Tốc độ tối đa cho phép 40"
                "toc_do_toi_da_cho_phep_50" -> "Tốc độ tối đa cho phép 50"
                "toc_do_toi_da_cho_phep_60" -> "Tốc độ tối đa cho phép 60"
                "toc_do_toi_da_cho_phep_80" -> "Tốc độ tối đa cho phép 80"
                "cam_su_dung_coi" -> "Cấm sử dụng còi"
                "cam_dung_va_do_xe" -> "Cấm dừng và đỗ xe"
                "cam_do_xe" -> "Cấm đỗ xe"
                "cam_do_xe_vao_ngay_le" -> "Cấm đỗ xe vào ngày lễ"
                "cam_do_xe_vao_ngay_chan" -> "Cấm đỗ xe vào ngày chẵn"
                "cam_re_trai_va_re_phai" -> "Cấm rẽ trái và rẽ phải"
                "cho_ngoat_nguy_hiem_vong_ben_trai" -> "Chỗ ngoặt nguy hiểm vòng bên trái"
                "cho_ngoat_nguy_hiem_vong_ben_phai" -> "Chỗ ngoặt nguy hiểm vòng bên phải"
                "nhieu_cho_ngoat_nguy_hiem_lien_tiep_cho_dau_tien_sang_trai" -> "Nhiều chỗ ngoặt nguy hiểm liên tiếp chỗ đầu tiên sang trái"
                "nhieu_cho_ngoat_nguy_hiem_lien_tiep_cho_dau_tien_sang_phai" -> "Nhiều chỗ ngoặt nguy hiểm liên tiếp chỗ đầu tiên sang phải"
                "duong_giao_nhau" -> "Đường giao nhau"
                "giao_nhau_voi_duong_khong_uu_tien" -> "Giao nhau với đường không ưu tiên"
                "giao_nhau_voi_duong_uu_tien" -> "Giao nhau với đường ưu tiên"
                "duong_co_go_giam_toc" -> "Đường có gờ giảm tốc"
                "duong_nguoi_di_bo_cat_ngang" -> "Đường người đi bộ cắt ngang"
                "tre_em" -> "Trẻ em"
                "cong_truong" -> "Công trường"
                "di_cham" -> "Đi chậm"
                "cac_xe_chi_duoc_di_thang" -> "Các xe chỉ được đi thẳng"
                "cac_xe_chi_duoc_re_phai" -> "Các xe chỉ được rẽ phải"
                "cac_xe_chi_duoc_re_trai" -> "Các xe chỉ được rẽ trái"
                "phai_di_vong_sang_ben_trai" -> "Phải đi vòng sang bên trái"
                "phai_di_vong_sang_ben_phai" -> "Phải đi vòng sang bên phải"
                "noi_giao_nhau_chay_theo_vong_xuyen" -> "Nơi giao nhau chạy theo vòng xuyến"
                "duong_mot_chieu" -> "Đường một chiều"
                "noi_do_xe" -> "Nơi đỗ xe"
                "cho_quay_xe" -> "Chỗ quay xe"
                "bien_gop_lan_duong_theo_phuong_tien" -> "Biển gộp làn đường theo phương tiện"
                "vi_tri_nguoi_di_bo_sang_ngang_ben_phai" -> "Vị trí người đi bộ sang ngang bên phải"
                "benh_vien" -> "Bệnh viện"
                "ben_xe_buyt" -> "Bến xe buýt"
                "chi_huong_duong" -> "Chỉ hướng đường"
                "ham_chui_qua_duong_cho_nguoi_di_bo" -> "Hầm chui qua đường cho người đi bộ"
                "duong_cam" -> "Đường cấm"
                "cam_di_nguoc_chieu" -> "Cấm đi ngược chiều"
                "cam_xe_oto" -> "Cấm xe ô tô"
                "cam_xe_oto_re_trai" -> "Cấm xe ô tô rẽ trái"
                "cam_xe_oto_re_phai" -> "Cấm xe ô tô rẽ phải"
                "cam_xe_oto_tai" -> "Cấm xe ô tô tải"
                "cam_xe_oto_tai_co_khoi_luong_gioi_han" -> "Cấm xe ô tô tải có khối lượng giới hạn"
                "huong_di_tren_moi_lan_duong_theo_vach_ke" -> "Hướng đi trên mỗi làn đường theo vạch kẻ"
                "bien_bao_cam" -> "Biển báo cấm"
                "bien_nguy_hiem" -> "Biển nguy hiểm"
                "bien_hieu_lenh" -> "Biển hiệu lệnh"

                else ->  label // Nếu không có trong danh sách, giữ nguyên tên nhãn
            }

            // Kiểm tra xem biển báo đã có trong hàng đợi chưa
            if (!queue.contains(label)) {
                queue.add("phía trước có biển báo" + label)
            }
        }


        // Xử lý hàng đợi và phát âm thanh
        if (queue.isNotEmpty()) {
            val detectedLabel = queue.poll() // Lấy tên biển báo từ hàng đợi
            tts.speak(detectedLabel, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
}

}
