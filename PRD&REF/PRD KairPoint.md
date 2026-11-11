# PRD — Kasir Point

Versi 1.2 · Dokumen perencanaan aplikasi kasir (POS) berbasis Android

Perubahan terbaru (versi 1.2): halaman Beranda dihapus, setelah splash screen aplikasi langsung membuka halaman **Kasir**. Sidebar di HP awalnya tersembunyi (ada tombol buka/tutup), di tablet atau layar lebar otomatis terbuka.

\---

## 1\. Ringkasan

**Kasir Point** adalah aplikasi kasir (Point of Sale) untuk perangkat Android (HP dan tablet). Aplikasi pertama dibuat untuk **caffe shop**, tetapi dirancang fleksibel supaya bisa dipakai usaha lain (misalnya usaha keluarga) tanpa dibuat ulang.

* **Logo:** ikon pin/lokasi (warna oranye, sesuai referensi).
* **Bahasa aplikasi:** seluruhnya **Bahasa Indonesia**.
* **Prinsip utama:** mudah dipakai (user friendly), cepat, responsif di berbagai ukuran layar, dan tidak membingungkan karyawan baru.

## 2\. Tujuan

1. Karyawan bisa mencatat pesanan dan pembayaran dengan cepat (kurang dari 1 menit per transaksi).
2. Semua transaksi tersimpan rapi dan bisa dilihat kembali.
3. Pemilik cukup membuka menu **Laporan** untuk melihat omzet, tanpa perlu akun khusus pemilik.
4. Struktur data dan tampilan struk fleksibel agar bisa dipakai untuk jenis usaha lain.
5. Tampilan nyaman di HP maupun tablet dengan resolusi berbeda-beda.

## 3\. Pengguna

|Pengguna|Penjelasan|
|-|-|
|Karyawan / kasir|Pengguna utama. Semua karyawan memakai **satu akun bersama**.|
|Pemilik usaha|Tidak punya peran khusus. Masuk ke aplikasi yang sama lalu membuka menu Laporan dan Pengaturan.|

Tidak ada pembagian peran (role) dan **tidak ada PIN** untuk menu Laporan dan Produk pada versi 1.

## 4\. Teknologi

> Mengikuti asumsi bahwa teknologinya sama dengan proyek aplikasi mobile sebelumnya. Sesuaikan jika berbeda.

|Bagian|Pilihan|
|-|-|
|Platform|Android (HP dan tablet)|
|Bahasa|Kotlin|
|Tampilan|Jetpack Compose, memakai **WindowSizeClass** supaya layout menyesuaikan ukuran layar|
|Login|Firebase Authentication (email dan kata sandi)|
|Database|Cloud Firestore (cache offline dinyalakan)|
|Penyimpanan foto produk|Firebase Storage (opsional, bisa ditambahkan belakangan)|

## 5\. Struktur Folder Proyek

Buat satu folder utama, lalu masukkan semua bahan ke dalamnya **sebelum** mulai membuat kode.

```
KasirPoint/
├── docs/
│   ├── PRD.md                  ← dokumen ini
│   └── referensi/
│       ├── referensi-ui.jpg    ← gambar referensi desain (Purr'Coffee)
│       ├── logo-pin.png        ← logo ikon pin/lokasi (jika sudah ada)
│       └── catatan-desain.md   ← catatan warna, font, ikon (opsional)
├── app/                        ← dibuat otomatis oleh Android Studio
│   └── google-services.json    ← file dari Firebase (diletakkan di sini)
└── README.md                   ← penjelasan singkat proyek
```

Catatan:

* Folder `app/` akan muncul saat proyek dibuat di Android Studio. Pastikan lokasi proyeknya diarahkan ke folder `KasirPoint/`.
* Nama paket (package name) yang disarankan: `com.kasirpoint.app`. Nama ini harus **sama persis** saat mendaftarkan aplikasi di Firebase.

## 6\. Yang Perlu Disiapkan

### Perangkat dan akun

* \[ ] Android Studio (versi terbaru) dan JDK bawaannya
* \[ ] HP Android (aktifkan Opsi Pengembang dan Debug USB) atau emulator
* \[ ] Emulator tambahan untuk uji responsif: HP kecil, HP biasa, tablet 7 inci, tablet 10 inci
* \[ ] Akun Google untuk Firebase
* \[ ] Akun GitHub dan repository baru untuk proyek ini
* \[ ] Antigravity (untuk generate kode)

### Firebase

* \[ ] Buat proyek Firebase baru bernama **Kasir Point**
* \[ ] Daftarkan aplikasi Android dengan package name `com.kasirpoint.app`
* \[ ] Unduh `google-services.json` lalu taruh di folder `app/`
* \[ ] Aktifkan **Authentication** → metode Email/Password
* \[ ] Aktifkan **Cloud Firestore** (mulai dengan mode pengujian, lalu aturan keamanan dirapikan sebelum dipakai sungguhan)

