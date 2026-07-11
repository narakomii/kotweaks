package narakomii.kotweaks.mixin.entity;

import narakomii.kotweaks.data.entity.CustomEntityDataAccess;
import narakomii.kotweaks.types.EntityDataExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(Entity.class)
abstract class EntityCustomDataMixin implements EntityDataExtension {
    @Shadow
    private CustomData customData;
    @Unique
    private final HashMap<CustomEntityDataAccess.Key<?, ?>, Object> kotweaks$customData = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <V> V kotweaks$get(CustomEntityDataAccess.Key<V, ?> key) {
        return (V) kotweaks$customData.get(key);
    }

    @Override
    public <V> void kotweaks$set(CustomEntityDataAccess.Key<V, ?> key, V value) {
        kotweaks$customData.put(key, value);
    }

    @Override
    public boolean kotweaks$has(CustomEntityDataAccess.Key<?, ?> key) {
        return kotweaks$customData.containsKey(key);
    }

    @Override
    public CompoundTag kotweaks$getDataComponents() {
        CompoundTag output = new CompoundTag();
        kotweaks$customData.forEach((key, value) -> output.put(key.toString(), key.toTag(value)));
        return output;
    }

    @Override
    public void kotweaks$setDataComponents(CompoundTag data) {
        kotweaks$customData.clear();
        data.forEach((stringKey, tag) -> {
            CustomEntityDataAccess.Key<?, Tag> key = CustomEntityDataAccess.key(stringKey);
            kotweaks$customData.put(key, key.toValue(tag));
        });
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V", shift = At.Shift.BEFORE))
    private void saveWithoutId(ValueOutput output, CallbackInfo ci) {
        output.store(CustomEntityDataAccess.ROOT_KEY, CompoundTag.CODEC, kotweaks$getDataComponents());
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V", shift = At.Shift.BEFORE))
    public void load(ValueInput input, CallbackInfo ci) {
        input.read(CustomEntityDataAccess.ROOT_KEY, CompoundTag.CODEC).ifPresent(this::kotweaks$setDataComponents);
    }
}
