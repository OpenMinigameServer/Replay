package io.github.openminigameserver.replay

data class PlayerNameSplitData(val name: String, val rest: String)

object PlayerNameSplittingHelper {
    private const val maxNameSize = 16
    const val npcNameSuffix = "§r"
    private const val npcMaxNameSize = maxNameSize - npcNameSuffix.length

    fun splitName(name: String): PlayerNameSplitData {
        return PlayerNameSplitData(
            name.subSequence(0 until npcMaxNameSize).toString() + npcNameSuffix,
            name.substring(npcMaxNameSize)
        )
    }
}