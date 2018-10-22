package io.github.tarasantoshchuk.scalableview

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.support.annotation.FloatRange
import android.util.AttributeSet
import android.view.*

interface ScalableViewDelegate {
    fun setMaxScale(@FloatRange(from = 1.0) scale: Float)
    fun setEnableScaling(enableScaling: Boolean)
    fun setEnableScrolling(enableScrolling: Boolean)
    fun reset()

    fun onTouchEvent(event: MotionEvent, block: (MotionEvent) -> Boolean = { false }): Boolean
    fun onInterceptTouchEvent(ev: MotionEvent, block: (MotionEvent) -> Boolean = { false }): Boolean
    fun dispatchTouchEvent(event: MotionEvent, block: (MotionEvent) -> Boolean = { false }): Boolean
    fun draw(canvas: Canvas, block: (Canvas) -> Unit)

    companion object {
        fun newInstance(view: View, attrs: AttributeSet?): ScalableViewDelegate {
            return ScalableViewDelegateImpl(view, attrs)
        }
    }
}

internal class ScalableViewDelegateImpl(val view: View, attrs: AttributeSet?) : ScalableViewDelegate {

    private val tempPointF = PointF()

    private var factor = 1f
    private val pivot = PointF()

    private var maxScale = Float.POSITIVE_INFINITY
    private var enableScaling = true
    private var enableScrolling = true

    private var scalingStarted = false

    private val scalableCanvas = ScalableCanvas()

    private val scaleDetector: ScaleGestureDetector
    private val gestureDetector: GestureDetector

    private val touchEventMatrix = Matrix()

    override fun setMaxScale(scale: Float) {
        maxScale = scale
        factor = Math.min(factor, maxScale)
        view.invalidate()
    }

    override fun setEnableScaling(enableScaling: Boolean) {
        this.enableScaling = enableScaling
    }

    override fun setEnableScrolling(enableScrolling: Boolean) {
        this.enableScrolling = enableScrolling
    }

    override fun onTouchEvent(event: MotionEvent, block: (MotionEvent) -> Boolean): Boolean {
        scalableCanvas.getMatrix().invert(touchEventMatrix)

        event.transform(scalableCanvas.getMatrix())

        scaleDetector.onTouchEvent(event)
        val scrollDetected = gestureDetector.onTouchEvent(event)

        val internalTouchHandled = scalingStarted || scrollDetected

        event.transform(touchEventMatrix)

        val customTouchHandled = block(event)

        return internalTouchHandled || customTouchHandled
    }

    override fun onInterceptTouchEvent(ev: MotionEvent, block: (MotionEvent) -> Boolean): Boolean {
        scalableCanvas.getMatrix().invert(touchEventMatrix)

        ev.transform(scalableCanvas.getMatrix())

        scaleDetector.onTouchEvent(ev)
        val scrollDetected = gestureDetector.onTouchEvent(ev)

        val canPerformInternalIntercept = enableScrolling || enableScaling

        val internalInterceptTouch = (scalingStarted || scrollDetected) && canPerformInternalIntercept && ev.action != MotionEvent.ACTION_DOWN

        ev.transform(touchEventMatrix)

        val customInterceptTouch = block(ev)

        return internalInterceptTouch || customInterceptTouch
    }

    override fun dispatchTouchEvent(event: MotionEvent, block: (MotionEvent) -> Boolean): Boolean {
        scalableCanvas.getMatrix().invert(touchEventMatrix)

        event.transform(touchEventMatrix)

        return block(event)
    }

    override fun draw(canvas: Canvas, block: (Canvas) -> Unit) {
        scalableCanvas.setMatrix {
            it.setScale(pivot, factor)
        }

        scalableCanvas.apply(canvas, block)
    }

    override fun reset() {
        factor = 1f
        pivot.set(0f, 0f)

        view.invalidate()
    }

    private fun scale(rPivot: PointF, rFactor: Float): Boolean {
        val (newFactor, effectiveRFactor) = clampFactor(rFactor)

        if (newFactor != 1f && effectiveRFactor != 1f) {
            val newPivotX = (rFactor * (1 - factor) * pivot.x + (1 - effectiveRFactor) * rPivot.x) / (1f - newFactor)
            val newPivotY = (rFactor * (1 - factor) * pivot.y + (1 - effectiveRFactor) * rPivot.y) / (1f - newFactor)
            pivot.set(newPivotX, newPivotY)

            factor = newFactor
            clampPivot()
            view.invalidate()
            return true
        }

        return false
    }

    private fun clampFactor(rFactor: Float): Pair<Float, Float> {
        val newFactor = (rFactor * factor).clamp(1f, maxScale)
        val effectiveRFactor = newFactor / factor

        return Pair(newFactor, effectiveRFactor)
    }

    private fun clampPivot() {
        pivot.x = pivot.x.clamp(0f, view.width.toFloat())
        pivot.y = pivot.y.clamp(0f, view.height.toFloat())
    }

    private fun scroll(distanceX: Float, distanceY: Float): Boolean {
        if (factor == 1f) {
            return false
        }

        tempPointF.set(pivot)

        pivot.x += distanceX / (factor - 1f)
        pivot.y += distanceY / (factor - 1f)

        clampPivot()

        return (tempPointF != pivot)
                .doIfTrue {
                    view.invalidate()
                }
    }

    init {
        val arr = view.context.obtainStyledAttributes(attrs, R.styleable.ScalableView)
        setMaxScale(arr.getFloat(R.styleable.ScalableView_maxScale, Float.POSITIVE_INFINITY))
        setEnableScaling(arr.getBoolean(R.styleable.ScalableView_enableScaling, true))
        arr.recycle()

        scaleDetector = ScaleGestureDetector(view.context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                scalingStarted = true
                return enableScaling
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                return detector.let {
                    tempPointF.set(it.focusX, it.focusY)
                    scale(tempPointF, it.scaleFactor)
                }
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {
                scalingStarted = false
            }
        })

        gestureDetector = GestureDetector(view.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                return enableScrolling
            }

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                return enableScrolling && scroll(distanceX, distanceY)
            }
        })
    }
}