package com.example.siasatmobile

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.siasatmobile.databinding.ActivityRegisterBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().getReference("users")

        val roleList = listOf("mahasiswa", "dosen") // Tanpa kaprodi
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roleList)
        binding.spinnerRole.adapter = adapter

        binding.btnRegister.setOnClickListener {
            val kode = binding.etKode.text.toString().trim()
            val nama = binding.etNama.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val role = binding.spinnerRole.selectedItem.toString()

            if (kode.isEmpty() || nama.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!kode.startsWith("67")) {
                Toast.makeText(this, "Kode harus diawali 67", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cek apakah user dengan kode ini sudah ada
            dbRef.child(kode).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    Toast.makeText(this, "Kode sudah terdaftar", Toast.LENGTH_SHORT).show()
                } else {
                    val user = mapOf(
                        "name" to nama,
                        "password" to password,
                        "role" to role
                    )

                    dbRef.child(kode).setValue(user).addOnSuccessListener {
                        Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                        finish() // balik ke login
                    }.addOnFailureListener {
                        Toast.makeText(this, "Gagal simpan data: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Gagal akses database: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
