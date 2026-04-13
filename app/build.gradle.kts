import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
//    alias(libs.plugins.protobuf)
    id("com.google.protobuf")
    alias(libs.plugins.kotlin.android)
}

//kotlin { jvmToolchain(21) }

android {
    namespace = "ru.k.kbook"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.k.kbook"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
    sourceSets {
        getByName("main") {
            java.srcDirs(
                "build/generated/source/proto/debug/java",
                "build/generated/source/proto/debug/grpc",
                "build/generated/source/proto/debug/grpcKt",
                "build/generated/source/proto/debug/kotlin"
            )
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.31.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.80.0"
        }
        create("grpcKt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.5.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("java")
                create("kotlin")
            }
            it.plugins {
                id("grpc") {
                    option("@generated=omit")
                }
                id("grpcKt")
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // gRPC
    implementation("io.grpc:grpc-protobuf:1.80.0")
    implementation("io.grpc:grpc-stub:1.80.0")
    implementation("io.grpc:grpc-okhttp:1.80.0")
    implementation("io.grpc:grpc-kotlin-stub:1.5.0")
    implementation("com.google.protobuf:protobuf-kotlin:4.31.1")
    implementation("com.google.protobuf:protobuf-java:4.31.1")
//    implementation("com.google.protobuf:protobuf-java-lite:3.19.2")
//    implementation("io.grpc:grpc-protobuf-lite:1.80.0")
//    implementation("io.grpc:grpc-stub:1.80.0")
}
