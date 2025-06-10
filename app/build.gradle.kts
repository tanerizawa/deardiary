plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" // TAMBAHKAN INI (Versi KSP harus cocok dengan versi Kotlin Anda)
    // Ganti dengan versi terbaru KSP yang sesuai dengan Kotlin Anda
}

android {
    namespace = "com.example.diarydepresiku"
    compileSdk = 35 // OK

    defaultConfig {
        applicationId = "com.example.diarydepresiku"
        minSdk = 24 // OK
        targetSdk = 35 // OK
        versionCode = 1 // OK
        versionName = "1.0" // OK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { // Tambahkan ini jika menggunakan Vector Drawables
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // OK
        targetCompatibility = JavaVersion.VERSION_11 // OK
    }
    kotlinOptions {
        jvmTarget = "11" // OK
    }
    buildFeatures {
        compose = true // OK
        // viewBinding = true // Tambahkan ini jika Anda juga menggunakan View Binding
    }
    composeOptions { // Tambahkan ini untuk konfigurasi Compose
        kotlinCompilerExtensionVersion = "1.5.1" // Pastikan versi ini sesuai dengan versi Kotlin Anda
        // Anda mungkin perlu menyesuaikannya dengan compileSdk dan Kotlin plugin
    }
    packaging { // Konfigurasi untuk mencegah konflik file di APK
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx) // OK
    implementation(libs.androidx.lifecycle.runtime.ktx) // OK
    implementation(libs.androidx.activity.compose) // OK
    implementation(platform(libs.androidx.compose.bom)) // OK
    implementation(libs.androidx.ui) // OK
    implementation(libs.androidx.ui.graphics) // OK
    implementation(libs.androidx.ui.tooling.preview) // OK
    implementation(libs.androidx.material3) // OK

    // ---- Tambahan untuk Room Database ----
    val room_version = "2.6.1" // Gunakan versi Room terbaru yang stabil

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version") // Untuk KAPT (lama)
    // Jika menggunakan KSP (direkomendasikan untuk Kotlin), ganti baris di atas dengan ini:
    ksp("androidx.room:room-compiler:$room_version") // Pastikan Anda juga menambahkan plugin KSP di atas

    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    // ---- Tambahan untuk Lifecycle/ViewModel ----
    val lifecycle_version = "2.7.0" // Gunakan versi Lifecycle terbaru yang stabil

    // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    // ViewModel utilities for Android KTX
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // LiveData (opsional, jika Anda masih menggunakannya)
    // implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    // ---- Tambahan untuk Retrofit (Jaringan) ----
    val retrofit_version = "2.9.0" // Gunakan versi Retrofit terbaru yang stabil
    val okhttp_version = "4.12.0" // Versi OkHttp yang compatible dengan Retrofit

    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version") // Untuk konversi JSON (Gson)
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version") // OkHttp (HTTP client)
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp_version") // Interceptor untuk logging (membantu debugging)

    // ---- Kotlin Coroutines Core (jika belum ada) ----
    // Pastikan Anda sudah punya implementasi coroutines-core dan coroutines-android.
    // Biasanya sudah datang dengan room-ktx atau lifecycle-viewmodel-ktx,
    // tapi tidak ada salahnya memastikan.
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0") // Ganti dengan versi terbaru
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0") // Ganti dengan versi terbaru


    // ---- Dependensi pengujian (sudah ada, hanya peninjauan) ----
    testImplementation(libs.junit) // OK
    androidTestImplementation(libs.androidx.junit) // OK
    androidTestImplementation(libs.androidx.espresso.core) // OK
    androidTestImplementation(platform(libs.androidx.compose.bom)) // OK
    androidTestImplementation(libs.androidx.ui.test.junit4) // OK
    debugImplementation(libs.androidx.ui.tooling) // OK
    debugImplementation(libs.androidx.ui.test.manifest) // OK
}