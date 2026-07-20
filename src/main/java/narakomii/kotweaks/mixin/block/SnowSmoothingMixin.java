package narakomii.kotweaks.mixin.block;

import narakomii.kotweaks.utils.MiscUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SnowLayerBlock.class)
abstract class SnowSmoothingMixin {
    @Shadow
    protected abstract boolean canSurvive(BlockState state, LevelReader level, BlockPos pos);

    /**
     * @author narrakomii
     * @reason makes snow flatten away to nearby blocks when randomly ticked
     */
    @Overwrite
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        final int currentHeight = state.getValue(SnowLayerBlock.LAYERS);
        int newHeight = currentHeight;
        if (level.getBrightness(LightLayer.BLOCK, pos) > 11)
            newHeight -= 1;

        final int diff = 3;
        if (newHeight >= diff) {
            var newPos = MiscUtils.pick(random, MiscUtils.facesXZ(pos));
            var existingState = level.getBlockState(newPos);
            if (existingState.is(Blocks.SNOW)) {
                int existingStateHeight = existingState.getValue(SnowLayerBlock.LAYERS);

                if (existingStateHeight <= newHeight - diff) {
                    Block.pushEntitiesUp(state, existingState, level, newPos);
                    level.setBlockAndUpdate(newPos, existingState.setValue(SnowLayerBlock.LAYERS, existingStateHeight + 1));

                    newHeight -= 1;
                }
            } else if (existingState.isAir() && canSurvive(state, level, newPos)) {
                BlockState newState = Blocks.SNOW.defaultBlockState();
                newState.setValue(SnowLayerBlock.LAYERS, 1);
                Block.pushEntitiesUp(state, newState, level, newPos);
                level.setBlockAndUpdate(newPos, newState);

                newHeight -= 1;
            }
        }

        if (currentHeight != newHeight) {
            if (newHeight > 0) {
                level.setBlockAndUpdate(pos, state.setValue(SnowLayerBlock.LAYERS, newHeight));
            } else {
                level.removeBlock(pos, false);
            }
        }
    }
}
