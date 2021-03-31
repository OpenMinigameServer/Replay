import kr.entree.spigradle.kotlin.paper

plugins {
    id("kr.entree.spigradle") version "2.2.3"
}
repositories {
    maven(url = "https://repo.citizensnpcs.co/")
}

dependencies {
    api(project(":impl-abstraction"))
    implementation("cloud.commandframework:cloud-paper:1.4.0")
    compileOnly(paper("1.16.5"))
    compileOnly("net.citizensnpcs:citizens:2.0.27-SNAPSHOT")
}

spigot {
    name = "Replay"
    version = project.version.toString()
    authors("NickAcPT")
    apiVersion = "1.16"
    depends("Citizens")
    debug {
        this.eula = true
        this.buildVersion = "1.16.5"
    }
}