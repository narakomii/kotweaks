package narakomii.kotweaks.mixin.item;

import narakomii.kotweaks.KoTweaks;
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientboundRegistryDataPacket.class)
abstract class Net2ClientRegistryDataMixin {
    @Inject(method = "<init>", at = @At("HEAD"))
    private static void hi(ResourceKey registry, List entries, CallbackInfo ci) {
        //KoTweaks.LOGGER.info("{}", registry.identifier());
    }
}
