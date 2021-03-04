package io.github.openminigameserver.replay.mixins;

import io.github.openminigameserver.replay.MinestomReplayExtension;
import io.github.openminigameserver.replay.extensions.MinestomInteropExtensionsKt;
import io.github.openminigameserver.replay.platform.minestom.MinestomReplayPlatform;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    public PlayerMixin(@NotNull EntityType entityType, @NotNull Position spawnPosition) {
        super(entityType, spawnPosition);
    }

    @Inject(method = "refreshHeldSlot", at = @At("RETURN"))
    public void onChangeHeld(byte slot, CallbackInfo ci) {
        if (getInstance() != null) {
            var recorder = MinestomInteropExtensionsKt.getRecorder(getInstance());
            if (recorder != null) {
                recorder.onEntityEquipmentChange(((MinestomReplayPlatform) MinestomReplayExtension.extension.getPlatform()).getPlayer((Player) (Object) this));
            }
        }
    }
}
