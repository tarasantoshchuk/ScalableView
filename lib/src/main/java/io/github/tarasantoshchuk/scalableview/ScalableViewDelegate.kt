package io.github.tarasantoshchuk.scalableview

import android.graphics.Canvas
import android.graphics.PointF
import android.support.annotation.FloatRange
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

interface ScalableViewDelegate {
    fun setMaxScale(@FloatRange(from = 1.0) scale: Float)
    fun setEnableScaling(enableScaling: Boolean)

    fun onTouchEvent(event: MotionEvent, block: (MotionEvent) -> Boolean = {false}): Boolean
    fun draw(canvas: Canvas, block: (Canvas) -> Unit)

    companion object {
        fun newInstance(view: View, attrs: AttributeSet?): ScalableViewDelegate {
            return ScalableViewDelegateImpl(view, attrs)
        }
    }
}

internal class ScalableViewDelegateImpl(val view: View, attrs: AttributeSet?) : ScalableViewDelegate {

    private val tempPointF: PointF

    private var factor = 1f
    private val pivot: PointF

    private var maxScale = Float.POSITIVE_INFINITY
    private var enableScaling = true

    private val scalableCanvas: ScalableCanvas

    private val scaleDetector: ScaleGestureDetector
    private val gestureDetector: GestureDetector

    override fun setMaxScale(scale: Float) {
        maxScale = scale
    }

    override fun setEnableScaling(enableScaling: Boolean) {
        this.enableScaling = enableScaling
    }

    override fun onTouchEvent(event: MotionEvent, block: (MotionEvent) -> Boolean): Boolean {
        val scaleDetected = scaleDetector.onTouchEvent(event)
        val scrollDetected = gestureDetector.onTouchEvent(event)

        tempPointF.set(event.x, event.y)
        tempPointF.scale(pivot, 1f / factor)
        event.setLocation(tempPointF)

        val importantForCustomTouchHandling = block(event)
        return scaleDetected || scrollDetected || importantForCustomTouchHandling
    }

    override fun draw(canvas: Canvas, block: (Canvas) -> Unit) {
        scalableCanvas.setMatrix {
            it.setScale(pivot, factor)
        }

        scalableCanvas.apply(canvas, block)
    }

    private fun scale(rPivot: PointF, rFactor: Float) {
        val newFactor = factor * rFactor

        if (newFactor != 1f) {
            val newPivotX = (rFactor * (1 - factor) * pivot.x + (1 - rFactor) * rPivot.x) / (1f - newFactor)
            val newPivotY = (rFactor * (1 - factor) * pivot.y + (1 - rFactor) * rPivot.y) / (1f - newFactor)
            pivot.set(newPivotX, newPivotY)
        }

        factor = newFactor

        onPostUpdateScale()
    }

    private fun onPostUpdateScale() {
        factor = factor.clamp(1f, maxScale)

        pivot.x = pivot.x.clamp( 0f, view.width.toFloat())
        pivot.y = pivot.y.clamp(0f, view.height.toFloat())
    }

    private fun reset() {
        factor = 1f
        pivot.set(0f, 0f)
    }

    private fun scroll(distanceX: Float, distanceY: Float) {
        if (factor == 1f) {
            return
        }

        pivot.x += distanceX / (factor - 1f)
        pivot.y += distanceY / (factor - 1f)

        onPostUpdateScale()
    }

    init {
        val arr = view.context.obtainStyledAttributes(attrs, R.styleable.ScalableView)
        setMaxScale(arr.getFloat(R.styleable.ScalableView_maxScale, Float.POSITIVE_INFINITY))
        setEnableScaling(arr.getBoolean(R.styleable.ScalableView_enableScaling, true))
        arr.recycle()
        tempPointF = PointF()
        pivot = PointF()
        scalableCanvas = ScalableCanvas()
        scaleDetector = ScaleGestureDetector(view.context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                detector.apply {
                    tempPointF.set(focusX, focusY)
                    scale(tempPointF, scaleFactor)
                    view.invalidate()
                }

                return true
            }
        })
        gestureDetector = GestureDetector(view.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                scroll(distanceX, distanceY)
                view.invalidate()
                return true
            }

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                reset()
                view.invalidate()
                return true
            }
        })
    }
}