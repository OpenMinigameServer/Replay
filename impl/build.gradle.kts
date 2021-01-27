val minestomVersion = "7a54b4162d"

dependencies {
    api(project(":model"))
    implementation("com.github.OpenMinigameServer:cloud-minestom:58e8fd76f3")
    implementation("cloud.commandframework:cloud-annotations:1.4.0")
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