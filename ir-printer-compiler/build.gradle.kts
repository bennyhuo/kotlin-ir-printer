import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm")
    kotlin("kapt")
    id("com.github.gmazzo.buildconfig")
    id("com.gradleup.shadow")
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    compileOnly("org.jetbrains.kotlin:kotlin-native-compiler-embeddable")

    implementation("com.bennyhuo.kotlin:kotlin-source-printer:2.3.0-1.3.2") {
        exclude("org.jetbrains.kotlin")
    }

    kapt("com.google.auto.service:auto-service:1.0.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")

    testImplementation(kotlin("test-junit"))
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation("com.bennyhuo.kotlin:kotlin-compile-testing-extensions:2.3.0-1.3.2")
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

tasks.named<ShadowJar>("shadowJar") {
    // 建议进行 relocate，防止与其他插件冲突
    relocate("com.bennyhuo.sourceprinter", "com.bennyhuo.kotlin.ir.printer.internal.sourceprinter")
    archiveClassifier.set("") // 覆盖默认 JAR
}

tasks.jar {
    actions = mutableListOf()
    dependsOn("shadowJar")
}