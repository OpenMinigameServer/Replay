package io.github.openminigameserver.replay.mixins;

import io.github.openminigameserver.replay.ReplayListener;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.utils.PacketUtils;
import net.minestom.server.utils.callback.validator.PlayerValidator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(PacketUtils.class)
public class PacketUtilsMixin {
    @Inject(method = "sendGroupedPacket(Ljava/util/Collection;" +
            "Lnet/minestom/server/network/packet/server/ServerPacket;" +
            "Lnet/minestom/server/utils/callback/validator/PlayerValidator;)V", at = @At("HEAD"))
    private static void onSendGroupedPacket(Collection<Player> players, ServerPacket packet,
                                            PlayerValidator playerValidator, CallbackInfo ci) {
        ReplayListener.handleSentPacket(packet, players);
    }
}
