package narakomii.kotweaks.mixin.block;

import narakomii.kotweaks.utils.MiscUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(Fluid.class)
abstract class WaterFreezeMixin {
    @Shadow
    public abstract boolean isSame(Fluid other);

    @Unique
    private static final Map<Identifier, Double> vals = new HashMap<>();
    static {
        vals.put(Identifier.parse("minecraft:ice"), 1.0);
        vals.put(Identifier.parse("minecraft:packed_ice"), 1.5);
        vals.put(Identifier.parse("minecraft:blue_ice"), 2.0);
    }

    @Inject(method = "randomTick", at = @At("HEAD"))
    private void randomTick(ServerLevel level, BlockPos pos, FluidState fluidState, RandomSource random, CallbackInfo ci) {
        if (fluidState.is(Fluids.WATER)) {
            double freezeSpeed = 0;

            for (BlockPos otherPos : MiscUtils.facesNotUp(pos)) {
                var block = BuiltInRegistries.BLOCK.getKey(level.getBlockState(otherPos).getBlock());
                if (vals.containsKey(block))
                    freezeSpeed += vals.get(block);
            }

            if (freezeSpeed > 0.0 && random.nextInt((int)(15.0 / freezeSpeed) + 1) == 0)
                level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
        }
    }

    @Inject(method = "isRandomlyTicking", at = @At("HEAD"), cancellable = true)
    private void isRandomlyTicking(CallbackInfoReturnable<Boolean> cir) {
        if (this.isSame(Fluids.WATER)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
