plugins {
    id("java-gradle-plugin")
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin"))
}

buildConfig {
    val compilerPluginProject = project(":ir-printer-compiler")
    packageName("${compilerPluginProject.group}.ir.printer")
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${property("KOTLIN_PLUGIN_ID")}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${compilerPluginProject.group}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${compilerPluginProject.property("POM_ARTIFACT_ID")}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${compilerPluginProject.version}\"")
}

gradlePlugin {
    plugins {
        create("PrinterGradlePlugin") {
            id = project.properties["KOTLIN_PLUGIN_ID"] as String
            displayName = "Kotlin Printer plugin"
            description = "Kotlin Printer plugin"
            implementationClass = "com.bennyhuo.kotlin.ir.printer.gradle.IrPrinterGradlePlugin"
        }
    }
}

kotlin {
    jvmToolchain(8)
}