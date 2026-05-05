import java.util.Properties

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

        // Load properties from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        // Define BuildConfig fields
        buildConfigField("String", "SMTP_HOST", "\"${localProperties.getProperty("SMTP_HOST") ?: "smtp.gmail.com"}\"")
        buildConfigField("String", "SMTP_PORT", "\"${localProperties.getProperty("SMTP_PORT") ?: "587"}\"")
        buildConfigField("String", "SMTP_USER", "\"${localProperties.getProperty("SMTP_USER") ?: ""}\"")
        buildConfigField("String", "SMTP_PASS", "\"${localProperties.getProperty("SMTP_PASS") ?: ""}\"")
        buildConfigField("String", "SMTP_SENDER", "\"${localProperties.getProperty("SMTP_SENDER") ?: ""}\"")
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
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/NOTICE.md"
            excludes += "/META-INF/LICENSE.md"
        }
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
    implementation(libs.firebase.storage)

    // CircleImageView
    implementation(libs.circleimageview)

    // Glide
    implementation(libs.glide)
    implementation(libs.google.firebase.storage)
    annotationProcessor(libs.glide.compiler)

    // MPAndroidChart
    implementation(libs.mpandroidchart)

    // Mail
    implementation(libs.javax.mail)
    implementation(libs.javax.activation)
    // Removed dotenv implementation(libs.dotenv)
}
