package io.github.tarasantoshchuk.scalableview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.annotation.CallSuper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


open class BaseScalableView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    @Suppress("LeakingThis")
    private val scalableViewDelegate: ScalableViewDelegate = ScalableViewDelegate.newInstance(this, attrs)

    @SuppressLint("ClickableViewAccessibility")
    final override fun onTouchEvent(event: MotionEvent): Boolean {
        return scalableViewDelegate.onTouchEvent(event, this::onScaledTouchEvent)
    }

    @SuppressLint("MissingSuperCall")
    final override fun draw(canvas: Canvas) {
        scalableViewDelegate.draw(canvas) {
            scaledDraw(canvas)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return scalableViewDelegate.dispatchTouchEvent(event) {
            super.dispatchTouchEvent(it)
        }
    }

    open fun reset() {
        scalableViewDelegate.reset()
    }

    open fun setMaxScale(maxScale: Float) {
        scalableViewDelegate.setMaxScale(maxScale)
    }

    open fun setEnableScaling(enableScaling: Boolean) {
        scalableViewDelegate.setEnableScaling(enableScaling)
    }

    open fun setEnableScrolling(enableScrolling: Boolean) {
        scalableViewDelegate.setEnableScrolling(enableScrolling)
    }

    @CallSuper
    open fun scaledDraw(canvas: Canvas) {
        super.draw(canvas)
    }

    open fun onScaledTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }
}

