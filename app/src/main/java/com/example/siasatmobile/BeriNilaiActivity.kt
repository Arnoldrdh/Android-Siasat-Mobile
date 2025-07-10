package com.example.siasatmobile

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.siasatmobile.databinding.ActivityBeriNilaiBinding
import com.google.firebase.database.*

class BeriNilaiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBeriNilaiBinding
    private lateinit var ambilMatkulRef: DatabaseReference
    private lateinit var usersRef: DatabaseReference
    private lateinit var nilaiRef: DatabaseReference

    private lateinit var idMatkul: String
    private lateinit var namaMatkul: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeriNilaiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data intent
        idMatkul = intent.getStringExtra("idMatkul") ?: ""
        namaMatkul = intent.getStringExtra("namaMatkul") ?: ""

        // Set judul halaman
        binding.tvJudulMatkul.text = "Beri Nilai - $namaMatkul"

        // Init Firebase reference
        ambilMatkulRef = FirebaseDatabase.getInstance().getReference("ambilMatkul")
        usersRef = FirebaseDatabase.getInstance().getReference("users")
        nilaiRef = FirebaseDatabase.getInstance().getReference("nilai").child(idMatkul)

        // Tampilkan mahasiswa
        tampilkanDaftarMahasiswa()

        // Tombol Simpan Nilai
        binding.btnSimpanNilai.setOnClickListener {
            simpanNilaiMahasiswa()
        }

        //tombol kemabali
        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun tampilkanDaftarMahasiswa() {
        binding.containerMahasiswa.removeAllViews()

        ambilMatkulRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (mahasiswaSnapshot in snapshot.children) {
                    val nim = mahasiswaSnapshot.key ?: continue
                    val ambilMatkulMap = mahasiswaSnapshot.value as? Map<*, *> ?: continue

                    if (ambilMatkulMap.containsKey(idMatkul)) {
                        usersRef.child(nim).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnap: DataSnapshot) {
                                val nama = userSnap.child("name").getValue(String::class.java) ?: "Tidak Diketahui"

                                val itemView = layoutInflater.inflate(R.layout.item_mahasiswa_nilai, binding.containerMahasiswa, false)
                                val tvNama = itemView.findViewById<TextView>(R.id.tvNamaMhs)
                                val tvNim = itemView.findViewById<TextView>(R.id.tvNimMhs)
//                                val etNilai = itemView.findViewById<EditText>(R.id.etNilaiMhs)

                                tvNama.text = nama
                                tvNim.text = nim

                                binding.containerMahasiswa.addView(itemView)
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BeriNilaiActivity, "Gagal mengambil data mahasiswa", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun simpanNilaiMahasiswa() {
        for (i in 0 until binding.containerMahasiswa.childCount) {
            val itemView = binding.containerMahasiswa.getChildAt(i)
            val tvNim = itemView.findViewById<TextView>(R.id.tvNimMhs)
            val tvNama = itemView.findViewById<TextView>(R.id.tvNamaMhs)
            val etNilai = itemView.findViewById<EditText>(R.id.etNilaiMhs)

            val nim = tvNim.text.toString()
            val nama = tvNama.text.toString()
            val nilai = etNilai.text.toString()

            if (nilai.isNotEmpty()) {
                val nilaiData = mapOf(
                    "name" to nama,
                    "nilai" to nilai
                )
                nilaiRef.child(nim).setValue(nilaiData)
            }
        }

        Toast.makeText(this, "Nilai berhasil disimpan", Toast.LENGTH_SHORT).show()
        finish()
    }
}
