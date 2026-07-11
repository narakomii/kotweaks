package narakomii.kotweaks.data.entity;

import narakomii.kotweaks.KoTweaks;
import narakomii.kotweaks.types.EntityDataExtension;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class CustomEntityDataAccess {
    public static final String ROOT_KEY = KoTweaks.MOD_ID + ":data";

    public static final class Key<V, T extends Tag> implements Serializable, Comparable<Key<?, ?>> {
        private static final Map<String, Key<?, ?>> keys = new HashMap<>();

        public static final Key<String, StringTag> Type = new Key<>("type", () -> "", StringTag::valueOf, StringTag::asString);
        public static final Key<String, StringTag> ForcedTarget = new Key<>("target", () -> "", StringTag::valueOf, StringTag::asString);
        public static final Key<int[], IntArrayTag> Timers = new Key<>("timers", () -> new int[]{0}, IntArrayTag::new, IntArrayTag::asIntArray);

        private final String key;
        private final Supplier<V> fallback;
        private final Function<V, T> toTag;
        private final Function<T, Optional<V>> toValue;
        private Key(String key, Supplier<V> defaultSupplier, Function<V, T> toTag, Function<T, Optional<V>> toValue) {
            this.key = key;
            this.fallback = defaultSupplier;
            this.toTag = toTag;
            this.toValue = toValue;
            keys.put(key, this);
        }

        public V getDefault() {
            return fallback.get();
        }

        @SuppressWarnings("unchecked")
        public T toTag(Object value) {
            return toTag.apply((V) value);
        }

        public V toValue(T tag) {
            return toValue.apply(tag).orElse(null);
        }

        @Override
        public String toString() {
            return key;
        }

        @Override
        public int compareTo(CustomEntityDataAccess.@NonNull Key o) {
            return this.key.compareTo(o.key);
        }
    }

    public static <V, T extends Tag> @Nullable V get(Entity entity, Key<V, T> key) {
        if (entity instanceof EntityDataExtension holder) {
            return holder.kotweaks$get(key);
        }

        return null;
    }

    public static <V, T extends Tag> V getOrSet(Entity entity, Key<V, T> key) {
        if (entity instanceof EntityDataExtension holder) {
            if (holder.kotweaks$has(key)) {
                return holder.kotweaks$get(key);
            } else {
                V value = key.getDefault();
                holder.kotweaks$set(key, value);
                return value;
            }
        }

        return null;
    }

    public static <V, T extends Tag> void setTag(Entity entity, Key<V, T> key, T tag) {
        if (entity instanceof EntityDataExtension holder) {
            holder.kotweaks$set(key, key.toValue(tag));
        }
    }

    public static <V, T extends Tag> void set(Entity entity, Key<V, T> key, V value) {
        if (entity instanceof EntityDataExtension holder) {
            holder.kotweaks$set(key, value);
        }
    }

    public static boolean has(Entity entity, Key<?, ?> key) {
        if (entity instanceof EntityDataExtension holder) {
            return holder.kotweaks$has(key);
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public static Key<Object, Tag> key(String key) {
        return (Key<Object, Tag>) Key.keys.get(key);
    }
}
