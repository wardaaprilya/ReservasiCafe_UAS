package com.example.kelompok3_uas.ui.reviewreservasi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelompok3_uas.Reservasi
import com.example.kelompok3_uas.ReservasiAdapter
import com.example.kelompok3_uas.databinding.FragmentReviewReservasiBinding
import com.google.firebase.database.*

class ReviewReservasiFragment : Fragment() {

    private var _binding: FragmentReviewReservasiBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var adapter: ReservasiAdapter
    private val reservasiList = mutableListOf<Reservasi>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewReservasiBinding.inflate(inflater, container, false)

        // Inisialisasi Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Setup RecyclerView
        binding.reservasiList.layoutManager = LinearLayoutManager(requireContext())

        // Pass the onCompleteClick function
        adapter = ReservasiAdapter(reservasiList, this::onApproveReservation, this::onRejectReservation, this::onCompleteReservation, showActions = true)
        binding.reservasiList.adapter = adapter

        // Memuat data reservasi yang perlu disetujui
        fetchReservations()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchReservations() {
        database.child("reservasi").orderByChild("status").equalTo("pending")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    reservasiList.clear()
                    if (snapshot.exists()) {
                        for (data in snapshot.children) {
                            val reservasi = data.getValue(Reservasi::class.java)
                            reservasi?.let {
                                reservasiList.add(it)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(context, "Tidak ada reservasi pending.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ReviewReservasiFragment", "Gagal memuat data: ${error.message}")
                    Toast.makeText(context, "Gagal memuat data.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun onApproveReservation(reservasi: Reservasi) {
        updateReservationStatus(reservasi, "approved")
    }

    private fun onRejectReservation(reservasi: Reservasi) {
        updateReservationStatus(reservasi, "rejected")
    }

    private fun onCompleteReservation(reservasi: Reservasi) {
        updateReservationStatus(reservasi, "completed") // Change status to completed
    }

    private fun updateReservationStatus(reservasi: Reservasi, status: String) {
        val reservasiId = reservasi.idReservasi.takeIf { it.isNotEmpty() }
        if (reservasiId.isNullOrEmpty()) {
            Toast.makeText(context, "ID Reservasi tidak valid.", Toast.LENGTH_SHORT).show()
            return
        }

        database.child("reservasi").child(reservasiId).child("status").setValue(status)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val message = when (status) {
                        "approved" -> "Reservasi berhasil disetujui."
                        "rejected" -> "Reservasi berhasil ditolak."
                        else -> "Reservasi selesai."
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    fetchReservations() // Refresh data setelah perubahan
                } else {
                    Toast.makeText(context, "Gagal memperbarui status reservasi", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

