package narakomii.kotweaks.utils;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

public final class MathUtils {
    private MathUtils() {}

    public static double sqr(double x) {
        return x * x;
    }

    public static boolean xzCloserThan(Vec3 a, Vec3 b, double maxDistance) {
        return sqr(a.x - b.x) + sqr(a.z - b.z) < maxDistance * maxDistance;
    }

    public static boolean inChunkRange(ChunkPos a, ChunkPos b, int maxDistance) {
        return Math.abs(a.x() - b.x()) < maxDistance
                && Math.abs(a.z() - b.z()) < maxDistance;
    }

    public static int min(int first, int... values) {
        var min = first;
        for (int value : values) {
            if (value < min)
                min = value;
        }

        return min;
    }

    public static int max(int first, int... values) {
        var max = first;
        for (int value : values) {
            if (value > max)
                max = value;
        }

        return max;
    }

    public static int map(double t, double inMin, double inMax, int outMin, int outMax) {
        var slope = (((double) outMax) - ((double) outMin)) / (inMax - inMin);
        return (int) Math.round(outMin + slope * (t - inMin));
    }

    public static long u(long k) { // random bullshit, go!!!!!!
        long o = k & (~8762155898184219513L);
        o ^= Long.rotateRight(k & 8762155898184219513L, 23);
        return o;
    }
}
