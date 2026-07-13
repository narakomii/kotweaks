package narakomii.kotweaks.storage.world;

import com.google.gson.reflect.TypeToken;
import narakomii.kotweaks.types.MapController;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.valueproviders.ClampedInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.NonNull;

import java.io.Serializable;
import java.util.*;

public class CropExpController extends MapController<String, CropExpController.CropExpData> {
    @Override
    public String fileName() {
        return "crop-exp";
    }

    @Override
    public Map<String, CropExpData> defaultMap() {
        Map<String, CropExpData> map = new HashMap<>();
        map.put(Objects.requireNonNull(Blocks.WHEAT.properties().blockId()).identifier().toString(), new CropExpData(-1, 3));
        map.put(Objects.requireNonNull(Blocks.CARROTS.properties().blockId()).identifier().toString(), new CropExpData(-1, 3));
        map.put(Objects.requireNonNull(Blocks.POTATOES.properties().blockId()).identifier().toString(), new CropExpData(-1, 3));
        map.put(Objects.requireNonNull(Blocks.BEETROOTS.properties().blockId()).identifier().toString(), new CropExpData(-1, 3));
        map.put(Objects.requireNonNull(Blocks.COCOA.properties().blockId()).identifier().toString(), new CropExpData(-1, 3));
        map.put(Objects.requireNonNull(Blocks.NETHER_WART.properties().blockId()).identifier().toString(), new CropExpData(-1, 3));
        return map;
    }

    @Override
    protected @NonNull CropExpData defaultEntry() {
        return CropExpData.DEFAULT();
    }

    public Optional<IntProvider> getProvider(String key) {
        Map<String, CropExpData> map = map();
        CropExpData options = map.get(key);

        if (!map.containsKey(key) || options.maxExp <= 0) {
            return Optional.empty();
        }

        return Optional.of(ClampedInt.of(UniformInt.of(options.minExp, options.maxExp), 0, Integer.MAX_VALUE));
    }

    @Override
    protected TypeToken<?> typeToken() {
        return new TypeToken<HashMap<String, CropExpData>>() {};
    }

    @Override
    public void reload(MinecraftServer server) {
        super.reload(server);
    }

    /*public record CropExpData(int minExp, int maxExp) {
        public static final CropExpData DEFAULT = new CropExpData(0, 0);
    }*/

    public static class CropExpData implements Serializable {
        public static CropExpData DEFAULT() { return new CropExpData(0, 0); }

        public int minExp;
        public int maxExp;

        public CropExpData(int minExp, int maxExp) {
            this.minExp = minExp;
            this.maxExp = maxExp;
        }
    }
}
