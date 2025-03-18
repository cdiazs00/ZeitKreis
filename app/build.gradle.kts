import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}

val dbUrl: String = System.getenv("DB_URL") ?: localProperties.getProperty("DB_URL", "")
val dbUser: String = System.getenv("DB_USER") ?: localProperties.getProperty("DB_USER", "")
val dbPassword: String = System.getenv("DB_PASSWORD") ?: localProperties.getProperty("DB_PASSWORD", "")

android {
    namespace = "com.example.zeitkreis"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.zeitkreis"
        minSdk = 26
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders.putAll(
            mapOf(
                "DB_URL" to dbUrl,
                "DB_USER" to dbUser,
                "DB_PASSWORD" to dbPassword
            )
        )
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
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    implementation(libs.activity)
    implementation(libs.appcompat)
    implementation(libs.converter.gson)
    implementation(libs.constraintlayout)
    implementation(libs.jakarta.persistence.api)
    implementation(libs.logging.interceptor)
    implementation(libs.material)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.postgresql)
    implementation(libs.retrofit)
    testImplementation(libs.junit)
}