package com.example.siasatmobile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.siasatmobile.databinding.ActivityTambahMatkulBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TambahMatkulActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahMatkulBinding
    private lateinit var dbRef: DatabaseReference

    private var mode: String? = null
    private var editId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTambahMatkulBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBatal.setOnClickListener {
            finish()
        }


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbRef = FirebaseDatabase.getInstance().getReference("matkul")

        // Ambil data dari intent
        mode = intent.getStringExtra("mode")
        editId = intent.getStringExtra("editId")

        if (mode == "edit" && editId != null) {
            // Pre-fill form
            binding.etNama.setText(intent.getStringExtra("nama") ?: "")
            binding.etSks.setText(intent.getIntExtra("sks", 0).toString())
            binding.etDosen.setText(intent.getStringExtra("dosen") ?: "")

            binding.btnSimpan.text = "Update"
        }

        binding.btnSimpan.setOnClickListener {
            val nama = binding.etNama.text.toString().trim()
            val sksStr = binding.etSks.text.toString().trim()
            val dosen = binding.etDosen.text.toString().trim()

            if (nama.isEmpty() || sksStr.isEmpty() || dosen.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sks = sksStr.toIntOrNull()
            if (sks == null || sks <= 0) {
                Toast.makeText(this, "SKS harus berupa angka > 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val matkul = mapOf(
                "nama" to nama,
                "sks" to sks,
                "dosen" to dosen
            )

            if (mode == "edit" && editId != null) {
                // 🔁 UPDATE data
                dbRef.child(editId!!).updateChildren(matkul).addOnSuccessListener {
                    Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Gagal update: ${it.message}", Toast.LENGTH_SHORT).show()
                }

            } else {
                // ➕ TAMBAH data baru
                dbRef.get().addOnSuccessListener { snapshot ->
                    val count = snapshot.childrenCount.toInt() + 1
                    val newId = "MK" + String.format("%03d", count)

                    dbRef.child(newId).setValue(matkul).addOnSuccessListener {
                        Toast.makeText(this, "Mata kuliah berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Gagal tambah: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
