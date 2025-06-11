package com.example.diarydepresiku

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters // **PENTING: Import TypeConverters**

/**
 * DiaryDatabase: Kelas Room Database untuk aplikasi.
 * Ini adalah titik akses utama ke data persisten aplikasi.
 *
 * @param entities Daftar kelas entitas yang akan disimpan dalam database.
 * @param version Versi skema database. Harus ditingkatkan saat skema berubah.
 * @param exportSchema Jika true, akan mengekspor skema ke folder file. Penting untuk produksi.
 * @param typeConverters Mendaftarkan kelas TypeConverter untuk tipe data kustom.
 */
@Database(
    entities = [DiaryEntry::class],
    version = 1, // Pastikan versi ini sesuai dengan versi database Anda
    exportSchema = false // Atur ke true untuk produksi dan periksa file skema
)
@TypeConverters(Converters::class) // **PENTING: Daftarkan kelas TypeConverter di sini**
abstract class DiaryDatabase : RoomDatabase() {

    // DAO (Data Access Object) untuk interaksi dengan entitas DiaryEntry
    abstract fun diaryDao(): DiaryDao

    companion object {
        @Volatile // Memastikan variabel ini selalu up-to-date di semua thread
        private var INSTANCE: DiaryDatabase? = null

        /**
         * Mendapatkan instance tunggal (singleton) dari DiaryDatabase.
         * Membuat database jika belum ada, atau mengembalikan instance yang sudah ada.
         * Menggunakan synchronized untuk memastikan keamanan thread.
         *
         * @param context Konteks aplikasi yang diperlukan untuk membangun database.
         * @return Instance dari DiaryDatabase.
         */
        fun getDatabase(context: Context): DiaryDatabase {
            // Jika instance sudah ada, kembalikan saja
            return INSTANCE ?: synchronized(this) {
                // Jika belum ada, bangun database dalam blok synchronized
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Gunakan applicationContext untuk mencegah memory leaks
                    DiaryDatabase::class.java,
                    "diary_db" // Nama file database lokal
                )
                    // Strategi fallback untuk migrasi yang merusak (HANYA UNTUK PENGEMBANGAN!)
                    // Akan menghapus dan membuat ulang database saat ada perubahan skema.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance // Simpan instance yang baru dibuat
                instance // Kembalikan instance
            }
        }
    }
}