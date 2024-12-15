package com.surendramaran.yolov8tflite

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.surendramaran.yolov8tflite.Constants.LABELS_PATH
import com.surendramaran.yolov8tflite.Constants.MODEL_PATH
import com.surendramaran.yolov8tflite.databinding.ActivityMainBinding
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity(), Detector.DetectorListener {
    private var isFrontCamera = false

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var tts: TextToSpeech
    private lateinit var queue: Queue<String>
    private var isSpeaking = false
    private var cameraProvider: ProcessCameraProvider? = null
        private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private val REQUEST_CODE_PERMISSIONS = 10
    private lateinit var detector: Detector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        queue = LinkedList()

        tts = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("vi", "VN")
            }
        })

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                isSpeaking = false
                if (queue.isNotEmpty()) {
                    val nextLabel = queue.poll()
                    isSpeaking = true
                    tts.speak(nextLabel, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
                }
            }

            override fun onError(utteranceId: String?) {
                isSpeaking = false
            }
        })
        startCameraFunctionality()


        val backIcon = findViewById<ImageView>(R.id.back_icon)
        backIcon.setOnClickListener { finish() }

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
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

    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        runOnUiThread {
            if (isSpeaking) return@runOnUiThread
            for (boundingBox in boundingBoxes) {
                var label = boundingBox.clsName
                label = when (label) {
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
                    else -> label
                }

                if (!queue.contains(label)) {
                    queue.add("Phía trước có biển báo: $label")
                }
            }

            if (queue.isNotEmpty() && !tts.isSpeaking) {
                val detectedLabel = queue.poll()
                tts.speak(detectedLabel, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
            }
        }
    }

    override fun onEmptyDetect() {
        binding.overlay.invalidate()
    }
}
