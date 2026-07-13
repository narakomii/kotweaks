package narakomii.kotweaks.types;

import narakomii.kotweaks.storage.entity.CustomEntityDataAccess;
import net.minecraft.nbt.CompoundTag;

public interface EntityDataExtension {
    <V> V kotweaks$get(CustomEntityDataAccess.Key<V, ?> key);
    <V> void kotweaks$set(CustomEntityDataAccess.Key<V, ?> key, V value);
    boolean kotweaks$has(CustomEntityDataAccess.Key<?, ?> key);

    CompoundTag kotweaks$getDataComponents();
    void kotweaks$setDataComponents(CompoundTag data);
}
