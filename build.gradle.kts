plugins {
    kotlin("jvm") version "1.4.30-M1"
}

group = "io.github.openminigameserver"
version = "1.0-SNAPSHOT"

allprojects.forEach {

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
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
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
    implementation(project(":Replay-Model"))
    compileOnly(minestom("c5d56ae820"))
    testImplementation(minestom("c5d56ae820"))
}

fun minestom(commit: String): String {
    return "com.github.Minestom:Minestom:$commit"
}

tasks {
    val templateContext = mapOf("version" to project.version.toString())
    processResources {
        expand(*templateContext.toList().toTypedArray())
    }

    create<Copy>("generateKotlinBuildInfo") {
        inputs.properties(templateContext) // for gradle up-to-date check
        from("src/template/kotlin/")
        into("$buildDir/generated/kotlin/")
        expand(*templateContext.toList().toTypedArray())
    }

    kotlin.sourceSets["main"].kotlin.srcDir("$buildDir/generated/kotlin")
    compileKotlin.get().dependsOn(get("generateKotlinBuildInfo"))
}