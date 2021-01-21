package io.github.openminigameserver.replay.model.recordable.impl

import io.github.openminigameserver.replay.model.recordable.EntityRecordableAction
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import net.minestom.server.entity.Player

class RecPlayerHandAnimation(val hand: Player.Hand, entity: RecordableEntity): EntityRecordableAction(entity)