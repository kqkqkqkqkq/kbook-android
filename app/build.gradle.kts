import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.protobuf")
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization") version "2.2.0"
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("org.jetbrains.kotlinx.kover") version "0.9.8"
}

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
//    sourceSets {
//        getByName("main") {
//            java.srcDirs(
//                "src/main/java",
//                "src/main/kotlin",
//                "build/generated/source/proto/debug/java",
//                "build/generated/source/proto/debug/grpc",
//                "build/generated/source/proto/debug/grpcKt",
//                "build/generated/source/proto/debug/kotlin",
//                "build/generated/ksp/debug/java"
//            )
//            kotlin.srcDirs("src/main/java")
//        }
//    }
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

val aspectJVersion = "1.9.21"

val agent: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = true
}

kover {
    reports {
        total {
            html {
                onCheck = true
                htmlDir = file("build/reports/kover/html")
            }
        }
    }
}

tasks.withType<Test> {
    val agentJar = configurations.getByName("agent").asPath

    jvmArgs = listOf(
//        "-javaagent:${agentJar}",
        "-XX:+EnableDynamicAgentLoading",
        "-Djdk.instrument.traceUsage=false"
    )

    systemProperty("allure.results.directory", "${project.buildDir}/allure-results")
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
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.compose.material.icons)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)

    // gRPC
    implementation("io.grpc:grpc-protobuf:1.80.0")
    implementation("io.grpc:grpc-stub:1.80.0")
    implementation("io.grpc:grpc-okhttp:1.80.0")
    implementation("io.grpc:grpc-kotlin-stub:1.5.0")
    implementation("com.google.protobuf:protobuf-kotlin:4.31.1")
    implementation("com.google.protobuf:protobuf-java:4.31.1")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.7")
    implementation(libs.kotlinx.serialization.json)

    // Coil
    implementation("io.coil-kt.coil3:coil-compose:3.4.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.4.0")

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Allure
    testImplementation(platform("io.qameta.allure:allure-bom:2.25.0"))
    testImplementation("io.qameta.allure:allure-junit4")
//    agent("org.aspectj:aspectjweaver:${aspectJVersion}")
//    testImplementation("io.qameta.allure:allure-junit4-aspect")

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    val kaspresso = "1.6.1"
    androidTestImplementation("com.kaspersky.android-components:kaspresso:$kaspresso")
    androidTestImplementation("com.kaspersky.android-components:kaspresso-allure-support:$kaspresso")
    androidTestImplementation("com.kaspersky.android-components:kaspresso-compose-support:$kaspresso")
}
