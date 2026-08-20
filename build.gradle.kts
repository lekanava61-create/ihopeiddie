plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 33
    
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("com.aliucord:Aliucord:main-SNAPSHOT")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.json:json:20220924")
}

tasks.register("make") {
    dependsOn("assembleRelease")
    doLast {
        val apk = buildDir.resolve("outputs/aar/release/ihopeiddie-release.aar")
        val dest = rootProject.buildDir.resolve("outputs/plugins/NukeAccount.zip")
        dest.parentFile?.mkdirs()
        apk.copyTo(dest, overwrite = true)
        println("Plugin built: $dest")
    }
}

tasks.register("deployWithAdb") {
    dependsOn("make")
    doLast {
        val dest = rootProject.buildDir.resolve("outputs/plugins/NukeAccount.zip")
        Runtime.getRuntime().exec(arrayOf("adb", "push", dest.absolutePath, "/sdcard/Aliucord/plugins/")).waitFor()
        println("Plugin deployed via ADB")
    }
}
