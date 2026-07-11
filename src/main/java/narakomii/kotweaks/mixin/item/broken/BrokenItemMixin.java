package narakomii.kotweaks.mixin.item.broken;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
abstract class BrokenItemMixin {
    /*@Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
    public void getDestroySpeed(ItemStack itemStack, BlockState state, CallbackInfoReturnable<Float> cir) {
        if (itemStack.isBroken()) {
            cir.setReturnValue(1.0F);
            cir.cancel();
        }
    }

    @Inject(method = "isCorrectToolForDrops", at = @At("HEAD"), cancellable = true)
    public void isCorrectToolForDrops(ItemStack itemStack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (itemStack.isBroken()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }*/
}
