package com.example.siasatmobile

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.siasatmobile.databinding.ActivityLiatNilaiBinding
import com.google.firebase.database.*

class LiatNilaiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiatNilaiBinding
    private lateinit var nilaiRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiatNilaiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data intent
        val idMatkul = intent.getStringExtra("idMatkul")
        val namaMatkul = intent.getStringExtra("namaMatkul")

        // Set judul
        binding.tvJudulMatkul.text = "Nilai Mahasiswa\n$namaMatkul"

        //kembali ke halamn awal
        binding.btnBack.setOnClickListener {
            finish()
        }


        // Referensi ke Firebase
        nilaiRef = FirebaseDatabase.getInstance().getReference("nilai/$idMatkul")

        tampilkanNilaiMahasiswa()
    }

    private fun tampilkanNilaiMahasiswa() {
        binding.containerNilaiMahasiswa.removeAllViews()

        nilaiRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(this@LiatNilaiActivity, "Belum ada nilai untuk matkul ini", Toast.LENGTH_SHORT).show()
                    return
                }

                for (child in snapshot.children) {
                    val nim = child.key ?: continue
                    val name = child.child("name").getValue(String::class.java) ?: "Mahasiswa"
                    val nilai = child.child("nilai").getValue(String::class.java) ?: "-"

                    val itemView = layoutInflater.inflate(R.layout.item_mahasiswa_nilai, binding.containerNilaiMahasiswa, false)

                    itemView.findViewById<TextView>(R.id.tvNamaMhs).text = name
                    itemView.findViewById<TextView>(R.id.tvNimMhs).text = nim
                    itemView.findViewById<TextView>(R.id.etNilaiMhs).text = "Nilai: $nilai"

                    binding.containerNilaiMahasiswa.addView(itemView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LiatNilaiActivity, "Gagal mengambil nilai", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
