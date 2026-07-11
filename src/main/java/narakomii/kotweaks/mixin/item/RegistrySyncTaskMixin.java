package narakomii.kotweaks.mixin.item;

import narakomii.kotweaks.KoTweaks;
import net.minecraft.server.network.config.SynchronizeRegistriesTask;
import net.minecraft.server.packs.repository.KnownPack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(SynchronizeRegistriesTask.class)
abstract class RegistrySyncTaskMixin {
    @ModifyArg(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/configuration/ClientboundSelectKnownPacks;<init>(Ljava/util/List;)V"), index = 0)
    private static List<KnownPack> hi(List<KnownPack> knownPacks) {
        return knownPacks.stream().map(pack -> {
            //KoTweaks.LOGGER.info(pack.toString());
            return pack;
        }).toList();
    }
}
