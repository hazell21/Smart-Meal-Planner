# Smart Meal Planner untuk Mahasiswa berbasis OOP

Aplikasi **Smart Meal Planner** merupakan sistem manajemen perencanaan makanan (*meal planning*) dinamis yang dirancang khusus untuk membantu mahasiswa dalam mengoptimalkan pengelolaan pola makan harian. Sistem ini dibangun dengan paradigma **Pemrograman Berorientasi Objek (OOP)** dan arsitektur **Model-View-Controller (MVC)** untuk memastikan struktur kode yang modular, adaptif, dan mudah dikembangkan.

Sistem ini membantu mahasiswa mengatasi kendala keterbatasan waktu, manajemen pengeluaran finansial konsumsi, serta efisiensi pemanfaatan logistik bahan makanan yang tersedia di gudang penyimpanan mereka.

---

## Fitur Utama Sistem

1. **Manajemen User & Autentikasi:** Fasilitas login berbasis hak akses (*role-based permission*), pengelolaan profil data mahasiswa, dan konfigurasi budget konsumsi awal.
2. **Manajemen Logistik Bahan & Resep:** Pengelolaan database komponen bahan baku pangan beserta harganya, input menu resep internal, dan kalkulasi otomatis total biaya bahan dari RAM.
3. **Meal Planner & Penjadwalan:** Fitur menyusun rencana jadwal menu harian mahasiswa (Sarapan, Makan Siang, Makan Malam) dan akumulasi total energi/gizi harian.
4. **Sistem Rekomendasi Cerdas (Live Web API):** Integrasi mesin rekomendasi menggunakan *Spoonacular API* untuk menyajikan opsi menu online yang disesuaikan secara dinamis berdasarkan ketersediaan bahan baku lokal di database serta batasan sisa budget maksimal pengguna.
5. **Budget Manager:** Analisis finansial *real-time* untuk memantau akumulasi pengeluaran dan melakukan pengecekan kelayakan anggaran anggaran ("Anggaran Aman" / "Overbudget").

---

## Tech Stack & Prasyarat Perangkat Lunak

* **Backend Environment:** Java (Java Servlet API 3.x / 4.x).
* **Frontend Environment:** JavaScript (Vanilla JS untuk manipulasi DOM responsif), HTML5, CSS3 (Bootstrap Framework).
* **Database Management:** MySQL Server (RDBMS).
* **Web Server:** Apache Tomcat (v8.5 atau yang kompatibel).
* **IDE Pengembangan:** NetBeans IDE.
* **Library Pertukaran Data:** JSON Library (`org.json`) untuk rest-api parsing.

---

## Implementasi Prinsip Pemrograman Berorientasi Objek (OOP)

Arsitektur aplikasi ini menerapkan pilar-pilar fundamental OOP secara eksplisit:
* **Abstraction & Abstract Class:** Kelas `User` dideklarasikan sebagai kelas abstrak dasar penyedia metode wajib `Login()`.
* **Inheritance (Pewarisan):** Kelas `Mahasiswa` mewarisi properti global dari kelas `User` dengan spesialisasi atribut keuangan tambahan (`budget`).
* **Polymorphism & Interface:** Pemanfaatan *Interface* `Rekomendasi` yang diimplementasikan secara konkrit oleh kelas `RekomendasiMakanan` untuk melangsungkan metode penentu rekomendasi menu.
* **Association, Aggregation, & Composition:** Kelas `MealPlanner` memiliki keterikatan daftaran (*List*) terhadap objek kelas `Resep` dan mengomposisikan kelas `Jadwal` dalam membentuk kesatuan struktural rencana berkala.

---

## Hasil Analisis Kualitas Perangkat Lunak (OO Metrics)

Kode program pada proyek ini telah diuji secara statis menggunakan pengujian otomatis berbasis **Chidamber & Kemerer (CK) Metrics Suite**. Ringkasan performa internal struktur kode adalah sebagai berikut:

* **Total Ukuran Sistem (LOC):** 895 Baris Kode.
* **Rata-rata Kompleksitas Metode (WMC):** 13.81 (Beban komputasi merata, terhindar dari *God Class Anti-Pattern*).
* **Rata-rata Ketergantungan Objek (CBO):** 2.31 (Terbukti memenuhi kaidah *Loose Coupling* atau tingkat ketergantungan antar-kelas yang sangat aman dan modular).
* **Maksimal Kedalaman Pewarisan (DIT):** 2 (Tercatat pada kelas `Mahasiswa` sebagai turunan `User`, mengonfirmasi struktur hierarki yang aman dari risiko *error cascade*).
* **Kepaduan Fungsi Kelas (LCOM):** Mayoritas kelas bernilai 0, membuktikan tingkat *High Cohesion* di mana metode di dalam kelas secara kompak menggunakan variabel atribut yang tersedia.

---

## Langkah Pemasangan dan Menjalankan Proyek

1. **Kloning Repositori:**
   ```bash
   git clone [https://github.com/username/repository-name.git](https://github.com/username/repository-name.git)
