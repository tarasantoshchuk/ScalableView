package io.github.tarasantoshchuk.scalableview.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.IllegalArgumentException

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        startActivity(
                Intent(this, when(v) {
                    btn_base_scalable_view -> ExampleScalableViewActivity::class.java
                    btn_scalable_text_view -> ScalableTextViewActivity::class.java
                    btn_scalable_layout -> ScalableFrameLayoutActivity::class.java
                    else -> {
                        throw IllegalArgumentException()
                    }
                })
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_base_scalable_view.setOnClickListener(this)
        btn_scalable_text_view.setOnClickListener(this)
        btn_scalable_layout.setOnClickListener(this)
    }


}
