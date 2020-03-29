package com.duyangs.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var i = 0
    private val handler = Handler()
    private val runnable = Runnable {
        update()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        update()

    }

    private fun update(){
        audio_bar.setProgress(i.toFloat() / 100f)
        i++
        handler.postDelayed(runnable,10)
    }
}
