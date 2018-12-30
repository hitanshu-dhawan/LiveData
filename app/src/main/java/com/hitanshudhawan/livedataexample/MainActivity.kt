package com.hitanshudhawan.livedataexample

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.hitanshudhawan.livedataexample.Data.Companion.data
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val observer: (String?) -> Unit = {
        if (it != data.getValue()) throw Exception()

        text_view.text = it
        Toast.makeText(this, "value changed", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            data.setValue((Math.random() * 1000_000).toInt().toString())
        }

        data.observe(this) {
            if (it != data.getValue()) throw Exception()

            text_view_2.text = it
        }

        button_2.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }

        Handler().postDelayed({
            data.observe(this, observer)
            Toast.makeText(this, "observer added", Toast.LENGTH_SHORT).show()
        }, 5000)

        Handler().postDelayed({
            data.removeObserver(observer)
            Toast.makeText(this, "observer removed", Toast.LENGTH_SHORT).show()
        }, 20_000)
    }
}
