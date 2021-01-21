package io.github.openminigameserver.replay.test

import net.minestom.server.Bootstrap

fun main(args: Array<String>) {
    System.setProperty("minestom.extension.indevfolder.classes", "../build/classes/java")
    System.setProperty("minestom.extension.indevfolder.resources", "../build/resources/main/")

    Bootstrap.bootstrap("io.github.openminigameserver.replay.test.ReplayKt", args)
}