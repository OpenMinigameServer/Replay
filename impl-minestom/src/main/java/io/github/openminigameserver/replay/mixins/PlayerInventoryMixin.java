package io.github.openminigameserver.replay.mixins;

import io.github.openminigameserver.replay.MinestomReplayExtension;
import io.github.openminigameserver.replay.extensions.MinestomInteropExtensionsKt;
import io.github.openminigameserver.replay.platform.minestom.MinestomReplayPlatform;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Shadow
    @Final
    protected Player player;

    @Inject(method = "update", at = @At("TAIL"))
    public void onInventoryUpdate(CallbackInfo ci) {
        if (player.getInstance() != null) {
            var recorder = MinestomInteropExtensionsKt.getRecorder(player.getInstance());
            if (recorder != null) {
                recorder.onEntityEquipmentChange(((MinestomReplayPlatform) MinestomReplayExtension.extension.getPlatform()).getPlayer((Player) (Object) this));
            }
        }
    }

}
