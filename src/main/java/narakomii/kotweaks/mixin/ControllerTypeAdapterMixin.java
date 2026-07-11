package narakomii.kotweaks.mixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import dev.codedsakura.blossom.lib.data.DataController;
import net.minecraft.network.chat.Component;
import net.minecraft.world.attribute.BedRule;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(DataController.class)
abstract class ControllerTypeAdapterMixin {
    @Mutable
    @Final
    @Shadow
    protected static Gson GSON;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void redirectGSON(CallbackInfo ci) {
        GSON = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .registerTypeAdapter(BedRule.class, new BedRuleTypeAdapter())
                //.registerTypeAdapter(Map.class, new RecordMapTypeAdapter(Record.class))
                .create();
    }

    //public static void addTypeAdapter() {}

    private static class BedRuleTypeAdapter extends TypeAdapter<BedRule> {
        @Override
        public void write(JsonWriter out, BedRule value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginObject();
            out.name("canSleep");
            out.jsonValue(GSON.toJson(value.canSleep()));
            out.name("canSetSpawn");
            out.jsonValue(GSON.toJson(value.canSetSpawn()));
            out.name("explodes");
            out.value(value.explodes());
            out.name("errorMessage");
            Optional<Component> message = value.errorMessage();
            if (message.isPresent()) {
                out.value(GSON.toJson(message.get()));
            } else {
                out.nullValue();
            }
            out.endObject();
        }

        @Override
        public BedRule read(JsonReader in) throws IOException {
            in.beginObject();
            BedRule.Rule canSleep = null;
            BedRule.Rule canSetSpawn = null;
            Boolean explodes = null;
            Optional<Component> message = Optional.empty();
            while (in.hasNext()) {
                String name = in.nextName();
                switch (name) {
                    case "canSleep":
                        canSleep = GSON.fromJson(in, BedRule.Rule.class);
                        break;
                    case "canSetSpawn":
                        canSetSpawn = GSON.fromJson(in, BedRule.Rule.class);
                        break;
                    case "explodes":
                        explodes = in.nextBoolean();
                        break;
                    case "errorMessage": {
                        if (in.peek() != JsonToken.NULL) {
                            message = Optional.of(GSON.fromJson(in, Component.class));
                        } else {
                            message = Optional.empty();
                            in.skipValue();
                        }
                    }
                }
            }
            in.endObject();
            if (canSleep == null) {
                throw new IOException("canSleep property is missing");
            }
            if (canSetSpawn == null) {
                throw new IOException("canSetSpawn property is missing");
            }
            if (explodes == null) {
                throw new IOException("explodes property is missing");
            }
            return new BedRule(canSleep, canSetSpawn, explodes, message);
        }
    }

    private static class RecordMapTypeAdapter<V extends Record> extends TypeAdapter<Map<String, V>> {
        final Class<V> recordClass;
        public RecordMapTypeAdapter(Class<V> recordClass) {
            this.recordClass = recordClass;
        }

        @Override
        public void write(JsonWriter out, Map<String, V> value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginObject();
            for  (Map.Entry<String, V> entry : value.entrySet()) {
                out.name(entry.getKey());
                out.value(GSON.toJson(entry.getValue()));
            }
            out.endObject();
        }

        @Override
        public Map<String, V> read(JsonReader in) throws IOException {
            in.beginObject();
            Map<String, V> map = new HashMap<>();
            while (in.hasNext()) {
                String name = in.nextName();
                map.put(name, GSON.fromJson(in, recordClass));
            }
            in.endObject();
            return map;
        }
    }
}
