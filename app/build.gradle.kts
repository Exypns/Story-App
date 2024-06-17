plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp") version "1.9.22-1.0.17"
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin)
}

android {
    namespace = "com.sulthan.storyapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sulthan.storyapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://story-api.dicoding.dev/v1/\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://story-api.dicoding.dev/v1/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    testOptions {
        animationsDisabled = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")

    //    Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

//    Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

//    Room
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.play.services.maps)
    ksp("androidx.room:room-compiler:2.6.1")

//    Datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

//    Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.exifinterface:exifinterface:1.3.7")

//    Paging
    implementation(libs.androidx.paging.runtime.ktx)

    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation(libs.androidx.room.paging)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.core.testing) // InstantTaskExecutorRule
    testImplementation(libs.kotlinx.coroutines.test) //TestDispatcher
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)

    androidTestImplementation(libs.androidx.core.testing) //InstantTaskExecutorRule
    androidTestImplementation(libs.kotlinx.coroutines.test) //TestDispatcher
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.espresso.intents) //IntentsTestRule

    androidTestImplementation(libs.mockwebserver)
    androidTestImplementation(libs.okhttp3.okhttp.tls)
}