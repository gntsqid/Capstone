plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.example.capstone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.capstone"
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Mapbox dependencies
    implementation("com.mapbox.maps:android:11.9.0") // Mapbox Maps SDK
    implementation("com.mapbox.extension:maps-compose:11.9.0") // Mapbox Compose Extension
    implementation("com.mapbox.search:mapbox-search-android:2.7.0") // Mapbox Search SDK
    implementation("com.mapbox.search:autofill:2.7.0")
    implementation("com.mapbox.search:discover:2.7.0")
    implementation("com.mapbox.search:place-autocomplete:2.7.0")
    implementation("com.mapbox.search:offline:2.7.0")
    implementation("com.mapbox.search:mapbox-search-android-ui:2.7.0")
    implementation("com.mapbox.search:autofill:2.7.0")
    implementation("com.mapbox.search:discover:2.7.0")
    implementation("com.mapbox.search:place-autocomplete:2.7.0")
    implementation("com.mapbox.search:offline:2.7.0")
    implementation("com.mapbox.search:mapbox-search-android:2.7.0")
    implementation("com.mapbox.search:mapbox-search-android-ui:2.7.0")

    // Core Compose libraries
    implementation("androidx.activity:activity-compose:1.8.0") // Provides `setContent`
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // AndroidX and Material dependencies
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")

    // HTTP Request; i.e. API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // Google Play Services for location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Testing libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
