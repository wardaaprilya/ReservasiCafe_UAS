package com.example.kelompok3_uas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var emailInputAdmin: EditText
    private lateinit var passInputAdmin: EditText
    private lateinit var btnLoginAdmin: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginadmin)

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize Views
        emailInputAdmin = findViewById(R.id.emailinputadmin)
        passInputAdmin = findViewById(R.id.passInputadmin)
        btnLoginAdmin = findViewById(R.id.buttonloginadmin)

        // Set up login button click listener
        btnLoginAdmin.setOnClickListener {
            val email = emailInputAdmin.text.toString()
            val password = passInputAdmin.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                // Attempt to log in
                loginAdmin(email, password)
            }
        }
    }

    private fun loginAdmin(email: String, password: String) {
        // Sign in using Firebase Authentication
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    // Check if the logged-in user is admin
                    if (user?.email == "admin@example.com") {
                        // Redirect to AdminMainActivity
                        val intent = Intent(this, AdminMainActivity::class.java)
                        startActivity(intent)
                        finish()  // Close the login activity
                    } else {
                        Toast.makeText(this, "Anda bukan admin", Toast.LENGTH_SHORT).show()
                        firebaseAuth.signOut() // Sign out non-admin users
                    }
                } else {
                    Toast.makeText(this, "Login gagal. Periksa email dan password", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
