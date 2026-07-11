package narakomii.kotweaks.mixin.entity.player;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
abstract class PlayerMixin extends Avatar {
    private PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    /*@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void attack(final Entity entity, CallbackInfo ci, @Local(name = "baseDamage") float baseDamage, @Local(name = "totalDamage") float totalDamage) {
        //KoTweaks.LOGGER.info(String.format("baseDamage: %f, totalDamage: %f", baseDamage, totalDamage));
    }*/

    @Inject(method = "isSweepAttack", at = @At("HEAD"), cancellable = true)
    private void isSweepAttack(boolean fullStrengthAttack, boolean criticalAttack, boolean knockbackAttack, CallbackInfoReturnable<Boolean> cir) {
        if (getItemInHand(InteractionHand.MAIN_HAND).isBroken()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