### Bahan desain

* \[ ] Gambar referensi UI (masukkan ke `docs/referensi/`)
* \[ ] Logo ikon pin/lokasi (dipakai untuk ikon aplikasi dan splash screen)
* \[ ] Font (lihat bagian Desain)



## 7\. Desain dan Tampilan

Mengikuti gambar referensi, termasuk **menu samping (sidebar)**.

### Warna (perkiraan dari referensi, boleh disesuaikan)

|Fungsi|Perkiraan warna|
|-|-|
|Latar utama|Krem/peach muda `#FDEBDD`|
|Kartu dan kolom isian|Putih `#FFFFFF`|
|Aksen utama (menu aktif, tab aktif, tombol Filter, logo)|Oranye `#F28C38`|
|Tombol aksi (Tambah ke Keranjang, Bayar)|Cokelat `#B07A52`|
|Chip pilihan terpilih|Peach `#F8D5B8`|
|Teks utama|Cokelat gelap `#3B2A20`|

### Gaya

* Sudut membulat pada kartu, kolom pencarian, dan tombol (gaya pil).
* Ikon bergaya garis (outline), sederhana.
* Font bersih dan membulat, mirip **Poppins** atau **Nunito** (dicocokkan lagi dengan gambar referensi).
* Banyak ruang kosong, tidak padat.

### Sidebar (menu samping)

Sidebar dipertahankan seperti referensi, dengan **tombol untuk membuka dan menutup** (seperti aplikasi Sadewa). Tombol ini selalu ada di semua ukuran layar.

Isi sidebar dari atas ke bawah:

* Logo dan nama **Kasir Point**
* **Kasir · Riwayat · Produk · Laporan · Pengaturan**
* Di bagian bawah: **Keluar**

Perilaku sidebar mengikuti ukuran layar:

|Ukuran layar|Perilaku sidebar|
|-|-|
|HP (layar sempit)|Awalnya **tersembunyi**. Dibuka lewat tombol menu di pojok atas, muncul menutupi sebagian layar, menutup otomatis setelah memilih menu.|
|Tablet atau layar lebar (misalnya laptop)|**Otomatis terbuka** saat aplikasi dibuka. Bisa ditutup lewat tombol, sehingga konten memenuhi layar, dan dibuka lagi kapan saja.|

Alasannya: di HP, sidebar yang selalu tampil akan memakan hampir separuh layar sehingga kartu produk jadi sangat sempit.

### Perubahan dari referensi ke Kasir Point

|Referensi (tablet/web)|Kasir Point|
|-|-|
|Sidebar tetap tampil|Sidebar bisa dibuka/ditutup (HP: awalnya tertutup, tablet/laptop: awalnya terbuka)|
|Grid 3 kolom tetap|Jumlah kolom menyesuaikan layar (lihat bagian Responsif)|
|Cart summary di sisi kanan|Tablet: panel kanan. HP: bar keranjang di bawah yang membuka halaman Keranjang|
|Menu Donate, Partners|Dihapus (tidak relevan)|
|Harga dolar|Rupiah|

## 8\. Responsif (HP dan Tablet)

Aplikasi harus rapi di berbagai ukuran dan resolusi, baik posisi tegak maupun mendatar.

|Ukuran|Lebar kira-kira|Kolom produk|Keranjang|Sidebar|
|-|-|-|-|-|
|HP|di bawah 600dp|2 kolom|Bar bawah, buka halaman penuh|Tersembunyi, buka/tutup lewat tombol|
|Tablet kecil|600–840dp|3 kolom|Panel kanan (jika muat) atau halaman penuh|Otomatis terbuka, bisa ditutup|
|Tablet besar / laptop|di atas 840dp|4 kolom|Panel kanan selalu tampil|Otomatis terbuka, bisa ditutup|

Aturan umum:

* Jangan memakai ukuran tetap dalam piksel untuk tata letak utama. Gunakan ukuran fleksibel.
* Teks dan tombol tidak boleh terpotong di layar kecil.
* Gambar produk menyesuaikan lebar kartu dengan perbandingan tetap.
* Tampilan tetap benar saat layar diputar (tegak ke mendatar).
* Uji minimal di: HP kecil, HP biasa, tablet 7 inci, tablet 10 inci.

## 9\. Alur Membuka Aplikasi

1. Pengguna mengetuk **ikon aplikasi** (logo pin).
2. **Splash screen**: logo dan ucapan selamat datang, misalnya "Selamat datang di Kasir Point".
3. Aplikasi memeriksa apakah sudah ada akun yang login:

   * **Sudah login** → langsung masuk halaman **Kasir**. Tidak perlu memasukkan email dan kata sandi lagi.
   * **Belum login** → masuk halaman **Login**.
