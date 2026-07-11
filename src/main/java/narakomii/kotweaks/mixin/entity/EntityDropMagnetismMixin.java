package narakomii.kotweaks.mixin.entity;

import narakomii.kotweaks.enchantment.ModEnchantments;
import narakomii.kotweaks.utils.ItemUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
abstract class EntityDropMagnetismMixin extends Entity {
    private EntityDropMagnetismMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Shadow
    public abstract void dropFromLootTable(ServerLevel level, DamageSource source, boolean playerKilled, ResourceKey<LootTable> lootTable, Consumer<ItemStack> itemStackConsumer);

    /**
     * @author narakomii
     * @reason check for magnetism enchant and spawn drops at killer
     */
    @Overwrite
    public void dropFromLootTable(final ServerLevel level, final DamageSource source, final boolean playerKilled, final ResourceKey<LootTable> lootTable) {
        Entity entity = source.getEntity();

        if (entity instanceof LivingEntity livingEntity && EnchantmentHelper.getEnchantmentLevel(level.registryAccess().getOrThrow(ModEnchantments.MAGNETISM), livingEntity) > 0) {
            dropFromLootTable(level, source, playerKilled, lootTable, itemStack -> ItemUtils.giveAsDrop(livingEntity, itemStack));
        } else {
            dropFromLootTable(level, source, playerKilled, lootTable, itemStack -> spawnAtLocation(level, itemStack));
        }
    }
}
