plugins {
    kotlin("jvm") version "1.4.30-M1"
}


allprojects.forEach {

    it.group = "io.github.openminigameserver"
    it.version = "1.0-SNAPSHOT"


    it.apply(plugin = "kotlin")

    it.repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://libraries.minecraft.net")
        maven("https://repo.spongepowered.org/maven")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://kotlin.bintray.com/kotlinx/")
    }


    it.dependencies {
        implementation(kotlin("stdlib"))

        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.2")
    }

    it.java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by it.tasks
    compileKotlin.kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs =
            freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn" + "-Xopt-in=kotlin.time.ExperimentalTime" + "-Xopt-in=kotlin.contracts.ExperimentalContracts"
    }
    val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by it.tasks
    compileTestKotlin.kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":impl"))
}