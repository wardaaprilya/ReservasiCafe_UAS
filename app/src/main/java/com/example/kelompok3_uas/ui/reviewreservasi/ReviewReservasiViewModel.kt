package com.example.kelompok3_uas.ui.reviewreservasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReviewReservasiViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is List Reservasi Fragment"
    }
    val text: LiveData<String> = _text

    }