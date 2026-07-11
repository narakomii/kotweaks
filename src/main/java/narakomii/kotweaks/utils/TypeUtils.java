package narakomii.kotweaks.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class TypeUtils {
    private TypeUtils() {}

    public static <T> Holder.Reference<T> holder(Registry<T> registry, String identifier) {
        return registry.get(Identifier.parse(identifier)).orElseThrow();
    }

    public static <T> Holder.Reference<T> holder(RegistryAccess access, ResourceKey<Registry<T>> registry, String identifier) {
        return access.getOrThrow(ResourceKey.create(registry, Identifier.parse(identifier)));
    }

    // https://stackoverflow.com/a/29138089
    public static <T> LazySupplier<@NonNull T> lazyValue(Supplier<@NonNull T> supplier) {
        if (supplier instanceof LazySupplier<T> lazy)
            return lazy;

        return new LazySupplier<>(supplier);
    }

    public static class LazySupplier<T> implements Supplier<T> {
        private T value;
        private final Supplier<T> supplier;
        private LazySupplier(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        public Supplier<T> getSupplier() {
            return supplier;
        }

        public <R> LazySupplier<R> map(Function<T, R> mapper) {
            return new LazySupplier<>(() -> mapper.apply(supplier.get()));
        }

        @Override
        public T get() {
            if (value == null) value = supplier.get();
            return value;
        }
    }

    public static <A, C> Optional<C> checkedApply(A a, Function<A, Optional<C>> f) {
        return a != null ? f.apply(a) : Optional.empty();
    }

    public static <A, C> C checkedApply(A a, Function<A, C> f, C fallback) {
        return a != null ? f.apply(a) : fallback;
    }
}
