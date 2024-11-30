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
        versionCode = 1                             // Application version code
        versionName = "1.0"                         // Application version name

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner" // Runner for instrumentation tests
    }

    buildTypes {
        debug {
            isMinifyEnabled = false // Disables code shrinking for debug builds
        }
        release {
            isMinifyEnabled = false // Disables code shrinking for release builds
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), // Default Proguard rules for Android
                "proguard-rules.pro" // Custom Proguard rules file
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8 // Java compatibility version
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
    // Core dependencies for Android
    implementation(libs.androidx.core.ktx)                        // Android KTX
    implementation(libs.androidx.lifecycle.runtime.ktx)           // Lifecycle runtime with KTX
    implementation(libs.androidx.activity.compose)                // Activity support for Compose
    implementation(platform(libs.androidx.compose.bom))           // Compose BOM for version alignment

    // Jetpack Compose dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")        // Material 3 components
    implementation("androidx.compose.runtime:runtime-livedata:1.7.4") // LiveData support in Compose

    // Testing libraries
    testImplementation(libs.junit)                                // Unit testing framework
    androidTestImplementation(libs.androidx.junit)                // AndroidX JUnit for Android tests
    androidTestImplementation(libs.androidx.espresso.core)        // Espresso UI testing
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Compose BOM for test dependencies

    // Compose UI testing dependencies
    androidTestImplementation("androidx.compose.ui:ui-test-junit4") // UI testing for Compose
    debugImplementation("androidx.compose.ui:ui-tooling")         // Debugging tools for Compose
    debugImplementation("androidx.compose.ui:ui-test-manifest")   // Manifest for UI testing in Compose

    // Lifecycle and Activity dependencies
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2") // ViewModel with KTX
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")   // Lifecycle runtime with KTX
    implementation("androidx.activity:activity-ktx:1.8.1")             // Activity KTX
    implementation("androidx.fragment:fragment-ktx:1.6.2")             // Fragment KTX

    // Room dependencies (for database)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion") // Room runtime
    ksp("androidx.room:room-compiler:$roomVersion")           // Room KSP compiler
    implementation("androidx.room:room-ktx:$roomVersion")     // Room KTX extensions

    // Navigation component for Compose
    val navVersion = "2.8.3"
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // DataStore for preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Material Icons for Compose
    implementation("androidx.compose.material:material-icons-extended")

    // Tracing library for performance monitoring
    implementation("androidx.tracing:tracing:1.1.0")

    implementation("androidx.work:work-runtime-ktx:2.8.1")

    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("com.google.cloud:google-cloud-pubsub:1.134.1")
    implementation("io.grpc:grpc-okhttp:1.68.1") // For gRPC transport
    implementation("io.grpc:grpc-protobuf:1.68.1") // Protobuf serialization for gRPC
    implementation("io.grpc:grpc-stub:1.68.1") // Stub-based gRPC API
    implementation("com.google.auth:google-auth-library-oauth2-http:1.17.0") // Authentication

    // For AppWidgets support
    implementation("androidx.glance:glance-appwidget:1.1.1")

    // For interop APIs with Material 3
    implementation("androidx.glance:glance-material3:1.1.1")

    // For interop APIs with Material 2
    implementation("androidx.glance:glance-material:1.1.1")

    implementation("com.google.code.gson:gson:2.8.8")
}
