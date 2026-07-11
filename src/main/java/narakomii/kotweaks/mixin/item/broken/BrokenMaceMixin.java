package narakomii.kotweaks.mixin.item.broken;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MaceItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MaceItem.class)
abstract class BrokenMaceMixin extends Item {
    private BrokenMaceMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "canSmashAttack", at = @At("HEAD"), cancellable = true)
    private static void canSmashAttack(LivingEntity attacker, CallbackInfoReturnable<Boolean> cir) {
        if (attacker.getWeaponItem().isBroken()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
