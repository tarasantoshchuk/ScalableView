package io.github.tarasantoshchuk.scalableview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


open class BaseScalableView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val scalableViewDelegate: ScalableViewDelegate = ScalableViewDelegate.newInstance(this, attrs)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return scalableViewDelegate.onTouchEvent(event, this::onScaledTouchEvent)
    }

    override fun draw(canvas: Canvas) {
        scalableViewDelegate.draw(canvas) {
            super.draw(canvas)
        }
    }

    open fun onScaledTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}

