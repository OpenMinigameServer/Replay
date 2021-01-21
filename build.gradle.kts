import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30-M1"
}

group = "io.github.openminigameserver"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://libraries.minecraft.net")
    maven("https://repo.spongepowered.org/maven")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://kotlin.bintray.com/kotlinx/")
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly(minestom("c5d56ae820"))
    testImplementation(minestom("c5d56ae820"))
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
}

fun minestom(commit: String): String {
    return "com.github.Minestom:Minestom:$commit"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
    freeCompilerArgs =
        freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn" + "-Xopt-in=kotlin.time.ExperimentalTime" + "-Xopt-in=kotlin.contracts.ExperimentalContracts"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
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