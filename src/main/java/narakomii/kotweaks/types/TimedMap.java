package narakomii.kotweaks.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TimedMap<K, V> {
    private final Map<K, TimedValue<V>> values = new HashMap<>();
    private final int maxAge;

    public TimedMap(int maxAge) {
        this.maxAge = maxAge;
    }

    public void tick() {
        // ignore this warning; iterating the map directly and removing values will throw a ConcurrentModificationException
        new ArrayList<>(values.entrySet()).forEach(entry -> {
            entry.getValue().age += 1;
            if (entry.getValue().age > maxAge)
                values.remove(entry.getKey());
        });
    }

    public void put(K key, V value) {
        values.put(key, new TimedValue<>(value));
    }

    public V get(K key) {
        if (values.containsKey(key)) {
            var val = values.get(key);
            val.age = 0;
            return val.value;
        }

        return null;
    }

    public V getOrPut(K key, Supplier<V> supplier) {
        if (values.containsKey(key)) {
            var val = values.get(key);
            val.age = 0;
            return val.value;
        }

        var val = supplier.get();
        put(key, val);
        return val;
    }

    public void reset(K key) {
        if (values.containsKey(key))
            values.get(key).age = 0;
    }

    static class TimedValue<V> {
        private int age = 0;
        private final V value;

        public V value() {
            return value;
        }

        public int age() {
            return age;
        }

        public TimedValue(V value) {
            this.value = value;
        }
    }
}
