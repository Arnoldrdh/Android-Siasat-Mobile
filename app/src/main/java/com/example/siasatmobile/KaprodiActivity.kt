package com.example.siasatmobile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.siasatmobile.Model.Matkul
import com.example.siasatmobile.databinding.ActivityKaprodiBinding
import com.google.firebase.database.*

class KaprodiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKaprodiBinding
    private lateinit var matkulRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKaprodiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val namaUser = intent.getStringExtra("namaUser") ?: "Kaprodi"
        binding.tvNamaUser.text = "Halo, $namaUser 👋"

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }


        // Init Firebase reference
        matkulRef = FirebaseDatabase.getInstance().getReference("matkul")


        // Tambah matkul
        binding.btnTambahMatkul.setOnClickListener {
            val intent = Intent(this, TambahMatkulActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data setiap kembali ke activity ini
        tampilkanMatkul()
    }

    private fun tampilkanMatkul() {
        binding.matkulListContainer.removeAllViews()

        matkulRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val matkul = child.getValue(Matkul::class.java)
                    val matkulId = child.key ?: continue

                    val itemView = layoutInflater.inflate(R.layout.item_matkul, binding.matkulListContainer, false)

                    val tvNama = itemView.findViewById<TextView>(R.id.tvNama)
                    val tvSks = itemView.findViewById<TextView>(R.id.tvSks)
                    val tvDosen = itemView.findViewById<TextView>(R.id.tvDosen)
                    val btnEdit = itemView.findViewById<TextView>(R.id.btnEdit)
                    val btnHapus = itemView.findViewById<TextView>(R.id.btnDelete)

                    tvNama.text = matkul?.nama
                    tvSks.text = "${matkul?.sks} SKS"
                    tvDosen.text = "Dosen: ${matkul?.dosen}"

                    // Hapus matkul
                    btnHapus.setOnClickListener {
                        matkulRef.child(matkulId).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(this@KaprodiActivity, "Matkul dihapus", Toast.LENGTH_SHORT).show()
                                tampilkanMatkul()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@KaprodiActivity, "Gagal hapus matkul", Toast.LENGTH_SHORT).show()
                            }
                    }

                    // Edit matkul
                    btnEdit.setOnClickListener {
                        val intent = Intent(this@KaprodiActivity, TambahMatkulActivity::class.java).apply {
                            putExtra("mode", "edit")
                            putExtra("editId", matkulId)
                            putExtra("nama", matkul?.nama)
                            putExtra("sks", matkul?.sks)
                            putExtra("dosen", matkul?.dosen)
                        }
                        startActivity(intent)
                    }

                    binding.matkulListContainer.addView(itemView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@KaprodiActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
