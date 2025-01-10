package com.example.kelompok3_uas

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AkunActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPasswordNew: EditText
    private lateinit var textViewEmailGoogle: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var btnEditProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengaturanakun)


        auth = FirebaseAuth.getInstance()

        // Inisialisasi elemen-elemen tampilan
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPasswordNew = findViewById(R.id.editTextPasswordNew)
        textViewEmailGoogle = findViewById(R.id.textViewEmailGoogle)
        textViewEmail = findViewById(R.id.textViewEmail)
        btnEditProfile = findViewById(R.id.btneditprofile)

        // Ambil data login_method dari Intent
        val loginMethod = intent.getStringExtra("login_method")

        val user = auth.currentUser
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "DefaultUser")

        if (user != null) {
            if (loginMethod == "google") {
                showGoogleLoginView(user, username)
            } else {
                showEmailPasswordLoginView(user, username)
            }
        }


        val backbackback: LinearLayout? = findViewById(R.id.backttothejungle)
        backbackback?.setOnClickListener {
            onBackPressed()
        }

        // Tombol untuk mengubah profile
        btnEditProfile.setOnClickListener {
            val newUsername = editTextUsername.text.toString().trim()
            val newEmail = editTextEmail.text.toString().trim()
            val newPassword = editTextPasswordNew.text.toString().trim()

            // Cek apakah ada perubahan username
            if (newUsername.isNotEmpty()) {
                // Update username di SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putString("username", newUsername)
                editor.apply()
            }

            val user = auth.currentUser
            if (user != null) {
                if (newEmail.isNotEmpty() && newEmail != user.email) {
                    // Update email
                    user.updateEmail(newEmail).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Email berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Gagal memperbarui email", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                if (newPassword.isNotEmpty()) {
                    // Update password
                    user.updatePassword(newPassword).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Gagal memperbarui password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showGoogleLoginView(user: FirebaseUser, username: String?) {
        // Menampilkan tampilan jika login menggunakan Google
        textViewEmailGoogle.visibility = View.VISIBLE
        textViewEmailGoogle.text = user.email
        editTextUsername.setText(username) // Menampilkan username yang disimpan
        editTextPasswordNew.visibility = View.GONE // Menyembunyikan password
        textViewEmail.visibility = View.GONE
        editTextEmail.visibility = View.GONE
    }

    private fun showEmailPasswordLoginView(user: FirebaseUser, username: String?) {
        // Menampilkan tampilan jika login menggunakan email/password
        textViewEmail.visibility = View.VISIBLE
        textViewEmail.text = user.email
        editTextEmail.setText(user.email) // Menampilkan email
        editTextUsername.setText(username) // Menampilkan username
        editTextPasswordNew.visibility = View.VISIBLE // Menampilkan input password baru
        textViewEmailGoogle.visibility = View.GONE
    }
}
