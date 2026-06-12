plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.kotlinx.kover)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

kover {
    reports {
        filters {
            excludes {
                // Exclude Android entry points and application class
                classes(
                    "com.felixjhonata.trackney.MainActivity",
                    "com.felixjhonata.trackney.TrackneyApplication"
                )
                // Exclude Compose UI views, dependency injection modules, UI theme configs, and DB configuration
                classes(
                    "com.felixjhonata.trackney.ui.theme.*",
                    "com.felixjhonata.trackney.*.view.*",
                    "com.felixjhonata.trackney.shared.model.di.*",
                    "com.felixjhonata.trackney.shared.model.database.TrackneyDatabase"
                )
                // Exclude generated DI (Hilt), DB (Room), and Compose compiler internal classes
                classes(
                    "*_HiltModules*",
                    "*Hilt_*",
                    "*_Factory*",
                    "*_MembersInjector*",
                    "*_Impl*",
                    "*ComposableSingletons*",
                    "dagger.hilt.internal.aggregatedroot.codegen.*",
                    "hilt_aggregated_deps.*"
                )
            }
        }
        verify {
            rule {
                minBound(95)
            }
        }
    }
}

android {
    namespace = "com.felixjhonata.trackney"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.felixjhonata.trackney"
        minSdk = 26
        targetSdk = 36
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
    buildFeatures {
        compose = true
    }
}

ksp {
    arg("room.schemaLocation", "${rootProject.projectDir}/docs/schemas")
}

dependencies {
    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.android.compiler)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}