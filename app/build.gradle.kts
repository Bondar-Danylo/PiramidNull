plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.piramidnull"
    compileSdk = 35

    packagingOptions {
        resources {
            excludes += setOf(
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES"
            )
        }
    }

    defaultConfig {
        applicationId = "com.example.piramidnull"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add BuildConfig field here (Kotlin DSL syntax)
        buildConfigField("String", "GOOGLE_CLOUD_API_KEY", "\"${project.findProperty("GOOGLE_CLOUD_API_KEY") ?: "AIzaSyAoVTX1JkuGfBdO_MB3Y1MS8uTaVQxkAhs"}\"")
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

    // Enable BuildConfig generation
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.auth0.android:jwtdecode:2.0.1")
    implementation ("com.auth0:java-jwt:4.2.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.22.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.google.cloud:google-cloud-texttospeech:2.37.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity:1.9.3")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}