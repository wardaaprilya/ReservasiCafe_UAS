package com.example.kelompok3_uas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.kelompok3_uas.databinding.ActivityMainadminBinding
import com.google.firebase.auth.FirebaseAuth

class AdminMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainadminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout using View Binding
        binding = ActivityMainadminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Bottom Navigation View
        val navView: BottomNavigationView = binding.mobileNavigationAdmin

        // Set up Toolbar
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Configure Action Bar
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)  // Hide default title
            setDisplayHomeAsUpEnabled(false)   // Hide back button
        }

        // Add logout button to toolbar
        val button = androidx.appcompat.widget.AppCompatImageButton(this).apply {
            setImageResource(R.drawable.logout)  // Replace with appropriate icon
            setContentDescription("Logout Button")
            background = null  // Make background transparent
            setPadding(0, 0, 16, 0)  // Right padding 16dp
            setOnClickListener {
                // Handle logout action
                logout()
            }
        }

        val layoutParams = androidx.appcompat.widget.Toolbar.LayoutParams(
            androidx.appcompat.widget.Toolbar.LayoutParams.WRAP_CONTENT,
            androidx.appcompat.widget.Toolbar.LayoutParams.MATCH_PARENT
        ).apply {
            gravity = Gravity.END  // Place button on the right
        }

        button.layoutParams = layoutParams
        toolbar.addView(button)  // Add button to toolbar

        // Initialize Navigation Controller
        val navController = findNavController(R.id.nav_host_fragment_activity_main_admin)
        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()

        // Check if user is logged in
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.d("AdminMainActivity", "User not logged in, redirecting to LandingActivity.")
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
            finish()
        } else if (currentUser.email != "admin@example.com") {
            Log.d("AdminMainActivity", "Non-admin user detected, redirecting to LandingActivity.")
            Toast.makeText(this, "Anda bukan admin", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun logout() {
        // Sign out from Firebase
        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        // Redirect to LandingActivity after logout
        val intent = Intent(this, LandingActivity::class.java)
        startActivity(intent)
        finish()  // Close AdminMainActivity to prevent returning
    }
}
