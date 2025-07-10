package com.example.siasatmobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.siasatmobile.Model.Matkul
import com.example.siasatmobile.databinding.ActivityDosenBinding
import com.google.firebase.database.*

class DosenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDosenBinding
    private lateinit var matkulRef: DatabaseReference
    private lateinit var namaDosen: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDosenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil nama dosen dari intent
        namaDosen = intent.getStringExtra("namaUser") ?: "Dosen"
        binding.tvNamaDosen.text = "Halo, $namaDosen 👋"

        // Inisialisasi Firebase reference
        matkulRef = FirebaseDatabase.getInstance().getReference("matkul")

        // Tombol Logout
        binding.btnLogout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        tampilkanMatkulYangDiampu()
    }

    private fun tampilkanMatkulYangDiampu() {
        binding.containerMatkulDosen.removeAllViews()

        matkulRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val matkul = child.getValue(Matkul::class.java)
                    val idMatkul = child.key ?: continue

                    // Tampilkan hanya jika nama dosennya cocok
                    matkul?.let {
                        if (it.dosen.equals(namaDosen, ignoreCase = true)) {
                            val itemView = layoutInflater.inflate(R.layout.item_matkul_dosen, binding.containerMatkulDosen, false)

                            val tvNama = itemView.findViewById<TextView>(R.id.tvNamaMatkul)
                            val tvSks = itemView.findViewById<TextView>(R.id.tvSksMatkul)
                            val btnLihatNilai = itemView.findViewById<Button>(R.id.btnLihatNilai)
                            val btnBeriNilai = itemView.findViewById<Button>(R.id.btnBeriNilai)

                            tvNama.text = it.nama
                            tvSks.text = "${it.sks} SKS"

                            // Klik tombol "Lihat Nilai"
                            btnLihatNilai.setOnClickListener {
                                val intent = Intent(this@DosenActivity, LiatNilaiActivity::class.java)
                                intent.putExtra("idMatkul", idMatkul)
                                intent.putExtra("namaMatkul", matkul.nama)
                                startActivity(intent)
                            }

                            // Klik tombol "Beri Nilai"
                            btnBeriNilai.setOnClickListener {
                                val intent = Intent(this@DosenActivity, BeriNilaiActivity::class.java)
                                intent.putExtra("idMatkul", idMatkul)
                                intent.putExtra("namaMatkul", matkul.nama)
                                startActivity(intent)
                            }

                            binding.containerMatkulDosen.addView(itemView)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DosenActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
