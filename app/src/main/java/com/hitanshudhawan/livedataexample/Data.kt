package com.hitanshudhawan.livedataexample

import com.hitanshudhawan.livedata.LiveData

class Data {
    companion object {
        val data: LiveData<Int?> = LiveData()

        init {
            data.setValue(0)
        }
    }
}