package io.github.openminigameserver.replay.test

import io.github.openminigameserver.replay.helpers.EntityHelper
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Arguments
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentWord
import net.minestom.server.command.builder.arguments.minecraft.registry.ArgumentEntityType
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player

object EntityCommand : Command("entity") {

    val entities = mutableListOf<Entity>()
    init {
        addSyntax({ sender: CommandSender, args: Arguments ->
            if (sender !is Player) return@addSyntax
            val type = args.getEntityType("type")

            val lastEntity = EntityHelper.createEntity(type, sender.position, null)
            lastEntity.isAutoViewable = true
            lastEntity.setInstance(sender.instance!!)
            entities.add(lastEntity)

        }, ArgumentWord("of").from("living"), ArgumentEntityType("type"))
        addSyntax({ sender: CommandSender, args: Arguments ->
            entities.forEach { it.remove() }
            entities.clear()
        }, ArgumentWord("of").from("delete"))
    }
}