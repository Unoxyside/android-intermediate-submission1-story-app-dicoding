plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin)
}

android {
    namespace = "com.bahasyim.mystoryapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bahasyim.mystoryapp"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)



    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    //gson converter
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //okhttp3
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    //lifecycle (untuk lifecycleScope)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    //glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    //datastore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    //splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")
    //paging
    implementation("androidx.paging:paging-runtime-ktx:3.3.0")
    implementation("androidx.room:room-paging:2.4.0-rc01")
    //room
    implementation("androidx.room:room-ktx:2.4.0-rc01")
    ksp("androidx.room:room-compiler:2.4.0-rc01")
    //maps
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.2.0")

    //testing
    testImplementation("androidx.arch.core:core-testing:2.2.0") // InstantTaskExecutorRule
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0") // InstantTaskExecutorRule
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1") //TestDispatcher
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1") //TestDispatcher
    testImplementation("org.mockito:mockito-core:5.6.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")

}