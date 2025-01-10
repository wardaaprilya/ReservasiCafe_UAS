package com.example.kelompok3_uas.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kelompok3_uas.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance().reference
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val scheduleTable = root.findViewById<android.widget.TableLayout>(R.id.scheduleTable)

        // Tetap tambahkan header tabel
        addTableHeader(scheduleTable)

        // Ambil data dari Firebase dan perbarui tabel
        fetchAndUpdateTable(scheduleTable)

        return root
    }

    private fun addTableHeader(scheduleTable: android.widget.TableLayout) {
        val headerRow = TableRow(context)
        val headers = listOf("Tanggal Reservasi", "Siang (11:00-13:00)", "Sore (15:00-17:00)", "Malam (19:00-21:00)")
        headers.forEach { header ->
            val textView = TextView(context).apply {
                text = header
                textSize = 11f
                setPadding(8, 8, 8, 8)
                gravity = android.view.Gravity.CENTER
                setBackgroundResource(R.color.gray)
            }
            headerRow.addView(textView)
        }
        scheduleTable.addView(headerRow)
    }

    private fun fetchAndUpdateTable(scheduleTable: android.widget.TableLayout) {
        database.child("reservasi").orderByChild("status").equalTo("approved")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reservations = snapshot.children.mapNotNull {
                        val tanggal = it.child("tanggal").value as? String
                        val waktu = it.child("waktu").value as? String
                        if (tanggal != null && waktu != null) {
                            Pair(tanggal, waktu)
                        } else {
                            null
                        }
                    }.toSet()

                    Log.d("HomeFragment", "Fetched reservations: $reservations")

                    updateTableWithReservations(scheduleTable, reservations)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HomeFragment", "Failed to load data: ${error.message}")
                }
            })
    }

    private fun updateTableWithReservations(
        scheduleTable: android.widget.TableLayout,
        reservations: Set<Pair<String, String>>
    ) {
        // Bersihkan baris tabel kecuali header
        while (scheduleTable.childCount > 1) {
            scheduleTable.removeViewAt(1)
        }

        // Buat tabel untuk 14 hari ke depan
        val calendar = Calendar.getInstance()
        repeat(14) {
            val date = dateFormat.format(calendar.time)
            val row = TableRow(context)

            // Kolom Tanggal
            val dateCell = TextView(context).apply {
                text = date
                textSize = 10f
                gravity = android.view.Gravity.CENTER
                setPadding(8, 8, 8, 8)
            }
            row.addView(dateCell)

            // Kolom untuk setiap sesi waktu
            val timeSlots = listOf("Siang (11:00-13:00)", "Sore (15:00-17:00)", "Malam (19:00-21:00)")
            timeSlots.forEach { timeSlot ->
                val cellText = if (reservations.contains(Pair(date, timeSlot))) "Tidak Tersedia" else "Tersedia"
                val cell = TextView(context).apply {
                    text = cellText
                    textSize = 10f
                    gravity = android.view.Gravity.CENTER
                    setPadding(8, 8, 8, 8)
                    setBackgroundResource(
                        if (cellText == "Tidak Tersedia") R.color.red else R.color.green
                    )
                }
                row.addView(cell)
            }

            // Tambahkan baris ke tabel
            scheduleTable.addView(row)

            // Pindah ke hari berikutnya
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }
}
