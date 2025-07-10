package com.example.siasatmobile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.siasatmobile.databinding.ActivityMahasiswaBinding
import com.google.firebase.database.*

class MahasiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMahasiswaBinding
    private lateinit var ambilMatkulRef: DatabaseReference
    private lateinit var matkulRef: DatabaseReference
    private lateinit var nilaiRef: DatabaseReference

    private lateinit var nimMahasiswa: String
    private lateinit var namaMahasiswa: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMahasiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari intent
        namaMahasiswa = intent.getStringExtra("namaUser") ?: "Mahasiswa"
        nimMahasiswa = intent.getStringExtra("nimUser") ?: ""

        // Tampilkan nama
        binding.tvNamaMahasiswa.text = "Halo, $namaMahasiswa 👋"

        // Firebase references
        ambilMatkulRef = FirebaseDatabase.getInstance().getReference("ambilMatkul/$nimMahasiswa")
        matkulRef = FirebaseDatabase.getInstance().getReference("matkul")
        nilaiRef = FirebaseDatabase.getInstance().getReference("nilai")

        // Tombol logout
        binding.btnLogout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Tombol ambil mata kuliah
        binding.btnAmbilMatkul.setOnClickListener {
            val intent = Intent(this, AmbilMatkulActivity::class.java)
            intent.putExtra("nimUser", nimMahasiswa)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        tampilkanMatkulYangDiambil()
    }

    private fun tampilkanMatkulYangDiambil() {
        binding.containerMahasiswaMatkul.removeAllViews()

        ambilMatkulRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(this@MahasiswaActivity, "Tidak ada mata kuliah yang diambil", Toast.LENGTH_SHORT).show()
                    return
                }

                for (matkulIdSnapshot in snapshot.children) {
                    val idMatkul = matkulIdSnapshot.key ?: continue

                    matkulRef.child(idMatkul).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(matkulSnap: DataSnapshot) {
                            val namaMatkul = matkulSnap.child("nama").getValue(String::class.java) ?: "-"
                            val sks = matkulSnap.child("sks").getValue(Int::class.java) ?: 0
                            val dosen = matkulSnap.child("dosen").getValue(String::class.java) ?: "-"

                            // Ambil nilai
                            nilaiRef.child(idMatkul).child(nimMahasiswa).child("nilai")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(nilaiSnap: DataSnapshot) {
                                        val nilai = nilaiSnap.getValue(String::class.java) ?: "-"

                                        val itemView = layoutInflater.inflate(R.layout.item_nilai_mahasiswa, binding.containerMahasiswaMatkul, false)
                                        itemView.findViewById<TextView>(R.id.tvNamaMhs).text = namaMatkul
                                        itemView.findViewById<TextView>(R.id.tvNimMhs).text = "Dosen: $dosen | $sks SKS"
                                        itemView.findViewById<TextView>(R.id.tvNilaiMhs).text = "Nilai: $nilai"

                                        binding.containerMahasiswaMatkul.addView(itemView)
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MahasiswaActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
