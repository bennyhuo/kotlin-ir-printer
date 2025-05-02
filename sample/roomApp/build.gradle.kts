import com.bennyhuo.kotlin.ir.printer.gradle.OutputType

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    id("com.bennyhuo.kotlin.ir.printer")
}

irPrinter {
    isEnabled = true
    isOptimizedKotlinIrEnabled = true
    indent = "    "
    outputType = OutputType.KOTLIN_LIKE_JETPACK_COMPOSE_STYLE
}

android {
    namespace = "com.bennyhuo.kotlin.printer.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bennyhuo.kotlin.printer.sample"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
}