package com.example.kelompok3_uas

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SdkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_syaratdanketentuan)

        val backButton: ImageButton = findViewById(R.id.backtooprofile)
        backButton.setOnClickListener {
            onBackPressed()  // Memanggil metode untuk kembali ke aktivitas sebelumnya
        }
    }
}
