package narakomii.kotweaks.mixin.entity;

import narakomii.kotweaks.storage.entity.CustomEntityDataAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;

@Mixin(Mob.class)
abstract class SpecialMobMixin {
    @Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
    private void ai(CallbackInfo ci) {
        Mob me = (Mob) (Object) this;
        ServerLevel level = (ServerLevel) me.level();

        switch (CustomEntityDataAccess.get(me, CustomEntityDataAccess.Key.Type)) {
            case "mauler" -> {
                LivingEntity target = me.asValidTarget((LivingEntity) level.getEntity(UUID.fromString(Objects.requireNonNull(CustomEntityDataAccess.get(me, CustomEntityDataAccess.Key.ForcedTarget)))));
                int[] timers = CustomEntityDataAccess.getOrSet(me, CustomEntityDataAccess.Key.Timers);
                timers[0]++;
                CustomEntityDataAccess.set(me, CustomEntityDataAccess.Key.Timers, timers);

                if (target == null) {
                    stopping(ci);
                    return;
                }

                timers[0] = 0;
                me.setLastHurtByMob(target);
            }
            case null, default -> {}
        }
    }

    @Unique
    private void stopping(CallbackInfo ci) {
        Mob me = (Mob) (Object) this;
        me.setTarget(null);
        me.getNavigation().stop();
        me.getNavigation().tick();
        me.getMoveControl().setWait();
        me.getMoveControl().tick();
        me.setJumping(false);
        //me.getJumpControl().tick();
        ci.cancel();

        int[] timers = CustomEntityDataAccess.getOrSet(me, CustomEntityDataAccess.Key.Timers);
        if (timers[0] > 600) {
            me.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}
