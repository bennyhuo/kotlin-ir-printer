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

    kapt("com.google.auto.service:auto-service:1.0.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")

    testImplementation(kotlin("test-junit"))
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation("com.bennyhuo.kotlin:kotlin-compile-testing-extensions:1.9.20-1.3.0")
}

kotlin {
    jvmToolchain(8)
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn")
compileKotlin.kotlinOptions.jvmTarget = "1.8"

buildConfig {
    packageName("$group.ir.printer")
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${project.property("KOTLIN_PLUGIN_ID")}\"")
}