4. Setelah berhasil login atau mendaftar → halaman **Kasir**.
5. Pengguna baru login lagi **hanya jika menekan Keluar (logout)**.

Catatan: sesi login dijaga oleh Firebase Authentication, jadi tetap tersimpan walaupun aplikasi ditutup atau HP dimatikan.

## 10\. Daftar Halaman dan Kebutuhan

### 10.1 Splash Screen

* Logo pin dan ucapan selamat datang.
* Tampil singkat (sekitar 2 detik) sambil memeriksa status login.

### 10.2 Login dan Daftar Akun

* Daftar akun baru: nama usaha, email, kata sandi.
* Masuk dengan email dan kata sandi.
* Tombol lihat/sembunyikan kata sandi.
* Pesan kesalahan ramah, contoh: "Email atau kata sandi salah. Coba lagi ya."

### 10.3 Kasir (halaman utama)

* Halaman pertama yang tampil setelah splash screen atau login.
* Kolom pencarian produk.
* Tab kategori (Coffee, Non Coffee, Food, Snack, Dessert, dst. sesuai data).
* Kartu produk: foto (jika ada), nama, harga, tombol tambah.
* Jika produk punya varian (misalnya ukuran), muncul pilihan saat ditambahkan.
* Produk tanpa foto tetap tampil rapi dengan gambar pengganti (placeholder).
* HP: bar keranjang di bawah menampilkan jumlah item dan total, ketuk untuk membuka Keranjang.
* Tablet: ringkasan keranjang tampil di panel kanan.
* Produk yang stoknya habis diberi tanda "Habis" (hanya jika stok dinyalakan).

### 10.4 Keranjang

* Daftar item dengan tombol tambah/kurang jumlah dan hapus.
* Pilihan tipe pesanan: **Makan di tempat · Bawa pulang** (bisa diaktifkan/dinonaktifkan di Pengaturan).
* Kolom catatan pesanan (opsional).
* Ringkasan: Subtotal, Pajak (jika aktif), **Total**.
* Tombol **Bayar**.

### 10.5 Pembayaran

* Pilihan metode: **Tunai · QRIS · Transfer**.
* Tunai: isi uang diterima, kembalian dihitung otomatis. Ada tombol cepat (uang pas, 50.000, 100.000).
* QRIS dan Transfer: **hanya dicatat sebagai metode bayar** (tidak terhubung ke layanan pembayaran).
* Tombol **Selesaikan Transaksi**.

### 10.6 Transaksi Berhasil dan Struk

* Pesan sukses dan tampilan struk sesuai **template struk** di Pengaturan.
* Isi struk bisa meliputi: logo, nama usaha, alamat usaha, nomor telepon, nomor transaksi, tanggal dan jam, daftar item, subtotal, pajak, total, metode bayar, uang diterima, kembalian, catatan, dan pesan penutup.
* Tombol **Bagikan** (misalnya ke WhatsApp) dan **Transaksi Baru**.
* Cetak ke printer Bluetooth tidak termasuk versi 1.

### 10.7 Riwayat

* Daftar transaksi terbaru, dikelompokkan per tanggal.
* Filter tanggal dan pencarian nomor transaksi.
* Ketuk transaksi untuk melihat detail dan struk.
* Transaksi bisa **dibatalkan** dengan konfirmasi (status berubah menjadi Dibatalkan, tidak dihapus permanen).

### 10.8 Produk

* Daftar produk dengan pencarian dan filter kategori.
* Tambah, ubah, hapus produk (ada konfirmasi sebelum menghapus).
* Isian produk: nama, kategori, harga, foto (**tidak wajib**), deskripsi singkat (opsional), varian (opsional), stok (opsional, bisa dinyalakan per produk).
* Kelola kategori: tambah, ubah, hapus.

### 10.9 Laporan

* Pilihan periode: **Hari ini · Minggu ini · Bulan ini · Pilih tanggal**.
* Kartu ringkasan: total omzet, jumlah transaksi, rata-rata per transaksi.
* Grafik sederhana omzet per hari.
* Produk terlaris.
* Rincian omzet per metode pembayaran.
* Transaksi yang dibatalkan tidak dihitung.

### 10.10 Pengaturan

* **Profil usaha**: nama usaha, alamat, nomor telepon, logo.
* **Pajak**: aktif/nonaktif dan persentase.
* **Tipe pesanan**: aktif/nonaktif (Makan di tempat, Bawa pulang).
* **Template struk**: pemilik bisa mengaktifkan atau menonaktifkan bagian struk, yaitu logo, nama usaha, alamat, nomor telepon, catatan, dan pesan penutup (teks pesan penutup bisa diubah). Ada pratinjau struk.
* **Keluar (logout)** dengan konfirmasi.

