package com.example.diarydepresiku

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters // **PENTING: Import TypeConverters**
import com.example.diarydepresiku.Converters
import com.example.diarydepresiku.content.EducationalArticleEntity

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
    entities = [DiaryEntry::class, EducationalArticleEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class) // **PENTING: Daftarkan kelas TypeConverter di sini**
abstract class DiaryDatabase : RoomDatabase() {

    // DAO (Data Access Object) untuk interaksi dengan entitas DiaryEntry
    abstract fun diaryDao(): DiaryDao

    // DAO untuk artikel edukasi yang disimpan secara lokal
    abstract fun educationalArticleDao(): com.example.diarydepresiku.content.EducationalArticleDao

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
