package com.surendramaran.yolov8tflite

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class DetectVideo : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var imageView: ImageView
    private lateinit var btnPlay: Button
    private lateinit var btnPause: Button
    private lateinit var btnDetect: Button
    private lateinit var detector: Detector
    private var selectedVideoUri: Uri? = null
    private val PICK_VIDEO_REQUEST = 1

    private val frameInterval = 2000L // Increase the time between frames (2 seconds between frames)
    private val frameHandler = Handler(Looper.getMainLooper())
    private var isDetecting = false

    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var mediaPlayer: MediaPlayer

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detect_video)

        // Check for READ_EXTERNAL_STORAGE permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }

        // Initialize UI elements
        videoView = findViewById(R.id.videoView)
        imageView = findViewById(R.id.imageView)
        btnPlay = findViewById(R.id.btnPlay)
        btnPause = findViewById(R.id.btnPause)
        btnDetect = findViewById(R.id.btnDetect)
        val btnSelectVideo: Button = findViewById(R.id.btnSelectVideo)
        videoView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        videoView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

        // Initialize SurfaceHolder for video rendering
        surfaceHolder = videoView.holder
        surfaceHolder.setKeepScreenOn(true)

        // Initialize the detector
        detector = Detector(
            baseContext,
            Constants.MODEL_PATH,
            Constants.LABELS_PATH,
            object : Detector.DetectorListener {
                override fun onEmptyDetect() {
                    runOnUiThread {
                        Toast.makeText(
                            this@DetectVideo,
                            "No objects detected!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
                    runOnUiThread {
                        Toast.makeText(
                            this@DetectVideo,
                            "Detected ${boundingBoxes.size} objects!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        detector.setup()

        // Select video button
        btnSelectVideo.setOnClickListener {
            openVideoPicker()
        }

        // Play button
        btnPlay.setOnClickListener {
            startVideoWithSlowerSpeed()
            btnPlay.visibility = View.GONE
            btnPause.visibility = View.VISIBLE
        }

        // Pause button
        btnPause.setOnClickListener {
            mediaPlayer.pause()
            btnPlay.visibility = View.VISIBLE
            btnPause.visibility = View.GONE
        }

        // Detect button
        btnDetect.setOnClickListener {
            if (selectedVideoUri != null) {
                processVideo()
            } else {
                Toast.makeText(this, "Please select a video first!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Open video picker
    private fun openVideoPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.data
            if (selectedVideoUri != null) {
                videoView.setVideoURI(selectedVideoUri)
                videoView.visibility = View.VISIBLE
                btnPlay.visibility = View.VISIBLE
                btnPause.visibility = View.GONE
                Toast.makeText(this, "Video selected successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to select video!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Start video with reduced playback speed
    @RequiresApi(Build.VERSION_CODES.M)
    private fun startVideoWithSlowerSpeed() {
        if (selectedVideoUri != null) {
            mediaPlayer = MediaPlayer.create(this, selectedVideoUri)

            // Set the playback speed to 50% (slower)
            mediaPlayer.setPlaybackParams(mediaPlayer.playbackParams.setSpeed(1.5f))

            // Set Surface for rendering the video
            mediaPlayer.setDisplay(surfaceHolder)

            // Start video
            mediaPlayer.start()
        }
    }

    // Process the video for real-time detection
    private fun processVideo() {
        if (selectedVideoUri == null) {
            Toast.makeText(this, "Please select a video first!", Toast.LENGTH_SHORT).show()
            return
        }

        // Start the video playback
        videoView.setVideoURI(selectedVideoUri)
        videoView.start()

        // Begin real-time frame extraction and detection
        isDetecting = true
        frameHandler.postDelayed(object : Runnable {
            override fun run() {
                if (isDetecting) {
                    // Capture the current frame of the video
                    val currentPosition = videoView.currentPosition
                    val retriever = MediaMetadataRetriever()
                    try {
                        contentResolver.openFileDescriptor(selectedVideoUri!!, "r")?.use { pfd ->
                            retriever.setDataSource(pfd.fileDescriptor)
                        }
                        val frameBitmap =
                            retriever.getFrameAtTime(
                                currentPosition * 1000L,
                                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                            )
                        if (frameBitmap != null) {
                            // Hiển thị Snackbar thông báo có frameBitmap
//                            Snackbar.make(findViewById(android.R.id.content), "FrameBitmap is available!", Snackbar.LENGTH_SHORT).show()

                            // Sau đó, tiếp tục xử lý nhận diện
                            detectImage(frameBitmap)
                        }
                    } catch (e: Exception) {
                        Log.e("DetectVideo", "Error processing video frame", e)
                    } finally {
                        retriever.release()
                    }

                    // Schedule the next frame processing
                    frameHandler.postDelayed(this, frameInterval)
                }
            }
        }, 0) // Start immediately
    }

    // Detect objects in the captured frame
    private fun detectImage(bitmap: Bitmap) {
        detector.detect(bitmap)
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.clear()
        isDetecting = false
        frameHandler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }
}
