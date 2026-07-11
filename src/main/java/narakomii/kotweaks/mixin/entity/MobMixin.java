package narakomii.kotweaks.mixin.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Mob.class)
abstract class MobMixin extends LivingEntity {
    private MobMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    /*@Inject(method = "doHurtTarget", at = @At("HEAD"))
    private void doHurtTarget(ServerLevel level, Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (this.getWeaponItem().isBroken()) {
            KoTweaks.LOGGER.info("item is broken");
        }
    }*/
}
