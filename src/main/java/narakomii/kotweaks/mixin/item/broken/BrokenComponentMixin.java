package narakomii.kotweaks.mixin.item.broken;

import narakomii.kotweaks.utils.BrokenItems;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DataComponentHolder.class)
interface BrokenComponentMixin<T> {
    /*@Inject(method = "get", at = @At("HEAD"), cancellable = true)
    default void get(DataComponentType<? extends T> type, CallbackInfoReturnable<T> cir) {
        if (((DataComponentHolder) this) instanceof ItemStack item && item.isBroken() && BrokenItems.IGNORED_COMPONENTS.contains(type)) {
            cir.setReturnValue(null);
            cir.cancel();
        }
    }*/
}