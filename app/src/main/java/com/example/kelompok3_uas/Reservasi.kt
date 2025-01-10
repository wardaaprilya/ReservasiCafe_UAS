package com.example.kelompok3_uas

data class Reservasi(
    val idReservasi: String = "",  // ID reservasi unik
    val nama: String = "",         // Nama pengguna
    val phone: String = "",        // Nomor telepon pengguna
    val tanggal: String = "",      // Tanggal reservasi
    val waktu: String = "",        // Waktu reservasi
    val status: String = "pending", // Status reservasi: pending, approved, rejected
    val buktiPembayaranUrl: String? = null,  // URL bukti pembayaran
    val catatan: String? = null,    // Catatan opsional
    val idUser: String = "",
    val timestamp: Long = 0L,
)