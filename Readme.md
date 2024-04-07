# Kotlin IR Printer

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.bennyhuo.kotlin/ir-printer-gradle-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.bennyhuo.kotlin/ir-printer-gradle-plugin)

Print transformed Kotlin IR into sources. Raw IR and two Kotlin like source code styles are supported. 

## Try it

Configure the repos:

**settings.gradle.kts**

```
pluginManagement {
    repositories {
        mavenCentral()
        // for snapshots
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        // for snapshots
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}
```

Apply the plugin:

**build.gradle.kts**

```
plugins {
    ...
    // Make sure to apply it in the last.
    id("com.bennyhuo.kotlin.ir.printer") version "<latest-version>"
}

irPrinter {
    isEnabled = true // default: true
    indent = "    " //  default: "  "
    outputDir = "/path/to/output" // default: <project>/build/outputs/kotlin/ir
    outputType = OutputType.KOTLIN_LIKE_JETPACK_COMPOSE_STYLE
}
```

And then, just compile the code:

```
./gradlew compileKotlin
```

for Android project:

```
./gradlew :app:compileDebugKotlin
```

## Change Log

See [releases](ttps://github.com/bennyhuo/kotlin-ir-printer/releases).

# License

[MIT License](LICENSE)

    Copyright (c) 2024 Bennyhuo

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.


