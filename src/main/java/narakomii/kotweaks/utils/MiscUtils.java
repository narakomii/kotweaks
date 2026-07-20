package narakomii.kotweaks.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.List;

public final class MiscUtils {
    private MiscUtils() {}

    @SafeVarargs
    public static <V> V pick(RandomSource random, V... values) {
        return values[random.nextInt(values.length)];
    }
    public static <V> V pick(RandomSource random, List<V> values) {
        return values.get(random.nextInt(values.size()));
    }

    public static List<BlockPos> faces(BlockPos pos) {
        return List.of(pos.above(), pos.below(), pos.north(), pos.south(), pos.east(), pos.west());
    }
    public static List<BlockPos> facesNotUp(BlockPos pos) {
        return List.of(pos.below(), pos.north(), pos.south(), pos.east(), pos.west());
    }
    public static List<BlockPos> facesXZ(BlockPos pos) {
        return List.of(pos.north(), pos.south(), pos.east(), pos.west());
    }
}
