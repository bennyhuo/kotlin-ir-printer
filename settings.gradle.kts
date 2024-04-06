pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include("ir-printer-compiler")
include("ir-printer-gradle-plugin")

val local = file("composite_build.local")
if (local.exists()) {
    local.readLines().forEach {
        val f = file("../$it")
        if (f.exists()) {
            includeBuild(f)
        }
    }
}
