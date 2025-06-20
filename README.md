# Diary Depresiku

**Diary Depresiku** adalah aplikasi Android yang dirancang untuk membantu pengguna memantau suasana hati dan kesehatan mental melalui pencatatan harian dan analisis berbasis kecerdasan buatan (AI). Aplikasi ini mengintegrasikan data emosi, aktivitas fisik, dan rekomendasi konten secara personal, dengan fokus pada privasi, kemudahan penggunaan, dan pendekatan berbasis bukti.

## Fitur Utama

### Minimum Viable Product (MVP)

- **Pencatatan Emosi Harian**  
  Pengguna dapat menulis catatan bebas serta memilih mood melalui skala atau emotikon. Formulir opsional seperti aktivitas harian dan tingkat stres juga tersedia.

- **Pelacakan Aktivitas dan Lokasi**  
  Aktivitas fisik dan lokasi pengguna tercatat secara otomatis (dengan izin), memberi konteks pada kondisi emosional harian.

- **Integrasi Data Kesehatan**  
  Dukungan terhadap Google Fit memungkinkan pengambilan data langkah, detak jantung, dan aktivitas lainnya secara aman.

- **Analisis Emosi Berbasis AI**  
  Catatan pengguna dianalisis menggunakan model NLP untuk mengidentifikasi pola emosi dan menyarankan konten yang sesuai.

- **Rekomendasi Konten Personal**  
  Berdasarkan hasil analisis, sistem menyarankan artikel, latihan relaksasi, atau tips kesehatan mental secara relevan.

- **Sinkronisasi dan Mode Offline**  
  Data dicatat secara lokal dan disinkronkan otomatis ke server saat koneksi tersedia.

- **Antarmuka Minimalis**  
  UI dirancang sederhana, bersih, dan menenangkan, menggunakan palet warna pastel dan struktur navigasi intuitif.

## Rencana Pengembangan Lanjutan

- Fitur konsultasi daring dengan psikolog
- Analisis lanjutan (grafik mood, insight perilaku)
- Dukungan wearable (Wear OS, sleep tracking)
- Sistem rekomendasi berbasis pembelajaran mesin
- Keamanan lanjutan (enkripsi end-to-end, autentikasi biometrik)
- Versi iOS dan platform lintas perangkat

## Arsitektur dan Teknologi

### Client-Side (Android)
- **Bahasa**: Kotlin
- **Framework**: Jetpack Compose (menggunakan Google Fonts untuk tipografi)
- **Penyimpanan Lokal**: Room (SQLite)
- **API Kesehatan**: Google Fit API
- **Networking**: Retrofit
- **Analisis AI Lokal (opsional)**: TensorFlow Lite

### Server-Side (Backend)
- **Bahasa**: Python (FastAPI)
- **Database**: PostgreSQL / MongoDB
- **AI Processing**: OpenAI GPT API
- **Authentication**: OAuth 2.0 / JWT
- **Deployment**: AWS / GCP / Render

## Alur Pengguna

1. Login atau registrasi akun.
2. Menulis catatan harian beserta detail mood dan aktivitas.
3. Data disimpan dan dianalisis oleh server.
4. Pengguna menerima insight atau rekomendasi konten.
5. Riwayat jurnal dapat diakses dan ditinjau kembali.
6. Notifikasi pengingat dikirim setiap hari secara terjadwal.

## Desain UI/UX

- **Warna**: Biru muda, hijau mint, dan oranye pastel sebagai aksen.
- **Font**: Roboto dari Google Fonts melalui Jetpack Compose.
- **Struktur Navigasi**: Bottom tab bar (Diary, Riwayat, Profil).
- **Fokus Desain**: Kesederhanaan, keterbacaan, dan kenyamanan visual.

## Pengujian dan Validasi

- **Unit Testing**: Fungsi aplikasi diuji secara modular.
- **Instrumented UI Testing**: Menggunakan Espresso dan Automator.
- **Beta Testing Tertutup**: Uji nyata dengan pengguna terbatas.
- **Audit Keamanan**: Uji penetrasi dasar dan pengujian enkripsi data.
- **Validasi Klinis**: Kolaborasi dengan profesional psikologi untuk memastikan intervensi aman dan sesuai.

## Roadmap Pengembangan

### Tahun Pertama
- Q1: Riset dan desain UI/UX
- Q2: Implementasi fitur inti dan backend
- Q3: Pengujian beta, penyempurnaan, dan validasi AI
- Q4: Peluncuran terbatas (closed/open beta)

### Tahun Kedua
- Pengembangan fitur lanjutan
- Pembentukan tim pengembang dan psikolog
- Strategi komersialisasi (freemium, B2B, hibah)
- Ekspansi ke platform lain dan penguatan infrastruktur

## Privasi dan Keamanan

- Enkripsi data lokal dan server
- Penyimpanan token secara aman
- Mekanisme opt-in untuk semua akses sensor
- Kebijakan privasi dan persetujuan pengguna ditampilkan dengan transparan

## Instalasi untuk Pengembang

```bash
git clone https://github.com/yourusername/diary-depresiku.git
cd diary-depresiku
./gradlew installDebug
cd app/backend_api
pip install -r requirements.txt
```

File `requirements.txt` menyertakan paket `openai` yang digunakan helper
`get_openrouter_client()` untuk terhubung ke layanan OpenRouter.

### Environment Variables

Backend membutuhkan `OPENROUTER_API_KEY` untuk autentikasi ketika
`get_openrouter_client()` memanggil API. Simpan variabel ini di file
`.env` lalu muat sebelum menjalankan server:

```bash
echo "OPENROUTER_API_KEY=your-openrouter-api-key" > .env
set -a && source .env && set +a
uvicorn app.main:app --reload
```

Setelah backend siap, jalankan `pytest` untuk memverifikasi fungsionalitas API.

## Konfigurasi Build

Secara default aplikasi menggunakan URL `http://10.0.2.2:8000/` untuk mengakses
backend. Nilai ini didefinisikan di `app/build.gradle.kts` melalui `buildConfigField`:

```kotlin
defaultConfig {
    buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000/\"")
}
```

Anda bisa menimpa nilai tersebut pada setiap `buildType` misalnya:

```kotlin
buildTypes {
    debug {
        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000/\"")
    }
    release {
        buildConfigField("String", "BASE_URL", "\"https://api.example.com/\"")
    }
}
```

Gunakan `BuildConfig.BASE_URL` di kode Kotlin untuk memperoleh URL yang sesuai
dengan jenis build.

### Kunci API

Simpan kunci API eksternal di file `local.properties` pada direktori proyek.
Tambahkan entri berikut:

```properties
NEWS_API_KEY=your-news-api-key
OPENROUTER_API_KEY=your-openrouter-api-key
```

Gradle akan membaca nilai tersebut melalui fungsi `secret("OPENROUTER_API_KEY")`
di `app/build.gradle.kts` dan meneruskannya ke `buildConfigField`.

File `local.properties` sudah ada di `.gitignore`, sehingga kunci rahasia tidak
terikut saat commit.

## Deployment di Render

Blueprint `render.yaml` tidak menyertakan kunci API. Tambahkan `OPENROUTER_API_KEY` pada menu **Environment** sebelum melakukan deploy.
## Kontribusi
Kami menyambut kontribusi dari komunitas. Silakan fork repositori ini, buat branch baru, dan kirim pull request. Pedoman kontribusi tersedia di CONTRIBUTING.md.

## Lisensi
Proyek ini dirilis di bawah MIT License.

## Kontak
Pengembang utama:
dr. Tan
Email: tanerizawa(at)gmail.com
GitHub: https://github.com/tanerizawa

