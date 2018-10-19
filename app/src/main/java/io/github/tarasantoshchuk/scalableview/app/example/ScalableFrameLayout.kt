package io.github.tarasantoshchuk.scalableview.app.example

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import io.github.tarasantoshchuk.scalableview.ScalableViewDelegate


class ScalableFrameLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val scalableViewDelegate = ScalableViewDelegate.newInstance(this, attrs)

    override fun draw(canvas: Canvas) {
        scalableViewDelegate.draw(canvas) {
            super.draw(canvas)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return scalableViewDelegate.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return scalableViewDelegate.onInterceptTouchEvent(ev)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return scalableViewDelegate.dispatchTouchEvent(ev) {
            super.dispatchTouchEvent(it)
        }
    }

    fun reset() {
        scalableViewDelegate.reset()
    }

    fun setMaxScale(maxScale: Float) {
        scalableViewDelegate.setMaxScale(maxScale)
    }

    fun setEnableScaling(enableScaling: Boolean) {
        scalableViewDelegate.setEnableScaling(enableScaling)
    }

    fun setEnableScrolling(enableScrolling: Boolean) {
        scalableViewDelegate.setEnableScrolling(enableScrolling)
    }
}