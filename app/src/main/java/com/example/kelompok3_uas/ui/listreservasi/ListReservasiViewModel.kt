package com.example.kelompok3_uas.ui.listreservasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kelompok3_uas.Reservasi

class ListReservasiViewModel : ViewModel() {

    private val _reservasiList = MutableLiveData<List<Reservasi>>()
    val reservasiList: LiveData<List<Reservasi>> get() = _reservasiList

    fun updateReservasiList(newList: List<Reservasi>) {
        _reservasiList.value = newList
    }
}
