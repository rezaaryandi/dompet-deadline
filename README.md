# Dompet Deadline

> Kelola uang dan tenggat kuliah, dalam satu tempat.

Aplikasi desktop untuk mahasiswa yang ingin memahami pengeluaran sekaligus mengelola tugas kuliah. Transaksi pengeluaran dapat ditautkan dengan tugas tertentu sehingga biaya setiap tugas terlihat langsung, tanpa menggabungkan catatan uang dan daftar tugas secara manual.

## Tampilan

Edisi Ocean Blue Modular: palet referensi `#001D39`, `#0A4174`, `#49769F`, `#4E8EA2`, `#6EA2B3`, `#7BBDE8`, `#BDD8E9`, dengan tint netral biru dan putih untuk keterbacaan. Tema terang dan gelap, kartu saldo utama, ikon navigasi vektor, tabel dan grafik selaras. Status tetap dituliskan dengan teks agar tidak bergantung pada warna.

## Fitur

| Fitur | Implementasi |
|---|---|
| Transaksi | Tambah, ubah, hapus, pemasukan/pengeluaran, kategori kustom, tautan tugas opsional, pengurutan tabel |
| Tugas kuliah | Tambah, ubah, hapus, tandai selesai/buka kembali, tenggat, prioritas, total biaya pengeluaran terkait |
| Dashboard | Sisa uang, pemasukan/pengeluaran bulan berjalan, tiga tenggat belum selesai, biaya tugas aktif |
| Grafik | Grafik batang horizontal per kategori dan bulan; custom `paintComponent`, tanpa library grafik |
| Pengingat | `javax.swing.Timer` setiap 60 detik, tenggat 0–24 jam, `SystemTray` atau dialog cadangan, deduplikasi persisten per tugas/tenggat |
| CSV | Ekspor seluruh transaksi, impor file UTF-8 dengan validasi dan transaksi database atomik |
| Database | SQLite melalui JDBC; data tetap ada setelah aplikasi ditutup |
| Tema | FlatLaf terang/gelap dan Java Preferences untuk menyimpan pilihan |
| Pencarian & filter | Transaksi berdasarkan jenis dan kata kunci; tugas berdasarkan status/prioritas dan kata kunci |
| Anggaran | Batas per kategori pengeluaran dan bulan, progres, peringatan pada 80% dan 100% |
| Build & packaging | Maven, JUnit 5, fat JAR, skrip jpackage Windows/Linux/macOS, GitHub Actions untuk build dan rilis |

## Cara menjalankan

### Windows (disarankan untuk pemula)

1. Pasang JDK 21 (minimal JDK 11 untuk aplikasi). Pastikan `java -version` dan `javac -version` berfungsi di terminal.
2. Ekstrak ZIP proyek ke satu folder. Pastikan `pom.xml` terlihat di folder tersebut.
3. Klik dua kali `Jalankan-Windows.bat`. Jika belum ada JAR, skrip akan menjalankan Maven; bila Maven belum terpasang, skrip mengunduh Maven 3.9.9 dari Maven Central dan memeriksa SHA-512. **Koneksi internet diperlukan pada build pertama** untuk mengunduh dependensi.
4. JAR hasil build berada di `target/DompetDeadline.jar`.

### IDE atau terminal

Buka folder yang berisi `pom.xml` sebagai proyek Maven di IntelliJ IDEA, NetBeans, Eclipse atau VS Code (Java Extension Pack). Jalankan kelas `id.dompetdeadline.Main`, atau gunakan terminal:

```sh
mvn clean verify
java -jar target/DompetDeadline.jar
```

`mvn clean verify` mengompilasi program, menjalankan unit test/integration test dan membangun fat JAR dengan SQLite JDBC, FlatLaf, dan dependensi runtime. Jangan mengunggah `original-DompetDeadline.jar` karena itu bukan fat JAR.

### Paket aplikasi jpackage (bonus)

Gunakan JDK 17+ yang menyediakan `jpackage` (disarankan JDK 21). Build paket dilakukan pada OS tujuan.

