package com.example.siasatmobile

import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.siasatmobile.databinding.ActivityAmbilMatkulBinding
import com.google.firebase.database.*

class AmbilMatkulActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAmbilMatkulBinding
    private lateinit var matkulRef: DatabaseReference
    private lateinit var ambilMatkulRef: DatabaseReference

    private lateinit var nimMahasiswa: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAmbilMatkulBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil NIM dari intent
        nimMahasiswa = intent.getStringExtra("nimUser") ?: return

        matkulRef = FirebaseDatabase.getInstance().getReference("matkul")
        ambilMatkulRef = FirebaseDatabase.getInstance().getReference("ambilMatkul").child(nimMahasiswa)

        tampilkanDaftarMatkul()

        binding.btnSimpan.setOnClickListener {
            simpanMatkulYangDipilih()
        }
    }

    private fun tampilkanDaftarMatkul() {
        matkulRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val idMatkul = child.key ?: continue
                    val namaMatkul = child.child("nama").getValue(String::class.java) ?: continue

                    val checkbox = layoutInflater.inflate(R.layout.item_checkbox_matkul, binding.containerCheckbox, false) as CheckBox
                    checkbox.text = namaMatkul
                    checkbox.tag = idMatkul

                    binding.containerCheckbox.addView(checkbox)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AmbilMatkulActivity, "Gagal memuat matkul", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun simpanMatkulYangDipilih() {
        val selectedMatkul = mutableMapOf<String, Boolean>()

        for (i in 0 until binding.containerCheckbox.childCount) {
            val checkbox = binding.containerCheckbox.getChildAt(i) as CheckBox
            if (checkbox.isChecked) {
                val idMatkul = checkbox.tag.toString()
                selectedMatkul[idMatkul] = true
            }
        }

        if (selectedMatkul.isEmpty()) {
            Toast.makeText(this, "Pilih minimal satu mata kuliah", Toast.LENGTH_SHORT).show()
            return
        }

        ambilMatkulRef.setValue(selectedMatkul).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Berhasil menyimpan matkul", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Gagal menyimpan matkul", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
