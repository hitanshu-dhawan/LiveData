package com.hitanshudhawan.livedataexample

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hitanshudhawan.livedataexample.Data.Companion.data
import kotlinx.android.synthetic.main.fragment_data.*

class DataFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addObserver()
        data_text_view.setOnClickListener(this)
        add_observer_button.setOnClickListener(this)
        remove_observer_button.setOnClickListener(this)
    }

    private val observer: (Int?) -> Unit = {
        if (it != data.getValue()) throw Exception()
        data_text_view.text = it.toString()
    }

    private fun addObserver() {
        try {
            data.observe(this, observer)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(context, "observe already added", Toast.LENGTH_SHORT).show()
        }
        observer_state_text_view.setTextColor(Color.GREEN)
    }

    private fun removeObserver() {
        data.removeObserver(observer)
        observer_state_text_view.setTextColor(Color.RED)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.data_text_view -> {
                data.setValue(data.getValue()?.plus(1))
            }
            R.id.add_observer_button -> {
                addObserver()
            }
            R.id.remove_observer_button -> {
                removeObserver()
            }
        }
    }

}
