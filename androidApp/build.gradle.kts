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

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "org.latinpray.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "org.latinpray.android"
        minSdk = 28
        targetSdk = 35
        versionCode = properties["app.version.code"].toString().toInt()
        versionName = properties["app.version.name"].toString()
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    //buildToolsVersion = "34.0.0"

    dependencies {
        implementation(projects.shared)
        implementation(libs.androidx.compose.runtime)
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.compose.ui.tooling)
        implementation(libs.androidx.compose.ui.tooling.preview)
        implementation(libs.androidx.compose.activity)
        implementation(libs.androidx.compose.material)
        implementation(libs.androidx.compose.foundation)
        debugImplementation(libs.androidx.compose.ui.tooling)
    }
    bundle {}
}
