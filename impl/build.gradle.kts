


dependencies {
    implementation(project(":model"))
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