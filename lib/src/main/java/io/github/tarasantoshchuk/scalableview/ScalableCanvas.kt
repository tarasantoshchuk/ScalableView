package io.github.tarasantoshchuk.scalableview

import android.graphics.Canvas
import android.graphics.Matrix


internal class ScalableCanvas {
    private val currentMatrix = Matrix()

    private var innerCanvas: Canvas? = null

    fun apply(canvas: Canvas, block: (Canvas) -> Unit) {
        attachCanvas(canvas)
        block(canvas)
        detachCanvas()
    }

    fun setMatrix(block: (Matrix) -> Unit) {
        currentMatrix.reset()
        block(currentMatrix)
        setMatrix(currentMatrix)
    }

    private fun attachCanvas(canvas: Canvas) {
        innerCanvas = canvas
        innerCanvas!!.save()
        innerCanvas!!.concat(currentMatrix)
    }

    private fun detachCanvas() {
        innerCanvas!!.restore()
        innerCanvas = null
    }

    private fun isAttached(): Boolean {
        return innerCanvas != null
    }

    private fun setMatrix(matrix: Matrix?) {
        currentMatrix.set(matrix)

        if (isAttached()) {
            throw IllegalStateException("Touching matrix while canvas attached")
        }
    }
}