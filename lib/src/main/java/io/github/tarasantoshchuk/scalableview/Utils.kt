package io.github.tarasantoshchuk.scalableview

import android.graphics.Matrix
import android.graphics.PointF
import android.view.MotionEvent

internal fun Matrix.setScale(pivot: PointF, factor: Float) {
    setScale(factor, factor, pivot.x, pivot.y)
}

internal fun PointF.scale(pivot: PointF, factor: Float) {
    x = x.scale(pivot.x, factor)
    y = y.scale(pivot.y, factor)
}

internal fun Float.scale(pivot: Float, factor: Float): Float {
    return (this - pivot) * factor + pivot
}

internal fun MotionEvent.setLocation(newLocation: PointF) {
    setLocation(newLocation.x, newLocation.y)
}

internal fun Float.clamp(left: Float, right: Float): Float {
    return Math.max(left, Math.min(this, right))
}

internal fun Boolean.doIfTrue(block: () -> Unit): Boolean {
    if (this) block()
    return this
}