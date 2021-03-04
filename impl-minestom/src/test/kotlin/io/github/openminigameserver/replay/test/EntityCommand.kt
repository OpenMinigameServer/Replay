package io.github.openminigameserver.replay.test

/*
object EntityCommand : Command("entity") {

    val entities = mutableListOf<Entity>()

    init {
        addSyntax({ sender: CommandSender, args: Arguments ->
            if (sender !is Player) return@addSyntax
            val type = args.getEntityType("type")

            val lastEntity = EntityHelper.createEntity(type, sender.position, null, false)
            lastEntity.isAutoViewable = true
            lastEntity.setInstance(sender.instance!!)
            entities.add(lastEntity)

            if (lastEntity is EntityCreature) {
                val targetGoal = FollowTargetGoal(lastEntity, UpdateOption(1, TimeUnit.TICK))
                lastEntity.target = sender
                lastEntity.goalSelectors.add(targetGoal)
            }

        }, ArgumentWord("of").from("living"), ArgumentEntityType("type"))
        addSyntax({ sender: CommandSender, args: Arguments ->
            entities.forEach {
                if (it is EntityCreature) {
                    it.currentGoalSelector?.end()
                    it.goalSelectors.clear()
                }
                it.remove()
            }
            entities.clear()
        }, ArgumentWord("of").from("delete"))
    }
}*/
