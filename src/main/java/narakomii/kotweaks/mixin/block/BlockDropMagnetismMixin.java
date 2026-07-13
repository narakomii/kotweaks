package narakomii.kotweaks.mixin.block;

import narakomii.kotweaks.game.ModEnchantments;
import narakomii.kotweaks.utils.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Block.class)
abstract class BlockDropMagnetismMixin {
    @Shadow
    public static List<ItemStack> getDrops(
            @NotNull BlockState state,
            ServerLevel level,
            BlockPos pos,
            @Nullable BlockEntity blockEntity,
            @Nullable Entity breaker,
            ItemInstance tool
    ) { throw new AssertionError(); }

    @Shadow
    public static void popResource(
            @NotNull Level level,
            @NotNull BlockPos pos,
            ItemStack itemStack
    ) { throw new AssertionError(); }

    /**
     * @author narakomii
     * @reason ues
     */
    // TODO how to get indirect drops like from sugar cane above the sugar cane that was actually broken?
    @Overwrite
    public static void dropResources(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final @Nullable BlockEntity blockEntity,
            final @Nullable Entity breaker,
            final ItemStack tool
    ) {
        if (level instanceof ServerLevel serverLevel) {
            List<ItemStack> drops = getDrops(state, serverLevel, pos, blockEntity, breaker, tool);
            if (breaker instanceof ServerPlayer player && 0 < EnchantmentHelper.getEnchantmentLevel(level.registryAccess().getOrThrow(ModEnchantments.MAGNETISM), player)) {
                drops.forEach(stack -> ItemUtils.giveAsDrop(player, stack));
            } else {
                drops.forEach(stack -> popResource(level, pos, stack));
            }
            state.spawnAfterBreak(serverLevel, pos, tool, true);
        }
    }
}
