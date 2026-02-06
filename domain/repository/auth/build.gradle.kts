@file:OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinWithJavaTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

fun KotlinHierarchyBuilder.withTargets(block: (KotlinTarget) -> Boolean) = withCompilations { block(it.target) }

kotlin {
    applyDefaultHierarchyTemplate {
        common {
            group("nonAndroid") {
                withTargets { it !is KotlinAndroidTarget }
            }

            group("nonJvm") {
                withTargets { !(it is KotlinJvmTarget || (it is KotlinWithJavaTarget<*, *> && it.platformType == KotlinPlatformType.jvm)) }
            }

            group("nonWeb") {
                withTargets { !(it.platformType == KotlinPlatformType.js || (it.platformType == KotlinPlatformType.wasm && it is KotlinJsIrTarget)) }
            }
        }
    }

    jvm {
        compilerOptions.jvmTarget = JvmTarget.JVM_11
    }

    js(IR) {
        binaries.library()
        useEsModules()
        generateTypeScriptDefinitions()
        browser()
        nodejs()
    }

    wasmJs {
        browser()
        nodejs()
    }

    iosArm64()
    iosSimulatorArm64()
    macosArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain:types"))
            implementation("me.maly.y9to:sdk:1.0-SNAPSHOT")
            implementation("me.maly.y9to:api-types:1.0-SNAPSHOT")
            implementation("me.maly.y9to:api-inputs:1.0-SNAPSHOT")
            implementation("me.maly.y9to:api-results:1.0-SNAPSHOT")
        }
    }
}
