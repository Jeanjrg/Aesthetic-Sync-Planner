# 🌸 Aesthetic Sync Planner

**Aesthetic Sync Planner** adalah aplikasi Android manajemen jadwal yang menggabungkan keindahan desain (aesthetic) dengan fungsionalitas sinkronisasi kalender yang cerdas. Aplikasi ini dirancang untuk membantu mahasiswa dan profesional mengatur waktu mereka dengan lebih menyenangkan dan terorganisir.

---

## ✨ Fitur Utama

*   **📅 Full Monthly Calendar**: Navigasi kalender satu bulan penuh yang memudahkan pemilihan tanggal dan pemantauan jadwal secara luas.
*   **🔄 Google Calendar Integration**: Sinkronisasi otomatis untuk menarik data Hari Libur Nasional Indonesia secara real-time.
*   **🌓 Smart Dark Mode**: Fitur Mode Malam yang estetik dengan tombol saklar kustom (Matahari & Bulan) yang menyimpan preferensi pengguna secara permanen.
*   **🔔 Intelligent Notifications**: Sistem pengingat otomatis (5m, 10m, 30m, 1j) sebelum acara dimulai agar Anda tidak pernah melewatkan agenda penting.
*   **✅ Persistence Checkbox**: Status penyelesaian tugas (centang) yang tersimpan permanen di database lokal, memudahkan pelacakan progres harian.
*   **💾 Offline Mode**: Menggunakan SQLite untuk menyimpan data secara lokal sehingga jadwal tetap dapat diakses meskipun tanpa koneksi internet.
*   **📝 Expandable Details**: Ketuk pada jadwal untuk melihat deskripsi lengkap tanpa harus berpindah layar (Fast-View).

---

## 🚀 Teknologi yang Digunakan

*   **Language**: Java
*   **UI Framework**: Material Design 3 (M3)
*   **Networking**: Retrofit 2 & GSON (Google Calendar API)
*   **Local Database**: SQLite (SQLiteOpenHelper)
*   **Background Task**: AlarmManager (for Notifications)
*   **Navigation**: Android Navigation Components

---

## 🛠️ Cara Instalasi

1.  **Clone Repositori**:
    ```bash
    git clone https://github.com/jpael/Aesthetic-Sync-Planner.git
    ```
2.  **Buka di Android Studio**: Pastikan Anda menggunakan versi terbaru (Ladybug atau lebih tinggi).
3.  **Sync Gradle**: Tunggu hingga proses sinkronisasi Gradle selesai.
4.  **Jalankan**: Klik tombol 'Run' dan pilih Emulator atau perangkat HP Anda.

> **Catatan Penting**: Jika mengalami kendala `AccessDeniedException` saat build, pastikan untuk menonaktifkan sementara sinkronisasi OneDrive atau pindahkan proyek ke folder lokal.

---

## 📸 Tampilan Aplikasi

| Home (Light) | Dark Mode | Add Event |
| :---: | :---: | :---: |
| ![Light Mode](https://via.placeholder.com/200x400?text=Light+Mode) | ![Dark Mode](https://via.placeholder.com/200x400?text=Dark+Mode) | ![Add Event](https://via.placeholder.com/200x400?text=Add+Event) |

---

## 👩‍💻 Kontributor

*   **Jean Patra Paeloran** - *Lead Developer*

---

## 📄 Lisensi

Proyek ini dibuat untuk tujuan akademik dan presentasi. Semua aset desain dan kode dilindungi oleh hak cipta pengembang.

---
*Dibuat dengan ❤️ untuk manajemen waktu yang lebih estetik.*
