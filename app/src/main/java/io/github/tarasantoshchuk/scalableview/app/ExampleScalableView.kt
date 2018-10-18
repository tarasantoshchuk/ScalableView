package io.github.tarasantoshchuk.scalableview.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import io.github.tarasantoshchuk.scalableview.BaseScalableView


class ExampleScalableView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseScalableView(context, attrs, defStyleAttr) {
    private val circlePaint = Paint()

    init {
        circlePaint.color = Color.RED
        circlePaint.style = Paint.Style.STROKE
        circlePaint.strokeWidth = 10f
        circlePaint.isAntiAlias = true
    }

    private val rect by lazy { Rect(5, 5, width-5, height-5) }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(100f, 100f, 50f, circlePaint)
        canvas.drawLine(20f, 30f, 200f, 420f, circlePaint)
        canvas.drawRect(rect, circlePaint)
    }

    override fun onScaledTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}