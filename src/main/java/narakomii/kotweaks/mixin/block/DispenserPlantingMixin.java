package narakomii.kotweaks.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenseItemBehavior.class)
interface DispenserPlantingMixin {
    @Inject(method = "bootStrap", at = @At("HEAD"))
    private static void bootStrap(CallbackInfo ci) {
        addSapling(Items.ACACIA_SAPLING, Blocks.ACACIA_SAPLING, BlockTags.SUPPORTS_VEGETATION);
        addSapling(Items.AZALEA, Blocks.AZALEA, BlockTags.SUPPORTS_AZALEA);
        addSapling(Items.BIRCH_SAPLING, Blocks.BIRCH_SAPLING, BlockTags.SUPPORTS_VEGETATION);
        addSapling(Items.CHERRY_SAPLING, Blocks.CHERRY_SAPLING, BlockTags.SUPPORTS_VEGETATION);
        addSapling(Items.DARK_OAK_SAPLING, Blocks.DARK_OAK_SAPLING, BlockTags.SUPPORTS_VEGETATION);
        addSapling(Items.FLOWERING_AZALEA, Blocks.FLOWERING_AZALEA, BlockTags.SUPPORTS_AZALEA);
        addSapling(Items.JUNGLE_SAPLING, Blocks.JUNGLE_SAPLING, BlockTags.SUPPORTS_VEGETATION);
        addSapling(Items.OAK_SAPLING, Blocks.OAK_SAPLING, BlockTags.SUPPORTS_VEGETATION);
        addSapling(Items.PALE_OAK_SAPLING, Blocks.PALE_OAK_SAPLING, BlockTags.SUPPORTS_VEGETATION);
        addSapling(Items.SPRUCE_SAPLING, Blocks.SPRUCE_SAPLING, BlockTags.SUPPORTS_VEGETATION);
        addSapling(Items.WHEAT_SEEDS, Blocks.WHEAT, BlockTags.SUPPORTS_CROPS);
        addSapling(Items.BEETROOT_SEEDS, Blocks.BEETROOTS, BlockTags.SUPPORTS_CROPS);
        addSapling(Items.POTATO, Blocks.POTATOES, BlockTags.SUPPORTS_CROPS);
        addSapling(Items.CARROT, Blocks.CARROTS, BlockTags.SUPPORTS_CROPS);
        addSapling(Items.NETHER_WART, Blocks.NETHER_WART, BlockTags.SUPPORTS_NETHER_WART);
    }

    @Unique
    private static void addSapling(Item item, Block block, TagKey<Block> soilBlockTag) {
        DispenserBlock.registerBehavior(item, new OptionalDispenseItemBehavior() {
            @Override
            protected @NonNull ItemStack execute(final @NonNull BlockSource source, final @NonNull ItemStack dispensed) {
                setSuccess(true);

                Level level = source.level();
                BlockPos dispenserTarget = source.pos().relative(source.state().getValue(DispenserBlock.FACING));

                if (level.getBlockState(dispenserTarget).is(soilBlockTag)) {
                    if (level.getBlockState(dispenserTarget.above()).is(BlockTags.AIR)) {
                        level.setBlockAndUpdate(dispenserTarget.above(), block.defaultBlockState());
                        dispensed.shrink(1);
                    } else {
                        setSuccess(false);
                    }
                } else if (level.getBlockState(dispenserTarget.below()).is(soilBlockTag)) {
                    if (level.getBlockState(dispenserTarget).is(BlockTags.AIR)) {
                        level.setBlockAndUpdate(dispenserTarget, block.defaultBlockState());
                        dispensed.shrink(1);
                    } else {
                        setSuccess(false);
                    }
                } else {
                    setSuccess(false);
                }

                return dispensed;
            }
        });
    }
}
