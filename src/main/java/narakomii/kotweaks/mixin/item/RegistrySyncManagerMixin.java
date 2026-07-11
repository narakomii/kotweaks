package narakomii.kotweaks.mixin.item;

import narakomii.kotweaks.KoTweaks;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RegistrySyncManager.class)
abstract class RegistrySyncManagerMixin {
    @Redirect(method = "createAndPopulateRegistryMap", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;getKey(Ljava/lang/Object;)Lnet/minecraft/resources/Identifier;"))
    private static <T> Identifier e(Registry<T> instance, T t) {
        var id = instance.getKey(t);
        if (id != null && instance.key().registry().equals(BuiltInRegistries.ITEM.key().registry())) {
            if (id.getNamespace().equals(KoTweaks.MOD_ID)) {
                return null;
            }
        }

        return id;
    }
}
