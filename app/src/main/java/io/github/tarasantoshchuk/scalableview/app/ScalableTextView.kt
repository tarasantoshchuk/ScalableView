package io.github.tarasantoshchuk.scalableview.app

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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return scalableViewDelegate.onTouchEvent(event)
    }
}