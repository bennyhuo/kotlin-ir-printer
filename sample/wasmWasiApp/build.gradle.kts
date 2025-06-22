@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("com.bennyhuo.kotlin.ir.printer")
}

kotlin {
    wasmWasi {
        nodejs()
        binaries.executable()

        binaries.all {
            linkTask.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xwasm-generate-wat=true")
                }
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies { 
                implementation(kotlin("stdlib"))
            }
        }
    }
}