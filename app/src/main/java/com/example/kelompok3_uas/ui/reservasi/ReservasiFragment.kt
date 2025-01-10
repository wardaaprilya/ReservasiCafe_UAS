package com.example.kelompok3_uas.ui.reservasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kelompok3_uas.R
import com.example.kelompok3_uas.Reservasi
import com.example.kelompok3_uas.TambahReservasiActivity
import com.example.kelompok3_uas.databinding.FragmentReservasiBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ReservasiFragment : Fragment() {

    private var _binding: FragmentReservasiBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var reservationNotesContainerPengguna: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        _binding = FragmentReservasiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inisialisasi Firebase Database dan Auth
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // Temukan View
        reservationNotesContainerPengguna = root.findViewById(R.id.reservationNotesContainerpengguna)

        // Temukan tombol dan set listener
        val btnTambahReservasi: Button = binding.buttontambahreservasi
        btnTambahReservasi.setOnClickListener {
            // Membuat Intent untuk berpindah ke TambahReservasiActivity
            val intent = Intent(activity, TambahReservasiActivity::class.java)
            startActivity(intent)
        }

        // Periksa status reservasi untuk pengguna saat ini
        checkReservationStatus()

        // Return the root view of the fragment
        return root
    }

    private fun checkReservationStatus() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            reservationNotesContainerPengguna.text =
                "Kamu belum login. Silakan login terlebih dahulu untuk melihat reservasi."
            return
        }

        val userId = currentUser.uid

        database.child("reservasi")
            .orderByChild("idUser")
            .equalTo(userId) // Filter berdasarkan UID pengguna
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Buat string untuk menampilkan semua reservasi
                        val reservedText = StringBuilder()

                        // Iterasi melalui semua data reservasi pengguna
                        for (data in snapshot.children) {
                            val reservasi = data.getValue(Reservasi::class.java)
                            reservasi?.let {
                                if (it.status == "approved") {
                                    reservedText.append("Kamu telah melakukan reservasi atas nama ${it.nama} pada tanggal ${it.tanggal} di waktu ${it.waktu}. Catatan: ${it.catatan}\n\n")
                                }
                            }
                        }

                        // Periksa apakah ada reservasi yang disetujui
                        if (reservedText.isNotEmpty()) {
                            reservationNotesContainerPengguna.text = reservedText.toString()
                        } else {
                            reservationNotesContainerPengguna.text = "Opsss, kamu belum melakukan reservasi apapun yang disetujui."
                        }
                    } else {
                        reservationNotesContainerPengguna.text = "Opsss, kamu belum melakukan reservasi apapun."
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    reservationNotesContainerPengguna.text = "Gagal memuat data reservasi: ${error.message}"
                }
            })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Membersihkan binding saat view dihancurkan
    }
}
