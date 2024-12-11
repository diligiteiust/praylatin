import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
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
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
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
    compileSdk = 34
    defaultConfig {
        minSdk = 28
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    sourceSets["main"].apply {
        res.srcDirs("src/androidMain/res", "src/commonMain/resources/res")
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

