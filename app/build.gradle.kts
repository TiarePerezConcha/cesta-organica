plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)


    // Agregar esto para habilitar KAPT

    kotlin("kapt")

}

android {
    namespace = "com.example.cestaOganicaIA"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cestaOganicaIA"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Dependencia para la navegación con Jetpack Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")


    // Íconos (core opcional) y EXTENDIDOS (¡este es el clave!)
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")


    // Dependencias Room
    implementation("androidx.room:room-runtime:2.6.1")  // Versión actualizada
    kapt("androidx.room:room-compiler:2.6.1")          // Misma versión
    implementation("androidx.room:room-ktx:2.6.1")     // Misma versión

// implementation(libs.androidx.compose.runtime.livedata)
// Option 1: If you add it to libs.versions.toml
//implementation(libs.androidx.compose.runtime.livedata)
// OR Option 2: Direct notation
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")
// CameraX
    val camerax_version = "1.3.3"
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")

// ZXing para leer QR
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
// ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}