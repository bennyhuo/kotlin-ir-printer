# Kotlin IR Printer

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.bennyhuo.kotlin/ir-printer-gradle-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.bennyhuo.kotlin/ir-printer-gradle-plugin)

Print transformed Kotlin IR into sources. Raw IR and two Kotlin like source code styles are supported. LLVM bitcode will also be disassembled on Kotlin Native targets.

## Introduction

### Kotlin IR

If you are interesting with the output of any compiler plugins, such as Jetpack Compose, you will definitely need this.

When we write the code as below:

```kt
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
```

You may wonder what will happen to the  function `Greeting`. You will most likely decompile the compiled classes back into Java sources, but that would be a mess.

With IR printer, you will get the Kotlin source code or raw IR structure directly from the Kotlin IR. So the corresponding output of `Greeting` will be:

```kt
@Composable
@ComposableTarget(applier = "androidx.compose.ui.UiComposable")
fun Greeting(name: String, modifier: Modifier?, $composer: Composer?, $changed: Int, $default: Int) {
    $composer = $composer.startRestartGroup(853713123)
    sourceInformation($composer, "C(Greeting)P(1)30@1094L70:MainActivity.kt#tztr8q")
    val $dirty = $changed
    if ($default and 0b0001 != 0) {
        $dirty = $dirty or 0b0110
    } else if ($changed and 0b1110 == 0) {
        $dirty = $dirty or if ($composer.changed(name)) 0b0100 else 0b0010
    }
    if ($default and 0b0010 != 0) {
        $dirty = $dirty or 0b00110000
    } else if ($changed and 0b01110000 == 0) {
        $dirty = $dirty or if ($composer.changed(modifier)) 0b00100000 else 0b00010000
    }
    if ($dirty and 0b01011011 != 0b00010010 || !$composer.skipping) {
        if ($default and 0b0010 != 0) {
            modifier = Companion
        }
        if (isTraceInProgress()) {
            traceEventStart(853713123, $dirty, -1, "com.bennyhuo.kotlin.printer.sample.Greeting (MainActivity.kt:29)")
        }
        Text("Hello $name!", modifier, <unsafe-coerce>(0L), <unsafe-coerce>(0L), null, null, null, <unsafe-coerce>(0L), null, null, <unsafe-coerce>(0L), <unsafe-coerce>(0), false, 0, 0, null, null, $composer, 0b01110000 and $dirty, 0, 131068)
        if (isTraceInProgress()) {
            traceEventEnd()
        }
    } else {
        $composer.skipToGroupEnd()
    }
    $composer.endRestartGroup()?.updateScope { $composer: Composer?, $force: Int ->
        Greeting(name, modifier, $composer, updateChangedFlags($changed or 0b0001), $default)
    }
}
```

The output file will be located in `$buildDir/outputs/kotlin/ir` by default and can be customized via the gradle extensions.

### LLVM IR

It is helpful to review the LLVM IR if you want to dive into Kotlin Native. The LLVM bitcode file, which is named out.bc, are always deleted after generating the binary outputs. This project will help you keep the bitcode files and disassemble it into LLVM IR.

You can run the gradle task named like `disassemble<binary-name><target-name>Bitcode` on a Kotlin Native project, for example on macos_arm64. After that, you will get the whole files in directory `<build-dir>/outputs` like below: 

```bash
kotlin
├── ir
│   └── macosArm64
│       ├── Main.kt
├── llvm-bc
│   └── macosArm64
│       ├── kotlin-native-sample.kexe.o
│       └── out.bc
└── llvm-ir
    └── macosArm64
        └── out.ll
```

'out.ll' is the disassembled LLVM IR, you can view it with any text editor you like:

```llvm
define internal void @"kfun:#Greeting(kotlin.String;androidx.compose.ui.Modifier?;androidx.compose.runtime.Composer?;kotlin.Int;kotlin.Int){}"(%struct.ObjHeader* %0, %struct.ObjHeader* %1, %struct.ObjHeader* %2, i32 %3, i32 %4) #10 !dbg !567387 {
prologue:
  %"p-$changed" = alloca i32, align 4
  call void @llvm.dbg.declare(metadata i32* %"p-$changed", metadata !567388, metadata !DIExpression()), !dbg !567389
  %"p-$default" = alloca i32, align 4
  call void @llvm.dbg.declare(metadata i32* %"p-$default", metadata !567390, metadata !DIExpression()), !dbg !567389
  %"$dirty" = alloca i32, align 4
  call void @llvm.dbg.declare(metadata i32* %"$dirty", metadata !567391, metadata !DIExpression()), !dbg !567389

...

call_success54:                                   ; preds = %when_next52
  call void @"kfun:androidx.compose.runtime.ScopeUpdateScope#updateScope(kotlin.Function2<androidx.compose.runtime.Composer,kotlin.Int,kotlin.Unit>){}-trampoline"(%struct.ObjHeader* %114, %struct.ObjHeader* %119), !dbg !567389
  br label %call_success55, !dbg !567389

call_success55:                                   ; preds = %call_success54
  br label %when_exit53, !dbg !567389

when_exit53:                                      ; preds = %call_success55, %when_case51
  br label %epilogue, !dbg !567401

epilogue:                                         ; preds = %when_exit53
  call void @LeaveFrame(%struct.ObjHeader** %7, i32 3, i32 28) #13, !dbg !567401
  ret void, !dbg !567401
}
```

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
    isLlvmIrEnabled = true // default: true, set to false to disable LLVM IR generating only.
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

Kotlin IR will be printed after compiling.

For Native project, for example on target macos_arm64: 

```
./gradlew disassembleDebugExecutableMacosArm64Bitcode
```

LLVM IR will be generated after this.

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


