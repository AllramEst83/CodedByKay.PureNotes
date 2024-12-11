import java.io.FileInputStream
import java.util.Properties

// Plugins: Required for Android, Kotlin, Jetpack Compose, and KSP
plugins {
    alias(libs.plugins.android.application) // Android Application plugin
    alias(libs.plugins.kotlin.android)      // Kotlin Android plugin
    alias(libs.plugins.kotlin.compose)      // Kotlin Compose plugin
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" // Kotlin Symbol Processing for Room
}

android {
    namespace = "com.codedbykay.purenotes" // Application namespace
    compileSdk = 34                      // Targeted Android SDK

    defaultConfig {
        applicationId = "com.codedbykay.purenotes" // Unique application ID
        minSdk = 33                                 // Minimum SDK version supported
        targetSdk = 34                              // Target SDK version
        versionCode = 2                             // Application version code
        versionName = "1.0.1"                         // Application version name

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner" // Runner for instrumentation tests
    }
    signingConfigs {
        create("release") {
            val keystoreProperties = Properties()
            val keystorePropertiesFile = rootProject.file("local.properties")

            if (keystorePropertiesFile.exists()) {
                // Load properties from local.properties
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            } else {
                // Load properties from environment variables
                keystoreProperties["KEYSTORE_PATH"] = System.getenv("KEYSTORE_DECODING_PATH")
                keystoreProperties["KEYSTORE_PASSWORD"] = System.getenv("KEYSTORE_PASSWORD")
                keystoreProperties["KEY_ALIAS"] = System.getenv("KEY_ALIAS")
                keystoreProperties["KEY_PASSWORD"] = System.getenv("KEY_PASSWORD")
            }

            // Assign signing properties
            storeFile = file(keystoreProperties["KEYSTORE_PATH"] as String)
            storePassword = keystoreProperties["KEYSTORE_PASSWORD"] as String
            keyAlias = keystoreProperties["KEY_ALIAS"] as String
            keyPassword = keystoreProperties["KEY_PASSWORD"] as String
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8" // JVM target version for Kotlin
    }

    buildFeatures {
        buildConfig = true // Enables BuildConfig for constants in the build
        compose = true     // Enables Jetpack Compose support
    }

    packaging {
        resources {
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/*.SF"
            excludes += "META-INF/*.DSA"
            excludes += "META-INF/*.RSA"
            excludes += "META-INF/AL2.0"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    // ---------------------------------------------------
    // Core Android Dependencies
    // Essential libraries providing basic Android functionality and integration.
    // ---------------------------------------------------
    implementation(libs.androidx.core.ktx)                        // Android KTX for Kotlin extensions
    implementation(libs.androidx.lifecycle.runtime.ktx)           // Lifecycle runtime with Kotlin extensions
    implementation(libs.androidx.activity.compose)                // Activity support for Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))           // Compose BOM for consistent Compose versions
    implementation("androidx.appcompat:appcompat:1.6.1")           // AppCompat for backward compatibility

    // ---------------------------------------------------
    // Jetpack Compose UI Dependencies
    // Libraries for building modern, declarative UIs using Jetpack Compose.
    // ---------------------------------------------------
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")        // Material 3 components for Compose
    implementation("androidx.compose.runtime:runtime-livedata:1.7.4") // LiveData integration in Compose
    implementation("androidx.compose.material:material-icons-extended") // Extended Material Icons

    // ---------------------------------------------------
    // Lifecycle and Activity Management
    // Tools to handle the lifecycle of components and manage activities/fragments.
    // ---------------------------------------------------
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2") // ViewModel with Kotlin extensions
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")   // Lifecycle runtime with Kotlin extensions
    implementation("androidx.activity:activity-ktx:1.8.1")             // Activity Kotlin extensions
    implementation("androidx.fragment:fragment-ktx:1.6.2")             // Fragment Kotlin extensions

    // ---------------------------------------------------
    // Room Database Dependencies
    // Libraries for local data persistence using Room ORM.
    // ---------------------------------------------------
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")        // Room runtime
    ksp("androidx.room:room-compiler:$roomVersion")                // Room compiler using KSP
    implementation("androidx.room:room-ktx:$roomVersion")            // Room Kotlin extensions

    // ---------------------------------------------------
    // Navigation
    // Libraries to handle in-app navigation within Jetpack Compose.
    // ---------------------------------------------------
    val navVersion = "2.8.3"
    implementation("androidx.navigation:navigation-compose:$navVersion") // Compose Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion") // Navigation testing

    // ---------------------------------------------------
    // DataStore for Preferences
    // Library for storing key-value pairs and preferences.
    // ---------------------------------------------------
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // ---------------------------------------------------
    // UI Enhancements
    // Libraries for additional UI features like image loading and animations.
    // ---------------------------------------------------
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")               // Image loading with Coil
    implementation("com.valentinilk.shimmer:compose-shimmer:1.3.1")     // Shimmer effect for loading states

    // ---------------------------------------------------
    // Performance Monitoring
    // Libraries to monitor and trace app performance.
    // ---------------------------------------------------
    implementation("androidx.tracing:tracing:1.1.0")

    // ---------------------------------------------------
    // Background Work
    // Libraries to handle background tasks and work scheduling.
    // ---------------------------------------------------
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // ---------------------------------------------------
    // AppWidgets Support
    // Libraries to create and manage app widgets.
    // ---------------------------------------------------
    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")            // Material 3 integration for Glance
    implementation("androidx.glance:glance-material:1.1.1")             // Material 2 integration for Glance

    // ---------------------------------------------------
    // Networking and Serialization
    // Libraries for data serialization and network operations.
    // ---------------------------------------------------
    implementation("com.google.code.gson:gson:2.8.8")                  // Gson for JSON serialization

    // ---------------------------------------------------
    // Testing Libraries
    // Frameworks and tools for unit and UI testing.
    // ---------------------------------------------------
    testImplementation(libs.junit)                                       // JUnit for unit testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3") // Coroutines testing utilities
    androidTestImplementation(libs.androidx.junit)                      // AndroidX JUnit for Android tests
    androidTestImplementation(libs.androidx.espresso.core)              // Espresso for UI testing
    androidTestImplementation(platform(libs.androidx.compose.bom))      // Compose BOM for consistent test dependencies
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")     // Compose UI testing
    debugImplementation("androidx.compose.ui:ui-tooling")              // Compose UI tooling for debugging
    debugImplementation("androidx.compose.ui:ui-test-manifest")        // Compose UI test manifest
}
