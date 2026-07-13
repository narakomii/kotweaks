package narakomii.kotweaks.storage.world;

import com.google.gson.reflect.TypeToken;
import narakomii.kotweaks.types.MapController;
import narakomii.kotweaks.mixin.accessors.ServerLevelAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DimensionBedController extends MapController<String, DimensionBedController.DimensionBedOptionData> {
    @Override
    public String fileName() {
        return "beds";
    }

    @Override
    public Map<String, DimensionBedOptionData> defaultMap() {
        Map<String, DimensionBedOptionData> map = new HashMap<>(4);
        map.put(Level.END.identifier().toString(), new DimensionBedOptionData(new BedRule(BedRule.Rule.ALWAYS, BedRule.Rule.ALWAYS, false, Optional.empty()), true));
        return map;
    }

    @Override
    protected @NonNull DimensionBedOptionData defaultEntry() {
        return DimensionBedOptionData.DEFAULT();
    }

    @Override
    protected TypeToken<?> typeToken() {
        return new TypeToken<HashMap<String, DimensionBedOptionData>>() {};
    }

    @Override
    public void reload(MinecraftServer server) {
        super.reload(server);

        for (Level level : server.getAllLevels()) {
            if (level instanceof ServerLevel serverLevel) {
                BedRule bedRule = get(serverLevel.dimension().identifier().toString()).bedRule;
                if (bedRule != null) {
                    ((ServerLevelAccessor) serverLevel).kotweaks$setEnvironmentAttributes(
                            EnvironmentAttributeSystem.builder()
                                    .addDefaultLayers(level)
                                    .addConstantLayer(
                                            EnvironmentAttributeMap.builder()
                                                    .set(EnvironmentAttributes.BED_RULE, bedRule)
                                                    .build()
                                    ).build()
                    );
                } else {
                    ((ServerLevelAccessor) serverLevel).kotweaks$setEnvironmentAttributes(
                            EnvironmentAttributeSystem.builder()
                                    .addDefaultLayers(level)
                                    .build()
                    );
                }
            }
        }
    }

    /*public record DimensionBedOptionData(@Nullable BedRule bedRule, boolean cannotPassNight) implements Serializable {
        public static final DimensionBedOptionData DEFAULT = new DimensionBedOptionData(null, false);
    }*/

    public static class DimensionBedOptionData implements Serializable {
        public static DimensionBedOptionData DEFAULT() { return new DimensionBedOptionData(null, false); }

        public @Nullable BedRule bedRule;
        public boolean cannotPassNight;

        public DimensionBedOptionData(@Nullable BedRule bedRule, boolean cannotPassNight) {
            this.bedRule = bedRule;
            this.cannotPassNight = cannotPassNight;
        }
    }
}
