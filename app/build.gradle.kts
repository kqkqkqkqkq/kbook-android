import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.protobuf")
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization") version "2.0.21"
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
        "-javaagent:${agentJar}",
        "-XX:+EnableDynamicAgentLoading",
        "-Djdk.instrument.traceUsage=false"
    )

    systemProperty("allure.results.directory", "${project.buildDir}/allure-results")

    doFirst {
        println("Running tests with Allure agent: $agentJar")
        println("Allure results directory: ${project.buildDir}/allure-results")
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
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
    implementation(libs.compose.material.icons)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")

    // gRPC
    implementation("io.grpc:grpc-protobuf:1.80.0")
    implementation("io.grpc:grpc-stub:1.80.0")
    implementation("io.grpc:grpc-okhttp:1.80.0")
    implementation("io.grpc:grpc-kotlin-stub:1.5.0")
    implementation("com.google.protobuf:protobuf-kotlin:4.31.1")
    implementation("com.google.protobuf:protobuf-java:4.31.1")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Coil
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt.coil3:coil-compose:3.4.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.4.0")

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    // Allure
    testImplementation(platform("io.qameta.allure:allure-bom:2.25.0"))
    testImplementation("io.qameta.allure:allure-junit4")
    agent("org.aspectj:aspectjweaver:${aspectJVersion}")
    testImplementation("io.qameta.allure:allure-junit4-aspect")

//    testImplementation("org.junit.platform:junit-platform-launcher:1.11.4")
//    testImplementation("org.junit.platform:junit-platform-engine:1.11.4")

    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

}
