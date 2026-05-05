plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.bankpick"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bankpick"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = false
        dataBinding = false
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.viewpager2)
    implementation(libs.recyclerview)
    implementation(libs.cardview)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    // CircleImageView
    implementation(libs.circleimageview)

    // Glide
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // MPAndroidChart
    implementation(libs.mpandroidchart)
}
