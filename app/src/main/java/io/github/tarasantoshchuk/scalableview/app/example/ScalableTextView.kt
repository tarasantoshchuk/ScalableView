package io.github.tarasantoshchuk.scalableview.app.example

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView
import io.github.tarasantoshchuk.scalableview.ScalableViewDelegate


class ScalableTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {
    private val scalableViewDelegate = ScalableViewDelegate.newInstance(this, attrs)

    override fun draw(canvas: Canvas) {
        scalableViewDelegate.draw(canvas) {
            super.draw(canvas)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return scalableViewDelegate.dispatchTouchEvent(event) {
            super.dispatchTouchEvent(it)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return scalableViewDelegate.onTouchEvent(event)
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