# Pemeriksaan manual sebelum rilis

Jalankan `mvn clean verify` terlebih dahulu. Tes ini dilakukan pada JAR hasil build dengan SQLite asli, bukan dengan preview. Gunakan profil Windows/pengguna uji bila tersedia.

| Skenario | Hasil yang diharapkan |
|---|---|
| Jalankan JAR pertama kali | Database di folder pengguna terbentuk; kategori tersedia; transaksi/tugas kosong |
| Tambah tugas Laporan, tenggat <24 jam | Tugas muncul di daftar dan dashboard; prioritas terbaca |
| Tambah pemasukan 1000000 dan pengeluaran 25000 terkait Laporan | Saldo bulan ini Rp975.000, biaya tugas Rp25.000 |
| Ubah pengeluaran menjadi 30000 | Saldo dan biaya tugas berubah |
| Tambah kategori Transport kampus | Kategori dapat dipilih sesuai jenis |
| Masukkan jumlah negatif atau bukan angka, tanggal salah | Validasi menjelaskan perbaikan, data tidak tersimpan |
| Urutkan kolom jumlah dan pilih baris untuk Ubah | Baris yang benar dibuka walau urutan berubah |
| Cari kategori / filter jenis | Hanya data cocok ditampilkan |
| Tandai tugas selesai / buka kembali | Dashboard dan biaya tugas aktif diperbarui |
| Buat anggaran kategori Kuliah 35000 | Progres ~85%, peringatan 80% muncul |
| Tambah pengeluaran Kuliah hingga 35000 | Status Batas tercapai dan peringatan baru muncul |
| Pilih bulan sebelumnya di Analisis | Grafik hanya pengeluaran bulan tersebut |
| Ekspor CSV, lihat header dan baris transaksi | Semua transaksi, enam kolom, data teks ter-escape |
| Impor CSV valid / CSV rusak | Data valid bertambah; data rusak ditolak atomik |
| Tunggu pemeriksaan Timer | Notifikasi satu kali untuk tenggat dalam 24 jam; dialog jika tray tidak didukung |
| Tutup dan buka kembali | Data dan tema tetap ada, pengingat sama tidak berulang |
| Hapus tugas yang mempunyai transaksi | Konfirmasi muncul; transaksi tetap ada dengan tautan tugas kosong |
| Hapus transaksi | Konfirmasi muncul; saldo, grafik, dan biaya berubah |
| Gunakan Tab / Enter, ubah ukuran jendela | Alur fokus wajar, dialog dapat disimpan dengan Enter, layout tidak tumpang tindih |
| Bangun paket jpackage dan buka exe Windows | Aplikasi dapat dijalankan dari seluruh folder app-image |

## Keputusan desain dan batasan

- Tidak ada login atau jaringan penyimpanan karena tugas tidak mewajibkannya. Data bersifat lokal dan bukan database terenkripsi.
- Tugas yang terlambat tetap muncul di daftar tugas aktif, tetapi notifikasi hanya untuk tenggat yang akan datang dalam 24 jam.
- Budget warning otomatis dipicu ketika status anggaran berubah; status tetap terlihat pada dashboard dan tabel anggaran.
- Impor CSV tidak membuat kategori/tugas baru secara otomatis. Buat referensi dahulu dan gunakan judul tugas yang unik.
- Kode starter resmi dari asisten lab belum tersedia di percakapan ini. Koneksi JDBC dan repository disusun sendiri berdasarkan skema modul; bandingkan dengan starter resmi jika dosen mewajibkan penggunaan kode tersebut.
- Skrip `scripts/preview` adalah alat QA visual dengan repository contoh atau repository in-memory khusus pengujian, bukan aplikasi produksi.

## Pemeriksaan modular otomatis

Di Linux dengan JDK dan Xvfb, jalankan `bash scripts/verify-offline.sh`. Skrip mengompilasi source Java 11, menguji logika/CSV dan menjalankan navigasi, pencarian, perubahan tema dan perubahan status tugas dengan repository in-memory. Pemeriksaan tersebut tidak menggantikan `mvn clean verify` dengan JDBC dan FlatLaf.
