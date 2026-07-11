package narakomii.kotweaks.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import narakomii.kotweaks.KoTweaks;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;

@Mixin(RegistrySynchronization.class)
abstract class RegistrySyncMixin {
    @ModifyExpressionValue(method = "lambda$packRegistry$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;listElements()Ljava/util/stream/Stream;"))
    private static <T> Stream<Holder.Reference<T>> listElements(Stream<Holder.Reference<T>> original) {
        return original.filter(e -> {
            if (e.key().registry() != Registries.ITEM.registry())
                return true;

            //KoTweaks.LOGGER.info("{}", e.key().identifier());
            if (e.key().identifier().getNamespace().equals(KoTweaks.MOD_ID)) {
                //KoTweaks.LOGGER.info("caught registry entry {}", e.key().identifier());
                return false;
            }

            return true;
        });
    }
}
