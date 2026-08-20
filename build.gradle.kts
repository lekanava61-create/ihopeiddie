plugins {
    id("com.android.library") version "7.4.2"
    kotlin("android") version "1.8.20"
}

android {
    compileSdk = 33
    namespace = "com.aliucord.plugins"
    
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
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
        val dest = rootProject.buildDir.resolve("outputs/plugins")
        dest.mkdirs()
        apk.copyTo(dest.resolve("NukeAccount.zip"), overwrite = true)
        println("✅ Plugin built: ${dest.resolve("NukeAccount.zip")}")
    }
}

tasks.register("deployWithAdb") {
    dependsOn("make")
    doLast {
        val dest = rootProject.buildDir.resolve("outputs/plugins/NukeAccount.zip")
        val result = Runtime.getRuntime().exec(arrayOf("adb", "push", dest.absolutePath, "/sdcard/Aliucord/plugins/")).waitFor()
        if (result == 0) {
            println("✅ Plugin deployed via ADB")
        } else {
            println("❌ ADB deployment failed with code: $result")
        }
    }
}
