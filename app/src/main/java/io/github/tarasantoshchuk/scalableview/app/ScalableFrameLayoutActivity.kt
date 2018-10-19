package io.github.tarasantoshchuk.scalableview.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.github.tarasantoshchuk.scalableview.app.util.ScalableTestHelperView
import kotlinx.android.synthetic.main.activity_scalable_frame_layout.*

class ScalableFrameLayoutActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scalable_frame_layout)

        btn_show_toast.setOnClickListener {
            Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show()
        }

        test_helper.setScaleTestListener(object : ScalableTestHelperView.ScaleTestListener {
            override fun onReset() {
                example_scalable_view.reset()
            }

            override fun onEnableScrolling(enable: Boolean) {
                example_scalable_view.setEnableScrolling(enable)
            }

            override fun onEnableScaling(enable: Boolean) {
                example_scalable_view.setEnableScaling(enable)
            }

            override fun onMaxScaleChange(newMaxScale: Float) {
                example_scalable_view.setMaxScale(newMaxScale)
            }
        })
    }
}
