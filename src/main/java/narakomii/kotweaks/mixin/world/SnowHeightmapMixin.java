package narakomii.kotweaks.mixin.world;

import com.llamalad7.mixinextras.sugar.Local;
import narakomii.kotweaks.types.LevelExtension;
import narakomii.kotweaks.types.TimedMap;
import narakomii.kotweaks.utils.MathUtils;
import narakomii.kotweaks.utils.MiscUtils;
import narakomii.kotweaks.utils.TypeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerEntityGetter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
abstract class SnowHeightmapMixin extends Level implements WorldGenLevel, ServerEntityGetter, LevelExtension {
    @Unique
    private final TimedMap<BlockPos, Double> snowHeightMap = new TimedMap<>(100);
    @Override
    public TimedMap<BlockPos, Double> kotweaks$snowHeightMap() {
        return snowHeightMap;
    }

    @Unique
    TypeUtils.LazySupplier<PerlinNoise> noise = TypeUtils.lazyValue(() -> PerlinNoise.create(new XoroshiroRandomSource(MathUtils.u(this.getSeed())), 1, 1));

    @Unique
    private static final double s = 0.18;

    @Unique
    private double noiseAt(BlockPos pos) {
        return snowHeightMap.getOrPut(pos, () -> noise.get().getValue(((double) pos.getX()) * s, ((double) pos.getY()) * s, ((double) pos.getZ()) * s));
    }

    @Shadow
    public abstract long getSeed();

    @Redirect(method = "tickPrecipitation", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private int editHeight(int a, int b, @Local(name = "topPos") BlockPos topPos) {
        var k = Math.ceilDiv(a, 3);

        var i = 0;
        for (BlockPos pos : MiscUtils.facesXZ(topPos)) {
            if (getBlockState(pos).isAir())
                i += 1;
        }
        if (i > 0) i += 1;

        return MathUtils.max(MathUtils.map(noiseAt(topPos.atY(0)), -1, 1, k, a) - i, 1);
    }

    protected SnowHeightmapMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }
}
