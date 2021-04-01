import kr.entree.spigradle.kotlin.paper

plugins {
    id("kr.entree.spigradle") version "2.2.3"
}
repositories {
    maven(url = "https://repo.citizensnpcs.co/")
    maven(url = "https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    api(project(":impl-abstraction"))
    implementation("cloud.commandframework:cloud-paper:1.4.0")
    compileOnly(paper("1.16.5"))
    compileOnly("net.citizensnpcs:citizens:2.0.27-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.6.1-SNAPSHOT")
}

spigot {
    name = "Replay"
    version = project.version.toString()
    authors("NickAcPT")
    apiVersion = "1.16"
    depends("Citizens", "ProtocolLib")
    debug {
        this.eula = true
        this.buildVersion = "1.16.5"
    }
}