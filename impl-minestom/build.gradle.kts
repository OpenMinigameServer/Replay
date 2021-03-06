val minestomVersion = "f7ec45802f"
val cloudVersion = "58e8fd76f3"

dependencies {
    api(project(":impl-abstraction"))
    implementation("com.github.OpenMinigameServer:cloud-minestom:$cloudVersion")
    implementation("cloud.commandframework:cloud-annotations:1.4.0")
    implementation("com.github.mworzala:adventure-platform-minestom:d208f53200")
    compileOnly(minestom(minestomVersion))
    testImplementation(minestom(minestomVersion))
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