## 11\. Alur Transaksi

1. Setelah aplikasi dibuka, halaman **Kasir** langsung tampil (dari menu lain bisa kembali lewat sidebar).
2. Pilih produk lalu tambahkan ke keranjang.
3. Buka **Keranjang**, cek pesanan, pilih tipe pesanan.
4. Tekan **Bayar**, pilih metode, isi uang diterima bila tunai.
5. Tekan **Selesaikan Transaksi**, struk tampil, data tersimpan.
6. Pemilik membuka **Laporan** kapan saja untuk melihat omzet.

## 12\. Struktur Data (Firestore)

Semua data berada di bawah akun yang login, jadi satu akun bersama otomatis berbagi data yang sama.

```
users/{uid}
  ├── namaUsaha, alamat, telepon, logoUrl
  ├── pajakAktif, pajakPersen
  ├── tipePesananAktif (makanDiTempat, bawaPulang)
  ├── strukTemplate
  │     └── tampilLogo, tampilNamaUsaha, tampilAlamat, tampilTelepon,
  │         tampilCatatan, pesanPenutup
  ├── categories/{id}
  │     └── nama, urutan
  ├── products/{id}
  │     └── nama, harga, kategoriId, fotoUrl (opsional), deskripsi,
  │         varian\[ {nama, tambahanHarga} ] (opsional),
  │         pakaiStok (true/false), stok (angka, opsional), aktif
  └── transactions/{id}
        └── nomor (contoh TRX-20261111-001), waktu, tipePesanan,
            items\[ {produkId, nama, harga, varian, jumlah} ],
            subtotal, pajak, total,
            metodeBayar, uangDiterima, kembalian,
            catatan, status (selesai / dibatalkan)
```

Catatan: nama dan harga produk disalin ke dalam transaksi, supaya riwayat tidak berubah ketika harga produk diubah di kemudian hari.

## 13\. Aturan Tampilan dan Bahasa (User Friendly)

* Semua teks memakai **Bahasa Indonesia** yang sederhana dan ramah.
* Harga selalu berformat Rupiah: `Rp15.000`.
* Tanggal berformat Indonesia: `11 November 2026`.
* Tombol besar dan jarak sentuh nyaman (minimal 48dp).
* Setiap halaman kosong punya pesan dan ajakan, contoh: "Belum ada produk. Tambahkan produk pertamamu."
* Konfirmasi sebelum tindakan berisiko (hapus, batalkan transaksi, keluar).
* Tampilkan indikator memuat dan pesan jelas bila internet bermasalah.
* Warna dan teks harus tetap terbaca jelas.

## 14\. Di Luar Cakupan Versi 1

* Diskon
* Nomor meja
* Peran pengguna (pemilik/karyawan) dan PIN
* Scan barcode
* Cetak struk ke printer Bluetooth
* Integrasi pembayaran QRIS otomatis
* Banyak cabang
* Manajemen stok bahan baku
* Versi iOS

## 15\. Tahapan Pengerjaan

1. **Persiapan**: folder, Firebase, Android Studio, referensi.
2. **Kerangka aplikasi**: splash screen, sidebar yang bisa dibuka/ditutup, layout responsif.
3. **Login dan daftar akun** (termasuk login otomatis dan logout).
4. **Produk dan kategori**.
5. **Kasir, keranjang, dan pembayaran**.
6. **Riwayat dan laporan**.
7. **Pengaturan dan template struk**.
8. **Perapian dan pengujian** di HP dan tablet berbagai ukuran.

## 16\. Kriteria Selesai

* Karyawan baru bisa menyelesaikan satu transaksi tunai tanpa bantuan.
* Setelah login, menutup dan membuka aplikasi lagi langsung masuk halaman Kasir tanpa login ulang.
* Data transaksi tetap tersimpan setelah aplikasi ditutup dan dibuka lagi.
* Angka di Laporan cocok dengan jumlah transaksi di Riwayat.
* Sidebar di HP awalnya tertutup dan bisa dibuka/ditutup lewat tombol; di tablet atau layar lebar awalnya terbuka dan bisa ditutup.
* Tampilan rapi di HP kecil, HP biasa, tablet 7 inci, dan tablet 10 inci, tegak maupun mendatar.
* Pemilik bisa mengaktifkan/menonaktifkan bagian struk dan perubahannya tampil di struk.
* Seluruh teks tampil dalam Bahasa Indonesia.
* Tampilan konsisten dengan referensi (warna, font, ikon, gaya kartu).

## 17\. Pertanyaan Terbuka

* Apakah splash screen cukup menampilkan ucapan yang sama untuk semua, atau menyebut nama usaha saat sudah login?
jawab : iyaa nnti sebut nama usahanya jika sudah membuat nama usaha

