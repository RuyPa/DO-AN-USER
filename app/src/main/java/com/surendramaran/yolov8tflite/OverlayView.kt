package com.surendramaran.yolov8tflite

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import java.util.LinkedList
import kotlin.math.max

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results = listOf<BoundingBox>()
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()
    private var isDrawNumber = false


    private var bounds = Rect()

    init {
        initPaints()
    }

    fun clear() {
        textPaint.reset()
        textBackgroundPaint.reset()
        boxPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        boxPaint.color = ContextCompat.getColor(context!!, R.color.bounding_box_color)
        boxPaint.strokeWidth = 8F
        boxPaint.style = Paint.Style.STROKE
    }

//    override fun draw(canvas: Canvas) {
//        super.draw(canvas)
//
//        results.forEach {
//            val left = it.x1 * width
//            val top = it.y1 * height
//            val right = it.x2 * width
//            val bottom = it.y2 * height
//
//            canvas.drawRect(left, top, right, bottom, boxPaint)
////            val drawableText = it.clsName
//            val drawableText = "1"
//
//
//            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
//            val textWidth = bounds.width()
//            val textHeight = bounds.height()
//            canvas.drawRect(
//                left,
//                top,
//                left + textWidth + BOUNDING_RECT_TEXT_PADDING,
//                top + textHeight + BOUNDING_RECT_TEXT_PADDING,
//                textBackgroundPaint
//            )
//            canvas.drawText(drawableText, left, top + bounds.height(), textPaint)
//
//        }
//    }
    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        results.forEachIndexed { index, it ->
            val left = it.x1 * width
            val top = it.y1 * height
            val right = it.x2 * width
            val bottom = it.y2 * height

            // Vẽ bounding box
            canvas.drawRect(left, top, right, bottom, boxPaint)

            // Nội dung vẽ: số thứ tự hoặc tên class
            val drawableText = if (isDrawNumber) {
                (index + 1).toString() // Vẽ số thứ tự
            } else {
                it.clsName // Vẽ tên class
            }

            // Vẽ nền text
            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height()
            canvas.drawRect(
                left,
                top,
                left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                textBackgroundPaint
            )

            // Vẽ text
            canvas.drawText(drawableText, left, top + bounds.height(), textPaint)
        }
    }

    fun getDetectedLabels(): List<Pair<Int, String>> {
        val detectedLabels = mutableListOf<Pair<Int, String>>()

        results.forEachIndexed { index, it ->
            val label = it.clsName
            detectedLabels.add(Pair(index + 1, label))  // Thêm số thứ tự và tên class vào danh sách
        }

        return detectedLabels
    }
    fun setResults(boundingBoxes: List<BoundingBox>) {
        results = boundingBoxes
        invalidate()
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }

    fun getResults(): List<BoundingBox> {
        return results
    }


    fun setDrawMode(drawNumber: Boolean) {
        isDrawNumber = drawNumber
        invalidate() // Gọi lại hàm `draw` để cập nhật nội dung vẽ
    }
}