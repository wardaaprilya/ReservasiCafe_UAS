package com.example.kelompok3_uas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReservasiAdapter(
    private val reservasiList: List<Reservasi>,
    private val onApproveClick: (Reservasi) -> Unit,
    private val onRejectClick: (Reservasi) -> Unit,
    private val onCompleteClick: (Reservasi) -> Unit, // Add the onCompleteClick parameter
    private val showActions: Boolean
) : RecyclerView.Adapter<ReservasiAdapter.ReservasiViewHolder>() {

    class ReservasiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaTextView: TextView = itemView.findViewById(R.id.reservasiNama)
        val tanggalTextView: TextView = itemView.findViewById(R.id.reservasiTanggal)
        val waktuTextView: TextView = itemView.findViewById(R.id.reservasiWaktu)
        val catatanTextView: TextView = itemView.findViewById(R.id.reservasiCatatan)
        val buktiPembayaranTextView: TextView = itemView.findViewById(R.id.reservasibuktiPembayaranURL)
        val reservasiStatus: TextView = itemView.findViewById(R.id.reservasiStatus)
        val approveButton: Button = itemView.findViewById(R.id.btnApprove)
        val rejectButton: Button = itemView.findViewById(R.id.btnReject)
        val completeButton: Button = itemView.findViewById(R.id.btnComplete) // Add complete button reference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservasiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservasi, parent, false)
        return ReservasiViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservasiViewHolder, position: Int) {
        val reservasi = reservasiList[position]
        holder.namaTextView.text = reservasi.nama
        holder.tanggalTextView.text = reservasi.tanggal
        holder.waktuTextView.text = reservasi.waktu
        holder.reservasiStatus.text = reservasi.status
        holder.catatanTextView.text = reservasi.catatan?.takeIf { it.isNotEmpty() } ?: "Tidak ada catatan"
        holder.buktiPembayaranTextView.text = reservasi.buktiPembayaranUrl?.takeIf { it.isNotEmpty() } ?: "Tidak ada bukti pembayaran"

        // Mengatur visibilitas tombol berdasarkan nilai showActions
        if (showActions) {
            holder.approveButton.visibility = View.VISIBLE
            holder.rejectButton.visibility = View.VISIBLE
            holder.completeButton.visibility = View.GONE // Show complete button

            holder.approveButton.setOnClickListener {
                onApproveClick(reservasi)
            }

            holder.rejectButton.setOnClickListener {
                onRejectClick(reservasi)
            }

        } else {
            holder.approveButton.visibility = View.GONE
            holder.rejectButton.visibility = View.GONE
            holder.completeButton.visibility = View.VISIBLE // Hide complete button if showActions is false

            holder.completeButton.setOnClickListener {
                onCompleteClick(reservasi) // Add onCompleteClick logic
            }

            if (reservasi.status == "selesai") {
                holder.completeButton.visibility = View.GONE
            } else {
                holder.completeButton.visibility = View.VISIBLE
                holder.completeButton.setOnClickListener {
                    onCompleteClick(reservasi)
                }
            }
        }
    }

    override fun getItemCount(): Int = reservasiList.size
}



