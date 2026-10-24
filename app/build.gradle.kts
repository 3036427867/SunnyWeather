plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.sunnyweather.android"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.sunnyweather.android"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures{
        viewBinding=true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.11.0")
    implementation("com.google.android.material:material:1.14.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0")
    implementation("com.squareup.retrofit2:retrofit:2.6.1")//
    implementation("com.squareup.retrofit2:converter-gson:2.6.1")//
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")//
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")//
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}