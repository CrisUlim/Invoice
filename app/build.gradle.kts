plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.invoice_2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.invoice_2"
        minSdk = 34
        targetSdk = 35
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("com.itextpdf:kernel:7.2.5")
    implementation("com.itextpdf:io:7.2.5")
    implementation("com.itextpdf:layout:7.2.5")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}