plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    namespace = "com.example.prm392_final_prj"
    compileSdk = 36
    packaging {
        resources {
            excludes.add("META-INF/LICENSE.md") // Keep this from the last fix
            excludes.add("META-INF/NOTICE.md")  // Add this line for the new error
        }
    }

    defaultConfig {
        applicationId = "com.example.prm392_final_prj"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"



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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {

        implementation(libs.appcompat)
        implementation(libs.material)
        implementation("com.sun.mail:android-mail:1.6.7")
        implementation("com.sun.mail:android-activation:1.6.7")
        implementation("androidx.room:room-runtime:2.4.3")
        annotationProcessor("androidx.room:room-compiler:2.4.3")
        implementation("com.google.android.gms:play-services-location:17.1.0")
        implementation("com.google.android.gms:play-services-maps:19.0.9")
        implementation("com.google.android.gms:play-services-places:17.1.0")
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
    }
}
dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.play.services.maps)
}
