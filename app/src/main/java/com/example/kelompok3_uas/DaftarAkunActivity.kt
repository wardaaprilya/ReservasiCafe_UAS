package com.example.kelompok3_uas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kelompok3_uas.databinding.ActivityDaftarakunBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.util.*

class DaftarAkunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDaftarakunBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val TAG = "DaftarAkunActivity"
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaftarakunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Menangani login jika sudah memiliki akun
        val MasukTextView: TextView = findViewById(R.id.sudahpunyaakunMasuk)
        MasukTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Setup Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Tombol Daftar
        binding.buttondaftar.setOnClickListener {
            val email = binding.emailinput.text.toString().trim()
            val password = binding.passInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pendaftaran dengan email dan password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Pendaftaran berhasil!")
                        val username = binding.usernameinput.text.toString().trim()

                        // Simpan username ke SharedPreferences
                        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("username", username)
                        editor.apply()

                        // Berpindah ke MainActivity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.w(TAG, "Pendaftaran gagal", task.exception)
                        Toast.makeText(this, "Pendaftaran gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Tombol Google SignIn
        binding.googleSignUpBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    }

    override fun onBackPressed() {
        Log.d(TAG, "Back button pressed in DaftarAkunActivity")
        super.onBackPressed() // Kembali ke LandingActivity
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.result
                if (account != null) {
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(this, "Berhasil masuk dengan Google!", Toast.LENGTH_SHORT).show()

                    // Buat username acak jika login menggunakan Google
                    val randomUsername = "User" + UUID.randomUUID().toString().take(6)  // Username acak
                    val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("username", randomUsername)
                    editor.apply()

                    // Berpindah ke MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Gagal masuk dengan Google: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}