Windows PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/package-windows.ps1
```

Hasil: `dist/DompetDeadline-Windows.zip` berisi app-image portable, termasuk runtime Java. Ekstrak seluruh folder lalu buka `DompetDeadline.exe` di dalamnya.

Linux/macOS:

```sh
mvn clean verify
bash scripts/package.sh
```

Hasil berada di `dist/packages/`. App-image bukan installer MSI/DMG dan tidak perlu installer eksternal. Untuk pengguna Windows, workflow GitHub juga menyediakan paket ZIP portable secara otomatis apabila build berhasil.

## Penggunaan

1. Buka **Tugas kuliah** dan tambah tugas dengan tenggat `YYYY-MM-DD HH:mm`.
2. Di **Transaksi**, catat pemasukan atau pengeluaran. Masukkan jumlah sebagai rupiah bulat tanpa titik/koma (misal `25000`). Pilih tugas terkait bila ada. Data awal hanya berisi kategori bawaan; transaksi dan tugas dimulai kosong.
3. Lihat biaya tugas pada tabel dan dashboard. Biaya tugas dihitung dari pengeluaran terkait pada seluruh periode; saldo dashboard hanya bulan berjalan. Tugas yang dihapus tidak menghapus transaksi historis: tautan tugas menjadi kosong.
4. Di **Analisis**, pilih bulan `YYYY-MM` untuk melihat grafik.
5. Di **Anggaran**, buat batas untuk kategori pengeluaran dan bulan tertentu. Peringatan muncul saat status melewati 80%/100% pada bulan berjalan.
6. Biarkan aplikasi terbuka agar pengingat tenggat berjalan. Aplikasi tidak memiliki layanan notifikasi di latar belakang ketika sudah ditutup. Notifikasi yang telah dikirim untuk tenggat yang sama tidak dikirim berulang, termasuk setelah aplikasi dibuka lagi.
7. Ekspor atau impor CSV melalui menu Berkas. Impor menambah transaksi baru, bukan mengganti data lama. Pastikan kategori dan judul tugas pada CSV sudah ada; judul tugas harus unik bila dipakai sebagai referensi impor. File CSV yang memiliki satu baris tidak valid ditolak seluruhnya. Jangan impor file yang sama berulang kali kecuali memang ingin menggandakan data.

### Format CSV

```csv
tanggal,kategori,jenis,jumlah,tugas terkait,catatan
2026-11-26,Kuliah,Pengeluaran,25000,Laporan PBO,Cetak laporan
2026-11-26,Uang saku,Pemasukan,1000000,,Uang bulanan
```

Format tanggal ISO dan jumlah berupa bilangan bulat. Kolom teks yang memiliki koma, kutip, dan baris baru ditangani sesuai format CSV. Berkas diekspor dalam UTF-8. Untuk membuka CSV yang berasal dari pihak lain di spreadsheet, perlakukan kolom teks sebagai teks, bukan rumus.

### Pintasan keyboard

| Pintasan | Aksi |
|---|---|
| Ctrl+N | Transaksi baru |
| Ctrl+T | Tugas baru |
| Ctrl+I / Ctrl+E | Impor / ekspor CSV |
| Ctrl+1 sampai Ctrl+5 | Navigasi halaman |
| Ctrl+L | Ganti tema |
| Ctrl+Q | Keluar |
| Tab / Enter / Esc | Pindah isian / simpan dialog / batalkan dialog |

Pada macOS gunakan Command sebagai pengganti Ctrl untuk menu aplikasi.

## Arsitektur proyek

```text
src/main/java/id/dompetdeadline/
├── Main.java                    # Entry point dan inisialisasi database
├── model/                       # Objek domain dan snapshot data
│   ├── Kategori.java
│   ├── Tugas.java
│   ├── Transaksi.java
│   ├── Anggaran.java
│   └── Data.java
├── repository/                  # SQL dan JDBC saja
│   ├── Database.java
│   ├── Repository.java
│   └── SqliteRepository.java
├── service/                     # Logika independen dari GUI
│   ├── ApplicationService.java  # Operasi aplikasi dan koordinasi repository
│   ├── Finance.java             # Perhitungan dan validasi
│   └── Csv.java                 # Pembacaan/penulisan CSV
└── view/                        # Tampilan Swing, layout dan event
    ├── MainFrame.java           # Navigasi, SwingWorker, lifecycle
    ├── Sidebar.java             # Navigasi visual
    ├── PagePanel.java           # Kelas abstrak dasar halaman
    ├── DashboardPanel.java
    ├── TransaksiPanel.java
    ├── TugasPanel.java
    ├── AnalisisPanel.java
    ├── AnggaranPanel.java
    ├── FormDialogs.java         # Dialog input dan validasi
    ├── CsvActions.java           # Dialog pemilihan file
    ├── Theme.java
    ├── Ui.java
    ├── BarChart.java
    └── Reminder.java
