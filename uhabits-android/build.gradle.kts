/*
 * Copyright (C) 2016-2025 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

plugins {
    alias(libs.plugins.agp)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint.plugin)
    alias(libs.plugins.mokkery)
}

tasks.compileLint {
    dependsOn("updateTranslators")
}

/*
Added on top of kotlinOptions to work around this issue:
https://youtrack.jetbrains.com/issue/KTIJ-24311/task-current-target-is-17-and-kaptGenerateStubsProductionDebugKotlin-task-current-target-is-1.8-jvm-target-compatibility-should#focus=Comments-27-6798448.0-0
Updating gradle might fix this, so try again in the future to remove this and run:
./gradlew --rerun-tasks :uhabits-android:kaptGenerateStubsReleaseKotlin
If this doesn't produce any warning, try to remove it.
 */
kotlin {
    jvmToolchain(17)
}

android {
    namespace = "org.isoron.uhabits"
    compileSdk = 36

    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets", "../uhabits-core/assets/main")
        }
    }

    defaultConfig {
        versionCode = 20301
        versionName = "2.3.1"
        minSdk = 28
        targetSdk = 36
        applicationId = "org.isoron.uhabits"
        //testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "org.isoron.uhabits.AllureFixRunner"
        testInstrumentationRunnerArguments["useTestStorageService"] = "true"
}

    signingConfigs {
        if (System.getenv("LOOP_KEY_ALIAS") != null) {
            create("release") {
                keyAlias = System.getenv("LOOP_KEY_ALIAS")
                keyPassword = System.getenv("LOOP_KEY_PASSWORD")
                storeFile = file(System.getenv("LOOP_KEY_STORE"))
                storePassword = System.getenv("LOOP_STORE_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
            if (signingConfigs.findByName("release") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }

        debug {
            enableUnitTestCoverage = true
            isDebuggable = true
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        targetCompatibility(JavaVersion.VERSION_17)
        sourceCompatibility(JavaVersion.VERSION_17)
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures.viewBinding = true
    lint.abortOnError = false
}

mokkery {
    defaultMockMode.set(dev.mokkery.MockMode.autofill)
    stubs.allowClassInheritance.set(true)
    stubs.allowConcreteClassInstantiation.set(true)
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.appIntro)
    implementation(libs.jsr305)
    implementation(libs.kotlin.inject.runtime)
    implementation(libs.guava)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.jackson)
    implementation(libs.ktor.client.json)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.appcompat)
    implementation(libs.legacy.preference.v14)
    implementation(libs.legacy.support.v4)
    implementation(libs.material)
    implementation(libs.documentfile)
    implementation(libs.opencsv)
    implementation(libs.konfetti.xml)
    implementation(project(":uhabits-core"))
    ksp(libs.kotlin.inject.compiler)

    androidTestImplementation(libs.annotation)
    androidTestImplementation(libs.kotlin.inject.runtime)
    androidTestImplementation(libs.espresso.contrib)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.ktor.client.mock)
    androidTestImplementation(libs.ktor.jackson)
    androidTestImplementation(libs.rules)
    androidTestImplementation(libs.uiautomator)

    testImplementation(libs.kotlin.inject.runtime)
    testImplementation(libs.junit.junit)

    androidTestImplementation(libs.kaspresso)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.tracing:tracing:1.1.0")

    androidTestImplementation("com.kaspersky.android-components:kaspresso-allure-support:1.5.3")
    androidTestImplementation("io.qameta.allure:allure-kotlin-android:2.4.0")
    androidTestImplementation("io.qameta.allure:allure-kotlin-junit4:2.4.0")


    androidTestImplementation("androidx.test:monitor:1.6.1")
    androidTestImplementation("androidx.test.services:test-services:1.5.0")


}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "io.qameta.allure") {
            useVersion("2.4.0")
        }
    }
}

tasks.register<Exec>("pullAllureResults") {
    group = "verification"
    description = "Выгружает отчеты Allure из защищенной внутренней памяти приложения"

    // Этот скрипт копирует файлы из /data/user/0/... в папку проекта
    commandLine(
        "sh", "-c",
        "adb shell \"run-as org.isoron.uhabits tar -c -C /data/user/0/org.isoron.uhabits/files/allure-results .\" | tar -x -v -C ./allure-results"
    )

    doFirst {
        val dir = file("allure-results")
        if (!dir.exists()) dir.mkdirs()
    }
}

