package narakomii.kotweaks.mixin.item;

import narakomii.kotweaks.types.CustomItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Item.class)
abstract class ItemNameMaskMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent component(String key) {
        if (((Item) (Object) this) instanceof CustomItem item)
            return Component.translatableWithFallback(key, item.getFallbackName());

        return Component.translatable(key);
    }
}