src/test/java/                   # Unit test JUnit + integration test SQLite
scripts/                         # Build, packaging, dan QA visual terpisah
.github/workflows/build.yml       # CI dan rilis dari tag Git
```

Alur data: `View → ApplicationService → Repository → SQLite`. `Main` menyusun dependensi; `MainFrame` menerima `ApplicationService` melalui constructor. Halaman Swing tidak mengakses repository atau SQL. Setiap halaman mewarisi `PagePanel` dan mengimplementasikan `buildContent()`. `Finance` dan `Csv` dapat diuji tanpa Swing; `Repository` dapat diganti dengan test double tanpa mengubah tampilan. Seluruh SQL ada di package `repository`, semua input SQL menggunakan `PreparedStatement`, dan operasi database diproses dengan `SwingWorker`. Data uang berupa `long` dalam rupiah; nilai dihitung tanpa floating-point. Tampilan disusun dengan `BorderLayout`, `GridLayout`, `GridBagLayout`, `BoxLayout`, dan `FlowLayout`. Tidak ada penempatan komponen memakai layout null.

Database disimpan di `~/.dompet-deadline/dompet.db` (di Windows, `C:\Users\<nama>\.dompet-deadline\dompet.db`). Data pribadi tidak disertakan dalam repo. Tabel: `kategori`, `tugas`, `transaksi`, `anggaran`, `pengingat`. `transaksi.kategori_id` wajib dan `transaksi.tugas_id` opsional. Skema dan seed kategori dibuat oleh `Database.initialize()`.

## Pengujian

```sh
mvn test
```

Terdapat 16 metode `@Test` JUnit: 7 pengujian Finance, 3 CSV, 4 ApplicationService dan 2 integration test SQLite (persistensi, foreign key, deduplikasi, rollback impor). Jalankan Maven untuk melihat hasil resmi.

Pemeriksaan lokal yang sudah dilakukan tanpa dependency: kompilasi seluruh source utama dengan `javac --release 11`, 16 pemeriksaan smoke test logika/CSV, 17 pemeriksaan modular (service, navigasi, filter, pergantian tema dan pembaruan tugas menggunakan repository uji), dan render GUI dengan data contoh melalui Xvfb. Lihat `docs/offline-test.txt`. JUnit, JDBC/SQLite melalui Java, FlatLaf, fat JAR Maven, dan jpackage belum dapat dijalankan di lingkungan pembuatan karena Maven/dependensi tidak tersedia dan jaringan ke Maven Central tidak dapat diakses. Workflow CI serta skrip build disediakan agar pengujian dan packaging dilakukan pada komputer yang terhubung internet. Jangan menganggapnya sudah lulus sebelum menjalankan build sendiri.

Lihat [panduan struktur dan alur kode](docs/ARSITEKTUR.md), [panduan pemeriksaan manual](docs/PENGUJIAN.md) dan [panduan pengumpulan](docs/PENGUMPULAN.md).

## Lisensi

MIT. Dependensi pihak ketiga tetap tunduk pada lisensi masing-masing.
