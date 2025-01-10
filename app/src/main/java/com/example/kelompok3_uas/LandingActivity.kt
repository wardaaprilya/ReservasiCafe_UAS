package com.example.kelompok3_uas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kelompok3_uas.databinding.ActivityLandingBinding

class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding

    // Mendeklarasikan SharedPreferences untuk menyimpan status login
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)

        // Mengecek apakah user sudah login
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val isAdmin = sharedPreferences.getBoolean("is_admin", false)

        // Jika sudah login, langsung ke MainActivity
        if (isLoggedIn) {
            if (isAdmin) {
                val intent = Intent(this, AdminMainActivity::class.java)
                startActivity(intent)
                finish() // Menutup LandingActivity agar tidak bisa kembali
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Menutup LandingActivity agar tidak bisa kembali
            }
        }

        // Navigasi ke halaman Login
        binding.buttonMasuk.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Navigasi ke halaman Daftar
        binding.buttonDaftar.setOnClickListener {
            val intent = Intent(this, DaftarAkunActivity::class.java)
            startActivity(intent)
        }

        // Navigasi untuk login sebagai Admin
        binding.textviewmasuksebagaiadmin.setOnClickListener {
            val intent = Intent(this, AdminLoginActivity::class.java)
            startActivity(intent)
        }
    }
}


