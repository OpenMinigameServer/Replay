dependencies {
    implementation("com.github.luben:zstd-jni:1.4.8-1")

    api(platform("com.fasterxml.jackson:jackson-bom:2.12.1"))
    api("com.fasterxml.jackson.core:jackson-core")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    api("com.fasterxml.jackson.dataformat:jackson-dataformat-smile")

    api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

}