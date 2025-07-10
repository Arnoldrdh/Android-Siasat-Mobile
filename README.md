# 📱 Siasat Mobile

Aplikasi mobile untuk manajemen mata kuliah berbasis Firebase Realtime Database.

---

## 👥 Role Pengguna & Akses

Aplikasi mendukung 3 role utama: **Mahasiswa**, **Dosen**, dan **Kaprodi**, dengan fitur dan hak akses berbeda:

### 🔑 Login & Register
- Fitur **Register** hanya tersedia untuk **Mahasiswa** dan **Dosen**
- Role **Kaprodi** hanya bisa login menggunakan akun default (tidak bisa register dari aplikasi)

### 📌 Fitur per Role

#### 👨‍🎓 Mahasiswa
- Register & login
- Ambil mata kuliah
- Lihat nilai yang diberikan oleh dosen

#### 👨‍🏫 Dosen
- Register & login
- Lihat daftar mata kuliah yang diampu
- Input dan lihat nilai mahasiswa

#### 👨‍💼 Kaprodi
- Login menggunakan akun default
- Tambah, ubah, dan hapus mata kuliah (CRUD)
- 📝 **Catatan:** Saat membuat mata kuliah dan mengisi NIM dosen pengampu, pastikan dosen tersebut sudah memiliki akun terlebih dahulu (sudah register). Jika tidak, maka dosen tidak akan bisa melihat mata kuliah yang diampunya.

---

### 🧪 Akun untuk Testing

| Role      | NIM       | Password   |
|-----------|-----------|------------|
| Kaprodi   | 672011    | kaprodi123 |
| Dosen     | 67021     | dosen123   |
| Mahasiswa | 672022100 | tota123    |
| (Mahasiswa juga bisa register akun baru di aplikasi) |

---

## 🔧 Teknologi yang Digunakan

- Kotlin + ViewBinding
- Firebase Realtime Database

---

## 📂 Struktur Firebase Realtime Database

```json
{
  "users": {
    "*nim*": {
      "name": "*nama pengguna*",
      "password": "*password*",
      "role": "*mahasiswa|dosen|kaprodi*"
    }
  },
  "matkul": {
    "*idMatkul*": {
      "nama": "*nama matkul*",
      "sks": *jumlah_sks*,
      "dosen": "*nim_dosen*"
    }
  },
  "ambilMatkul": {
    "*nim_mahasiswa*": {
      "*idMatkul1*": true,
      "*idMatkul2*": true
    }
  },
  "nilai": {
    "*idMatkul*": {
      "*nim_mahasiswa*": {
        "name": "*nama mahasiswa*",
        "nilai": *angka_nilai*
      }
    }
  }
}
