plugins {
    alias(libs.plugins.android.application)
}

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
    implementation (libs.converter.gson)
    implementation(libs.constraintlayout)
    implementation(libs.jakarta.persistence.api)
    implementation (libs.logging.interceptor)
    implementation(libs.material)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.fragment.v287)
    implementation(libs.navigation.ui)
    implementation(libs.navigation.ui.v287)
    implementation(libs.postgresql)
    implementation(libs.postgresql.v4260)
    implementation (libs.retrofit)
    testImplementation(libs.junit)
}