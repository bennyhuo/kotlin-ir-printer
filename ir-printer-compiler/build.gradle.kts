import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    kotlin("kapt")
    id("com.github.gmazzo.buildconfig")
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    compileOnly("org.jetbrains.kotlin:kotlin-native-compiler-embeddable")

    kapt("com.google.auto.service:auto-service:1.0.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")

    testImplementation(kotlin("test-junit"))
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation("com.bennyhuo.kotlin:kotlin-compile-testing-extensions:2.2.0-1.3.0")
}

kotlin {
    jvmToolchain(8)
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xmulti-dollar-interpolation"
        )
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

buildConfig {
    packageName("$group.ir.printer")
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${project.property("KOTLIN_PLUGIN_ID")}\"")
}
