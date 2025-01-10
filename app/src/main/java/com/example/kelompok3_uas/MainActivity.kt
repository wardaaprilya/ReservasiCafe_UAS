package com.example.kelompok3_uas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.kelompok3_uas.databinding.ActivityMainBinding
import com.google.firebase.inappmessaging.FirebaseInAppMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val toolbar = binding.toolbarr
        setSupportActionBar(toolbar)

        supportActionBar?.setLogo(R.drawable.logolatarwarna) // Ganti dengan ikon Anda
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.navView.setupWithNavController(navController)

        // Firebase In-App Messaging setup
        val firebaseInAppMessaging = FirebaseInAppMessaging.getInstance()

        // (Opsional) Menambahkan trigger event jika diperlukan
        // Misalnya, setelah login atau aksi lainnya, Anda dapat memicu event tertentu
        firebaseInAppMessaging.triggerEvent("main_activity_opened")

        // Firebase In-App Messaging akan menampilkan pesan yang telah dikonfigurasi di Firebase Console
    }
}
