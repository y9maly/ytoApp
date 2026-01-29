@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinWithJavaTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    applyDefaultHierarchyTemplate {
        common {
            group("notJvm") {
                withCompilations {
                    val target = it.target
                    target !is KotlinJvmTarget &&
                    (target !is KotlinWithJavaTarget<*, *> || target.platformType != KotlinPlatformType.jvm)
                }
            }
        }
    }

    androidLibrary {
        namespace = "me.maly.y9to"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        androidResources {
            enable = true
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.flowmvi.core)
            implementation(libs.flowmvi.compose)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.navigation3kmp.ui)
            implementation("androidx.paging:paging-common:3.4.0-rc01")
            implementation("androidx.paging:paging-compose:3.4.0-rc01")
            implementation("tech.annexflow.compose:constraintlayout-compose-multiplatform:0.6.1")
            implementation("dev.chrisbanes.haze:haze:1.7.1")

            implementation("me.maly.y9to:sdk:1.0-SNAPSHOT")
            implementation("me.maly.y9to:api-types:1.0-SNAPSHOT")
            implementation("me.maly.y9to:api-inputs:1.0-SNAPSHOT")
            implementation("me.maly.y9to:api-results:1.0-SNAPSHOT")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

dependencies {
//    debugImplementation(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "me.maly.y9to.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "me.maly.y9to"
            packageVersion = "1.0.0"
        }
    }
}
