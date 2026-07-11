package narakomii.kotweaks.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import narakomii.kotweaks.KoTweaks;
import narakomii.kotweaks.utils.CommandUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.attribute.BedRule;

import java.io.*;
import java.util.Map;
import java.util.Optional;

public abstract class MapController<K extends Serializable & Comparable<K>, V> {
    protected static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .registerTypeAdapter(BedRule.class, new BedRuleTypeAdapter())
            .create();

    protected abstract String fileName();
    protected abstract Map<K, V> defaultMap();
    protected abstract V defaultEntry();
    protected abstract TypeToken<?> typeToken();
    public void reload(MinecraftServer server) {
        read();
    }

    private File file() {
        return KoTweaks.FOLDER.toPath().resolve(fileName() + ".json").toFile();
    }

    private final Map<K, V> map = defaultMap();

    // https://stackoverflow.com/a/24288021
    @SuppressWarnings("unchecked")
    public void read() {
        try {
             map.putAll(map.getClass().cast(GSON.fromJson(new BufferedReader(new InputStreamReader(new FileInputStream(file()))), typeToken())));
        } catch (FileNotFoundException e) {
            write();
        } catch (ClassCastException e) {
            KoTweaks.LOGGER.error("Error reading config, malformed JSON? [{}]", file());
        }
    }

    public void write() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file()));
            GSON.toJson(map, writer);
            writer.close();
        } catch (IOException e) {
            KoTweaks.LOGGER.error(CommandUtils.formatError(e));
        }
    }

    //TODO rename this
    public Map<K, V> map() {
        return map;
    }

    public boolean hasKey(K key) {
        return map.containsKey(key);
    }

    public V get(K key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            V v = defaultEntry();
            map.put(key, v);
            return v;
        }
    }

    /*public void set(K key, V value) {
        map.put(key, value);
        write();
    }*/

    public static class BedRuleTypeAdapter extends TypeAdapter<BedRule> {
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
}

/*public abstract class MapController<K extends Comparable<K>, V extends Record> extends ListDataController<MapController.Entry<K, V>> {
    protected abstract V defaultRecord();
    protected abstract Map<K, V> defaultMap();

    public V get(K key) {

    }

    @Override

    protected record Entry<K, V>(K key, V value) {
    }
}*/

//TODO: either use a ListDataController<Entry<V>> or add Map type reader thing to the Gson instance
/*public abstract class MapController<K, V> extends DataController<Map<K, V>> {
    protected abstract V defaultRecord();
    protected abstract String filename();

    @Override
    public String getFilename() {
        return KoTweaks.MOD_ID + "$" + filename();
    }

    public V get(K key) {
        Map<K, V> map = read();

        if (map == null) {
            //return defaultRecord();
            map = defaultData();
            /*Map<K, V> newMap = defaultData();
            if (newMap.containsKey(key)) {
                return newMap.get(key);
            } else {
                return defaultRecord();
            }*-/
        }

        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return defaultRecord();
        }
    }

    public void set(K key, V value) {
        Map<K, V> map = read();
        map.put(key, value);
        write();
    }
}*/
