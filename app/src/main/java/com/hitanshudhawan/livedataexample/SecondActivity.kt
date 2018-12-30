package com.hitanshudhawan.livedataexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hitanshudhawan.livedataexample.Data.Companion.data
import kotlinx.android.synthetic.main.activity_main.*

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        button.setOnClickListener {
            data.setValue((Math.random() * 1000_000).toInt().toString())
        }

        data.observe(this) {
            if (it != data.getValue()) throw Exception()

            text_view_2.text = it
        }
    }
}
