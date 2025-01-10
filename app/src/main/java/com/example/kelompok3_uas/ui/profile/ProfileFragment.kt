package com.example.kelompok3_uas.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kelompok3_uas.AkunActivity
import com.example.kelompok3_uas.LandingActivity
import com.example.kelompok3_uas.SdkActivity
import com.example.kelompok3_uas.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.inappmessaging.FirebaseInAppMessaging

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Firebase Authentication instance
        auth = FirebaseAuth.getInstance()

        // Set up the logout button
        val logoutButton: Button = binding.buttonlogout
        logoutButton.setOnClickListener {
            logout()
        }

        val btnnsdk: Button = binding.btnsdk
        btnnsdk.setOnClickListener {
            val intent = Intent(activity, SdkActivity::class.java)
            startActivity(intent)
        }

        val btnpgtrnakun: Button = binding.btnpgtrnakun
        btnpgtrnakun.setOnClickListener {
            val intent = Intent(activity, AkunActivity::class.java)

            // Cek jenis login pengguna
            val user = auth.currentUser
            if (user != null) {
                // Periksa apakah login menggunakan Google atau email/password
                if (user.providerData[0].providerId == "google.com") {
                    // Login menggunakan Google
                    intent.putExtra("login_method", "google")
                } else {
                    // Login menggunakan email/password
                    intent.putExtra("login_method", "email")
                }
            }

            startActivity(intent)
        }

        // Ambil username dari SharedPreferences dan tampilkan di TextView
        val sharedPreferences = activity?.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val username = sharedPreferences?.getString("username", "Username tidak ditemukan")
        binding.textviewusername.text = username

        return root
    }

    private fun logout() {
        // Sign out user dari Firebase
        auth.signOut()

        // Redirect ke LandingActivity setelah logout
        val intent = Intent(activity, LandingActivity::class.java)
        startActivity(intent)
        activity?.finish() // Menutup ProfileFragment dan kembali ke layar landing
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

