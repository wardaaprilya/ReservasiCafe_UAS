package com.example.kelompok3_uas.ui.reservasi


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReservasiViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Reservasi Fragment"
    }
    val text: LiveData<String> = _text
}



