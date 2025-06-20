// app/build.gradle.kts

import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp) // <<< GUNAKAN ALIAS INI jika sudah didefinisikan di libs.versions.toml
}

val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}

fun secret(key: String): String? =
    localProps.getProperty(key) ?: project.findProperty(key) as String?

android {
    namespace = "com.example.diarydepresiku"
    compileSdk = 35 // OK

    defaultConfig {
        applicationId = "com.example.diarydepresiku"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Base URL for backend API
        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000/\"")
        // API keys loaded from local.properties or Gradle properties
        val openRouter = secret("OPENROUTER_API_KEY") ?: ""
        buildConfigField("String", "OPENROUTER_API_KEY", "\"$openRouter\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            manifestPlaceholders["usesCleartextTraffic"] = true
        }
        release {
            manifestPlaceholders["usesCleartextTraffic"] = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion("11")
        targetCompatibility = JavaVersion.toVersion("11")
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        // >>> PENTING: Link ke versi compiler dari libs.versions.toml
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom)) // OK
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    // >>> PENTING: Gunakan referensi libs. untuk google-fonts
    implementation(libs.androidx.ui.text.google.fonts)

    // ---- Tambahan untuk Room Database ----
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler) // Untuk KSP (gunakan alias)
    implementation(libs.androidx.room.ktx)

    // ---- Tambahan untuk Lifecycle/ViewModel ----
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // ---- Tambahan untuk Retrofit (Jaringan) ----
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.play.services.auth)

    // ---- Kotlin Coroutines Core ----
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.datastore.preferences)

    // ---- Dependensi pengujian ----
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("com.google.android.material:material:1.12.0")

}
