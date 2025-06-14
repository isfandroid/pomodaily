plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.dagger.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.sqldelight)
}

android {
    namespace = "com.isfandroid.pomodaily"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.isfandroid.pomodaily"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.isfandroid.pomodaily")
        }
    }
}

dependencies {

    // CORE
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.serialization.json)

    // UI
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.lifecycle.service)

    // DI
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    // DATA - DATASTORE
    implementation(libs.androidx.datastore)

    // DATA - SQLDelight
    implementation(libs.sqldelight.driver)
    implementation(libs.sqldelight.coroutines)

    // WORKER
    implementation(libs.androidx.work)
    implementation(libs.androidx.hilt.work)

    // FIREBASE
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    // TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}