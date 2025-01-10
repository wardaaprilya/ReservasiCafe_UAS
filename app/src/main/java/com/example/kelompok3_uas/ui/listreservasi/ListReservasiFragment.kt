package com.example.kelompok3_uas.ui.listreservasi

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
import com.example.kelompok3_uas.databinding.FragmentListReservasiBinding
import com.google.firebase.database.*

class ListReservasiFragment : Fragment() {

    private var _binding: FragmentListReservasiBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private val reservasiList = mutableListOf<Reservasi>()
    private lateinit var adapter: ReservasiAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListReservasiBinding.inflate(inflater, container, false)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Initialize RecyclerView
        binding.listReservasi.layoutManager = LinearLayoutManager(requireContext())
        adapter = ReservasiAdapter(
            reservasiList,
            onApproveClick = {},
            onRejectClick = {},
            onCompleteClick = { reservasi -> completeReservation(reservasi) },
            showActions = false // Show the "Selesai" button
        )
        binding.listReservasi.adapter = adapter

        // Load accepted reservations
        fetchAcceptedReservations()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchAcceptedReservations() {
        val approvedReservationsQuery = database.child("reservasi").orderByChild("status").equalTo("approved")
        val completedReservationsQuery = database.child("reservasi").orderByChild("status").equalTo("selesai")

        val combinedQuery = approvedReservationsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reservasiList.clear()

                for (data in snapshot.children) {
                    val reservasi = data.getValue(Reservasi::class.java)
                    if (reservasi != null) {
                        reservasiList.add(reservasi)
                    }
                }

                // Continue with the next query for completed reservations
                completedReservationsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            val reservasi = data.getValue(Reservasi::class.java)
                            if (reservasi != null) {
                                reservasiList.add(reservasi)
                            }
                        }

                        // Sort the list by timestamp
                        reservasiList.sortByDescending { it.timestamp }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ListReservasiFragment", "Failed to load completed data: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ListReservasiFragment", "Failed to load approved data: ${error.message}")
            }
        })
    }


    private fun completeReservation(reservasi: Reservasi) {
        val reservasiId = reservasi.idReservasi.takeIf { it.isNotEmpty() }
        if (reservasiId.isNullOrEmpty()) {
            Toast.makeText(context, "ID Reservasi tidak valid.", Toast.LENGTH_SHORT).show()
            return
        }

        // Update reservation status to 'selesai'
        database.child("reservasi").child(reservasiId).child("status").setValue("selesai")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Reservasi berhasil diselesaikan.", Toast.LENGTH_SHORT).show()
                    fetchAcceptedReservations() // Refresh data after the status change
                } else {
                    Toast.makeText(context, "Failed to update reservation status", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
