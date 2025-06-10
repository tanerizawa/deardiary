package com.example.diarydepresiku

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.google.gson.annotations.SerializedName // Penting untuk mapping JSON

/**
 * Data class untuk body permintaan POST entri diary ke server.
 * Properti dan tipe data harus cocok dengan yang diharapkan oleh API backend.
 * Menggunakan @SerializedName untuk pemetaan JSON yang eksplisit.
 */
data class DiaryEntryRequest(
    @SerializedName("content") val content: String, // Sesuaikan dengan nama properti di backend
    @SerializedName("mood") val mood: String,
    @SerializedName("timestamp") val timestamp: Long // Jika backend mengharapkan Unix timestamp (Long)
)

/**
 * Data class untuk respons dari server setelah menyimpan entri.
 * Properti dan tipe data harus cocok dengan format respons dari API backend.
 */
data class DiaryEntryResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("content") val content: String, // Sesuaikan dengan nama properti di backend
    @SerializedName("mood") val mood: String,
    @SerializedName("timestamp") val timestamp: Long, // Jika backend mengembalikan Unix timestamp (Long)
    @SerializedName("message") val message: String? = null // Contoh properti opsional dari server
)

/**
 * DiaryApi: Interface Retrofit untuk berkomunikasi dengan backend FastAPI.
 * Mendefinisikan semua endpoint API yang akan digunakan aplikasi.
 */
interface DiaryApi {
    /**
     * Endpoint untuk mengirim entri diary baru ke server.
     * Menggunakan anotasi @POST untuk permintaan HTTP POST.
     * @param entry Objek DiaryEntryRequest yang akan dikirim sebagai body permintaan.
     * @return Objek Response dari Retrofit yang membungkus DiaryEntryResponse.
     */
    @POST("entries/") // Pastikan ini adalah endpoint yang benar di server FastAPI Anda.
    // Saya menambahkan trailing slash '/' karena umum di REST API.
    suspend fun postEntry(@Body entry: DiaryEntryRequest): Response<DiaryEntryResponse>

    // (Opsional) Endpoint lain dapat didefinisikan di sini, misalnya:
    // @GET("entries/")
    // suspend fun getAllEntries(): Response<List<DiaryEntryResponse>>

    // @GET("entries/{id}")
    // suspend fun getEntryById(@Path("id") id: Int): Response<DiaryEntryResponse>

    // @PUT("entries/{id}")
    // suspend fun updateEntry(@Path("id") id: Int, @Body entry: DiaryEntryRequest): Response<DiaryEntryResponse>

    // @DELETE("entries/{id}")
    // suspend fun deleteEntry(@Path("id") id: Int): Response<Response<Unit>> // Atau Response<Void>
}