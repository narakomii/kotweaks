package narakomii.kotweaks.mixin.network;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import narakomii.kotweaks.utils.ItemUtils;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ServerEntity.class)
abstract class S2CEntityItemMaskMixin {
    @ModifyExpressionValue(method = "sendDirtyEntityData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;packDirty()Ljava/util/List;"))
    private @Nullable List<SynchedEntityData.DataValue<?>> modifyEntityData(@Nullable List<SynchedEntityData.DataValue<?>> original) {
        return original != null ? original.stream().map(v -> {
            if (v.value() instanceof ItemStack item)
                return new SynchedEntityData.DataValue<>(v.id(), (EntityDataSerializer<ItemStack>) v.serializer(), ItemUtils.toClientItem(item));

            return v;
        }).toList() : null;
    }
}
