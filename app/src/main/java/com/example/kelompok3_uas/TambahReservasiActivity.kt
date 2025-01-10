package com.example.kelompok3_uas

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class TambahReservasiActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etDate: EditText
    private lateinit var spTime: Spinner
    private lateinit var etNote: EditText
    private lateinit var btnUploadFoto: ImageButton
    private lateinit var btntambahkanreservasi: Button
    private lateinit var btnbatalkanreservasi: Button
    private var imageUri: Uri? = null
    private val MAX_FILE_SIZE = 2 * 1024 * 1024 // 2MB
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_reservasi)

        // Initialize views
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etDate = findViewById(R.id.etDate)
        spTime = findViewById(R.id.spTime)
        etNote = findViewById(R.id.etNote)
        btnUploadFoto = findViewById(R.id.btnuploadfoto)
        btntambahkanreservasi = findViewById(R.id.btntambahkanreservasi)
        btnbatalkanreservasi = findViewById(R.id.btnbatalkanreservasi)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance().reference

        // Set input type for phone
        etPhone.inputType = android.text.InputType.TYPE_CLASS_PHONE

        // Set up Spinner
        val timeOptions = arrayOf("Siang (11:00-13:00)", "Sore (15:00-17:00)", "Malam (19:00-21:00)")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTime.adapter = adapter

        // Set up DatePicker
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        etDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    etDate.setText(dateFormat.format(selectedDate.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            // Limit the date to 14 days from now
            val maxDate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 14)
            }
            datePicker.datePicker.minDate = System.currentTimeMillis()
            datePicker.datePicker.maxDate = maxDate.timeInMillis
            datePicker.show()
        }

        // Handle photo upload
        btnUploadFoto.setOnClickListener {
            openGalleryForImage()
        }

        val backbuttonmasuk: ImageButton = findViewById(R.id.backtoreservasi)
        backbuttonmasuk.setOnClickListener {
            onBackPressed()  // Memanggil metode untuk kembali ke aktivitas sebelumnya
        }

        // Handle add reservation
        btntambahkanreservasi.setOnClickListener {
            val name = etName.text.toString()
            val phone = etPhone.text.toString()
            val date = etDate.text.toString()
            val time = spTime.selectedItem.toString()
            val note = etNote.text.toString()  // Catatan opsional

            // Validasi hanya untuk nama, nomor HP, tanggal, dan waktu
            if (name.isEmpty() || phone.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Isi semua field yang wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                val idReservasi = database.push().key.toString()

                // Jika bukti pembayaran dipilih, upload gambar
                if (imageUri != null) {
                    uploadImage(imageUri!!) { buktiPembayaranUrl ->
                        saveReservasi(idReservasi, name, phone, date, time, note, buktiPembayaranUrl ?: "")
                    }
                } else {
                    // Simpan tanpa bukti pembayaran
                    saveReservasi(idReservasi, name, phone, date, time, note, "")
                }
            }
        }

        // Handle cancel reservation
        btnbatalkanreservasi.setOnClickListener {
            etName.text.clear()
            etPhone.text.clear()
            etDate.text.clear()
            etNote.text.clear()
            spTime.setSelection(0)
            Toast.makeText(this, "Reservasi dibatalkan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            if (imageUri != null) {
                val fileSize = getFileSize(imageUri!!)
                if (fileSize > MAX_FILE_SIZE) {
                    Toast.makeText(this, "Ukuran file melebihi 2MB", Toast.LENGTH_SHORT).show()
                    imageUri = null
                } else {
                    btnUploadFoto.setImageURI(imageUri)
                }
            }
        }
    }

    private fun getFileSize(uri: Uri): Long {
        var fileSize: Long = 0
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            fileSize = inputStream?.available()?.toLong() ?: 0
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fileSize
    }

    private fun uploadImage(uri: Uri, callback: (String?) -> Unit) {
        val fileRef = storage.child("uploads/${System.currentTimeMillis()}.jpg")
        val uploadTask = fileRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                callback(downloadUri.toString())
            }.addOnFailureListener {
                callback(null)
            }
        }

        uploadTask.addOnFailureListener {
            callback(null)
        }
    }

    private fun saveReservasi(
        idReservasi: String, name: String, phone: String, date: String, time: String,
        note: String, buktiPembayaranUrl: String
    ) {
        val reservasi = Reservasi(
            idReservasi = idReservasi,
            idUser = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            nama = name,
            phone = phone,
            tanggal = date,
            waktu = time,
            catatan = note,
            buktiPembayaranUrl = buktiPembayaranUrl,
            status = "pending"
        )

        // Simpan data ke Firebase
        database.child("reservasi").child(idReservasi).setValue(reservasi)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Data berhasil dikirim", Toast.LENGTH_SHORT).show()
                    finish()  // Menutup activity setelah data berhasil disimpan
                } else {
                    Toast.makeText(this, "Gagal mengirimkan reservasi", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
