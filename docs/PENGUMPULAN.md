# Pengumpulan tugas GUI PBO

## Yang harus dipenuhi sebelum mengumpulkan

Modul mengharuskan repositori publik, README, screenshot/GIF, minimal 10 commit bermakna yang tersebar selama pengerjaan, minimal 3 unit test JUnit, executable JAR pada Releases, LICENSE dan .gitignore. Data pribadi tidak boleh diunggah. Tugas dikerjakan mandiri dan semua kode harus dipahami.

Project ini menyediakan bahan awal lengkap, bukan repositori GitHub yang sudah dipublikasikan. Riwayat commit belum dibuat agar tidak menyimulasikan pengerjaan historis. Lakukan pengujian, penyesuaian, dan commit sungguhan selama pengerjaan. Jangan mengklaim tes atau packaging telah lulus sebelum benar-benar dijalankan.

## Langkah publikasi

1. Buat repositori **public** bernama `dompet-deadline` di GitHub tanpa README tambahan. Pastikan file pribadi dan database tidak ikut.
2. Dari folder project, inisialisasi Git dan hubungkan repo:

```sh
git init
git branch -M main
git remote add origin https://github.com/USERNAME/dompet-deadline.git
```

3. Pelajari dan uji setiap bagian, lakukan perubahan dan commit yang bermakna secara bertahap. Contoh lingkup commit: model, database, repository, logika keuangan, transaksi, tugas, dashboard/grafik, pengingat, impor/ekspor, anggaran, tema, pengujian, dokumentasi, packaging. Jangan memecah commit secara artifisial hanya untuk memenuhi angka; sertakan perubahan/pemeriksaan yang benar-benar dilakukan.
4. Setelah versi siap, jalankan `mvn clean verify`, pastikan semua tes lulus, dan cek aplikasi dengan daftar `PENGUJIAN.md`.
5. Ganti screenshot preview README dengan screenshot dari aplikasi hasil build jika ingin menampilkan FlatLaf yang sebenarnya. Sesuaikan README dan lisensi sesuai pekerjaan dan identitasmu.
6. Push commit ke GitHub:

```sh
git push -u origin main
```

7. Aktifkan Actions pada repositori bila diperlukan. Setelah build berhasil, buat tag rilis:

```sh
git tag -a v1.0.0 -m "Release Dompet Deadline 1.0.0"
git push origin v1.0.0
```

Workflow mencoba membangun JAR, menjalankan JUnit dan jpackage Windows, serta menerbitkan Release dari tag. Bila workflow tidak digunakan, buka **Releases → Draft a new release** di GitHub, buat tag v1.0.0 dan unggah `target/DompetDeadline.jar` serta paket Windows portable yang sudah dibangun dan diuji.

## Yang diserahkan

- Tautan repositori: `https://github.com/USERNAME/dompet-deadline`
- Tautan Releases: `https://github.com/USERNAME/dompet-deadline/releases`
- Satu paragraf bonus: jelaskan fitur yang benar-benar berhasil dibangun dan diuji, meliputi impor CSV, tema FlatLaf, pencarian/filter, anggaran, Maven, dan paket jpackage apabila semuanya lulus verifikasi.

Modul menyebutkan 12 hari sejak dibagikan; tanggal dan media penyerahan tautan mengikuti arahan asisten lab.
