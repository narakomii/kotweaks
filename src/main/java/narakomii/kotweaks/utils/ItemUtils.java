package narakomii.kotweaks.utils;

import narakomii.kotweaks.types.CustomItem;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.*;

public final class ItemUtils {
    private ItemUtils() {}

    public static final List<DataComponentType<?>> DATA_COMPONENT_TYPES = new ArrayList<>();
    static {
        Arrays.stream(DataComponents.class.getFields()).forEach(field -> {
            try {
                if (field.get(null) instanceof DataComponentType type) {
                    DATA_COMPONENT_TYPES.add(type);
                }
            } catch (IllegalAccessException e) {
                //
            }
        });

        /*.filter(field -> {
            try {
                return field.get(null) instanceof DataComponentType<?>;
            } catch (IllegalAccessException | NullPointerException e) {
                return false;
            }
        }).map(field -> {
            try {
                return (DataComponentType<?>) field.get(null);
            } catch (ClassCastException | IllegalAccessException e) {
                throw new RuntimeException("This error should be impossible to reach", e);
            }
        }).toList();*/
    }

    public static DataComponentPatch.Builder empty(DataComponentPatch.Builder builder) {
        DATA_COMPONENT_TYPES.forEach(builder::remove);
        return builder;
    }

    public static void give(Inventory inventory, ItemStack stack, int count) {
        int maxStackSize = stack.getMaxStackSize();
        int remaining = count;
        while (remaining > 0) {
            int size = Math.min(maxStackSize, remaining);
            remaining -= size;
            inventory.add(stack.copyWithCount(size));
        }
    }

    public static void giveAsDrop(Entity entity, ItemStack stack) {
        ServerLevel level = (ServerLevel) entity.level();
        ItemEntity item = new ItemEntity(level, entity.position().x, entity.position().y + 0.5, entity.position().z, stack, 0, 0.2, 0);
        item.setNoPickUpDelay();
        level.addFreshEntity(item);
        if (entity instanceof Player player) {
            item.playerTouch(player);
        }
    }

    public static <T> DataComponentMap with(DataComponentMap components, DataComponentType<T> type, T value) {
        return DataComponentMap.builder()
                .addAll(components)
                .set(type, value)
                .build();
    }

    public static DataComponentMap merge(DataComponentMap components, DataComponentType<CustomData> type, CompoundTag value) {
        CustomData c = components.get(type);

        return DataComponentMap.builder()
                .addAll(components)
                .set(
                        type,
                        c != null ? CustomData.of(c.copyTag().merge(value)) : CustomData.of(value)
                )
                .build();
    }

    public static Identifier getId(ItemStack stack) {
        return stack.typeHolder().unwrapKey().orElseThrow().identifier();
    }

    public static ItemStack toFakeItem(ItemStack stack) {
        return ((CustomItem.Stack) (Object) stack).kotweaks$toFakeItem();
    }

    public static ItemStack fromFakeItem(ItemStack stack) {
        return ((CustomItem.Stack) (Object) stack).kotweaks$fromFakeItem();
    }
}
