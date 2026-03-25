plugins {
    kotlin("jvm") version "2.3.0" apply false
    id("org.jetbrains.dokka") version "1.7.10" apply false
    id("com.github.gmazzo.buildconfig") version "2.1.0" apply false
    id("com.vanniktech.maven.publish") version "0.31.0" apply false
    id("com.gradleup.shadow") version "8.3.6" apply false
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    if (!name.startsWith("sample") && parent?.name?.startsWith("sample") != true) {
        group = property("GROUP").toString()
        version = property("VERSION_NAME").toString()

        apply(plugin = "com.vanniktech.maven.publish")
    }
}
