package io.github.openminigameserver.replay.platform.minestom

import io.github.openminigameserver.replay.replayer.statehelper.ControlItemAction
import net.minestom.server.data.DataImpl
import net.minestom.server.item.ItemStack

const val actionData = "controlItem"

var ItemStack.controlItemAction: ControlItemAction
    get() = data?.get<ControlItemAction>(actionData) ?: ControlItemAction.NONE
    set(value) {
        if (data == null) {
            data = DataImpl()
        }
        data?.set(actionData, value)
    }