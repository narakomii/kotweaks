package narakomii.kotweaks.utils;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

public final class MathUtils {
    private MathUtils() {}

    private double sqr(double x) {
        return x * x;
    }

    private boolean xzCloserThan(Vec3 a, Vec3 b, double maxDistance) {
        return sqr(a.x - b.x) + sqr(a.z - b.z) < maxDistance * maxDistance;
    }

    private boolean inChunkRange(ChunkPos a, ChunkPos b, int maxDistance) {
        return Math.abs(a.x() - b.x()) < maxDistance
                && Math.abs(a.z() - b.z()) < maxDistance;
    }
}
