package com.example.siasatmobile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.siasatmobile.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi referensi database
        dbRef = FirebaseDatabase.getInstance().getReference("users")

        // Klik tombol login
        binding.btnLogin.setOnClickListener {
            val kode = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (kode.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!kode.startsWith("67")) {
                Toast.makeText(this, "Kode harus diawali dengan 67", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cek user dari Firebase
            dbRef.child(kode).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val dbPassword = snapshot.child("password").value.toString()
                    val role = snapshot.child("role").value.toString()
                    val namaUser = snapshot.child("name").value.toString()


                    if (password == dbPassword) {
                        // Arahkan user sesuai role
                        when (role) {
                            "kaprodi" -> {
                                val intent = Intent(this, KaprodiActivity::class.java)
                                intent.putExtra("namaUser", namaUser) // ⬅️ kirim nama
                                startActivity(intent)
                                finish()
                            }
                            "dosen" -> {
                                val intent = Intent (this, DosenActivity::class.java)
                                intent.putExtra("namaUser", namaUser)
                                startActivity(intent)
                                finish()
                            }
                            "mahasiswa" -> {
                                val intent = Intent(this, MahasiswaActivity::class.java)
                                intent.putExtra("namaUser", namaUser)
                                intent.putExtra("nimUser", kode)  // kode = NIM
                                startActivity(intent)
                                finish()
                            }

                            else -> {
                                Toast.makeText(this, "Role tidak dikenali", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Password salah", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Gagal login: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Klik teks register
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
