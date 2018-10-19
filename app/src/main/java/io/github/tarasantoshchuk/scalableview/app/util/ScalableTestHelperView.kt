package io.github.tarasantoshchuk.scalableview.app.util

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.SeekBar
import io.github.tarasantoshchuk.scalableview.app.R
import kotlinx.android.synthetic.main.view_scalable_test_helper.view.*

class ScalableTestHelperView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var listener: ScaleTestListener? = null

    init {
        inflate(context, R.layout.view_scalable_test_helper, this)

        enable_scale.setOnCheckedChangeListener { _, enabled ->
            listener?.onEnableScaling(enabled)
        }

        enable_scroll.setOnCheckedChangeListener { _, enabled ->
            listener?.onEnableScrolling(enabled)
        }

        btn_trigger_reset.setOnClickListener {
            listener?.onReset()
        }

        seek_max_scale.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newMaxScale = progress / 1000f + 1f
                txt_scale_factor.text = "Max scale value = $newMaxScale"
                listener?.onMaxScaleChange(newMaxScale)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    fun setScaleTestListener(l: ScaleTestListener) {
        listener = l
    }

    interface ScaleTestListener {
        fun onReset()
        fun onEnableScrolling(enable: Boolean)
        fun onEnableScaling(enable: Boolean)
        fun onMaxScaleChange(newMaxScale: Float)
    }
}
