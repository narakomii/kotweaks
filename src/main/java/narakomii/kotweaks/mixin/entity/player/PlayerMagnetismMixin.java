package narakomii.kotweaks.mixin.entity.player;

import com.llamalad7.mixinextras.sugar.Local;
import narakomii.kotweaks.enchantment.ModEnchantments;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Player.class)
abstract class PlayerMagnetismMixin extends Avatar {
    private PlayerMagnetismMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Shadow
    protected abstract void touch(Entity entity);

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", shift = At.Shift.BEFORE))
    private void magnetismPickups(CallbackInfo ci, @Local(name = "pickupArea") AABB pickupArea, @Local(name = "entities") List<Entity> entities, @Local(name = "orbs") List<Entity> orbs) {
        int level = EnchantmentHelper.getEnchantmentLevel(level().registryAccess().getOrThrow(ModEnchantments.MAGNETISM), this);
        if (level > 0) {
            double xz = 1.0 + (double) level;
            double y = 3.0 + (double) level;
            List<Entity> newEntities = level().getEntities(this, pickupArea.inflate(xz, y, xz));

            for (Entity entity : newEntities) {
                if (entities.contains(entity))
                    continue;

                if (entity.is(EntityType.EXPERIENCE_ORB)) {
                    orbs.add(entity);
                } else if (!entity.isRemoved() && entity.is(EntityType.ITEM)) {
                    touch(entity);
                }
            }
        }
    }
}
