/*
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, version 3 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. Look for COPYING file in the top folder.
 *  If not, see http://www.gnu.org/licenses/.
 */

import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("org.jetbrains.kotlinx.atomicfu") version "0.29.0"
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
            binaryOption("bundleId", "org.latinpray.shared")
        }
    }

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation(compose.ui)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.materialIconsExtended)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(libs.jb.androidx.navigation.compose)
            implementation(libs.kaml)
            implementation(libs.kotlin.serialization.json)
            //implementation(libs.kotlin.serialization.yaml)
            implementation(libs.squareup.okio)
            implementation(libs.kotlin.stdlib)
            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.datastore.pref)
            implementation(libs.markdown.renderer)
            implementation(libs.markdown.renderer.m2)
            implementation(libs.markdown.renderer.m3)
            // Add the purchases-kmp dependencies.
            implementation(libs.purchases.core)
            implementation(libs.purchases.datetime)   // Optional
            implementation(libs.purchases.either)     // Optional
            implementation(libs.purchases.result)     // Optional
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.kcron.common)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        named { it.lowercase().startsWith("ios") }.configureEach {
            languageSettings {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "org.latinpray.shared"
    generateResClass = auto
}

android {
    namespace = "org.latinpray"
    compileSdk = 35
    defaultConfig {
        minSdk = 28
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    sourceSets["main"].apply {
        res.srcDirs(
            "src/androidMain/res",
            "src/commonMain/resources/res",
            "src/commonMain/composeResources"
        )
        // 3..
        assets.srcDirs("src/commonMain/resources/assets")
    }
}
dependencies {
    implementation(libs.androidx.compose.ui)
    implementation(libs.jb.androidx.navigation.compose)
    implementation(libs.kaml)
    implementation(libs.kotlin.serialization.json)
    //implementation(libs.kotlin.serialization.yaml)
    implementation(libs.squareup.okio)
    implementation(libs.kotlin.stdlib)
}
