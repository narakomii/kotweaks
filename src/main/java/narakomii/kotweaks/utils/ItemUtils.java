package narakomii.kotweaks.utils;

import narakomii.kotweaks.types.CustomItem;
import net.minecraft.core.component.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
                if (field.get(null) instanceof DataComponentType type)
                    DATA_COMPONENT_TYPES.add(type);
            } catch (IllegalAccessException e) {}
        });
    }

    public static <T> boolean componentsEqual(DataComponentType<T> type, T a, T b) {
        return CommandUtils.formatNbt(type, a).equals(CommandUtils.formatNbt(type, b));
    }
    public static boolean componentsEqual(TypedDataComponent<?> a, TypedDataComponent<?> b) {
        return componentsEqual(a, b, false);
    }
    public static boolean componentsEqual(TypedDataComponent<?> a, TypedDataComponent<?> b, boolean defaultValue) {
        if (a == null || b == null || a.value() == null || b.value() == null)
            return defaultValue;

        return a.type() == b.type() && CommandUtils.formatNbt(a).equals(CommandUtils.formatNbt(b));
    }
    public static boolean componentsNotEqual(TypedDataComponent<?> a, TypedDataComponent<?> b) {
        return componentsNotEqual(a, b, false);
    }
    public static boolean componentsNotEqual(TypedDataComponent<?> a, TypedDataComponent<?> b, boolean defaultValue) {
        if (a == null || b == null || a.value() == null || b.value() == null)
            return defaultValue;

        return !(a.type() == b.type() && CommandUtils.formatNbt(a).equals(CommandUtils.formatNbt(b)));
    }
    public static boolean componentsEqual(TypedDataComponent<?> a, Object b) {
        if (a == null || b == null)
            return true;

        return CommandUtils.formatNbt(a).equals(CommandUtils.formatNbt(a.type(), b));
    }

    public static DataComponentPatch.Builder emptyPatch(DataComponentPatch.Builder builder) {
        DATA_COMPONENT_TYPES.forEach(builder::remove);
        return builder;
    }
    public static DataComponentPatch.Builder putPatch(DataComponentPatch.Builder builder, DataComponentPatch other) {
        other.entrySet().forEach(entry -> {
            if (entry.getValue() == null) return;
            var val = entry.getValue().orElse(null);
            if (val == null) return;
            builder.set((DataComponentType<Object>) entry.getKey(), val);
        });

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

    public static DataComponentPatch.Builder merge(DataComponentPatch.Builder builder, Identifier id, Tag value) {
        var c = builder.build().get(DataComponentMap.EMPTY, DataComponents.CUSTOM_DATA);
        var v = new CompoundTag();
        v.put(id.toString(), value);
        return builder.set(
                DataComponents.CUSTOM_DATA,
                c != null ? CustomData.of(c.copyTag().merge(v)) : CustomData.of(v)
        );
    }

    public static Identifier getId(ItemStack stack) {
        return stack.typeHolder().unwrapKey().orElseThrow().identifier();
    }

    public static ItemStack toClientItem(ItemStack stack) {
        return BrokenItems.toClientItem(toFakeItem(stack));
    }
    public static ItemStack fromClientItem(ItemStack stack) {
        return fromFakeItem(BrokenItems.fromClientItem(stack));
    }

    public static ItemStack toFakeItem(ItemStack stack) {
        return CustomItem.toFakeItem(stack);
    }
    public static ItemStack fromFakeItem(ItemStack stack) {
        return CustomItem.fromFakeItem(stack);
    }
}
