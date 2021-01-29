package io.github.openminigameserver.replay.mixins;

import io.github.openminigameserver.replay.extensions.MinestomInteropExtensionsKt;
import net.minestom.server.data.Data;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.CustomBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InstanceContainer.class)
public abstract class InstanceMixin {

    @Inject(method = "UNSAFE_setBlock", at = @At(value = "INVOKE", target = "Lnet/minestom/server/instance/Chunk;" +
            "UNSAFE_setBlock(IIISSLnet/minestom/server/data/Data;Z)V"))
    public void onSetBlock(Chunk chunk, int x, int y, int z, short blockStateId, CustomBlock customBlock, Data data,
                           CallbackInfo ci) {
        //noinspection ConstantConditions
        if (!(((Object) this) instanceof Instance)) return;
        var instance = (Instance) (Object) this;
        var recorder = MinestomInteropExtensionsKt.getRecorder(instance);
        if (recorder == null) return;

        recorder.notifyBlockChange(x, y, z, blockStateId);
    }
}
