plugins {
    kotlin("jvm") version "1.4.30-M1"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    `maven-publish`
}


allprojects.forEach {

    it.group = "io.github.openminigameserver.Replay"
    it.version = "1.0-SNAPSHOT"

    it.apply(plugin = "kotlin")
    it.apply(plugin = "maven-publish")
    it.apply(plugin = "com.github.johnrengelman.shadow")

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
        api(kotlin("stdlib"))

        api("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.2")
    }

    it.java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by it.tasks
    compileKotlin.kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs =
            freeCompilerArgs + "-Xjvm-default=enable" + "-Xopt-in=kotlin.RequiresOptIn" + "-Xopt-in=kotlin.time.ExperimentalTime" + "-Xopt-in=kotlin.contracts.ExperimentalContracts"
    }
    val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by it.tasks
    compileTestKotlin.kotlinOptions {
        jvmTarget = "11"
    }

    it.publishing {
        publications {
            create<MavenPublication>(it.project.name) {
                groupId = it.project.group.toString()
                artifactId = it.project.name
                version = it.project.version.toString()
                from(it.components["java"])
            }
        }
    }

    if (it.tasks.findByName("install") != null)
        it.tasks.replace("install").dependsOn("publishToMavenLocal")
}

dependencies {
    api(project(":impl"))
}
