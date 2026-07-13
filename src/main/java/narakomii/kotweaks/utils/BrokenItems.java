package narakomii.kotweaks.utils;

import narakomii.kotweaks.KoTweaks;
import narakomii.kotweaks.types.CustomItem;
import net.minecraft.core.component.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.TagValueInput;

import java.util.Set;

public final class BrokenItems {
    private BrokenItems() {}

    //TODO make sure dispensers can't use broken items like shears

    public static final Set<DataComponentType<?>> IGNORED_COMPONENTS = Set.of(new DataComponentType<?>[]{
            DataComponents.ATTRIBUTE_MODIFIERS,
            DataComponents.BLOCKS_ATTACKS,
            DataComponents.GLIDER,
            DataComponents.KINETIC_WEAPON,
            DataComponents.TOOL,
            DataComponents.WEAPON,
            DataComponents.PIERCING_WEAPON,
            DataComponents.ATTACK_RANGE
    });

    public static ItemStack toClientItem(ItemStack item) {
        if (!item.isBroken())
            return item;

        var ignored = DataComponentMap.builder();
        var builder = DataComponentPatch.builder();
        for (DataComponentType<?> type : BrokenItems.IGNORED_COMPONENTS) {
            var baseVal = item.item.components().get(type);
            var inputVal = item.getComponents().get(type);

            if (!ItemUtils.componentsEqual((DataComponentType<Object>) type, baseVal, inputVal))
                ignored.set((DataComponentType<Object>) type, inputVal);

            builder.remove(type);
        }
        ItemUtils.merge(builder, CustomItem.IGNORED_DATA_ID, DataComponentMap.CODEC.encode(ignored.build(), KoTweaks.registryLookup.createSerializationContext(NbtOps.INSTANCE), new CompoundTag()).getOrThrow());

        ItemStack newItem = item.copy();
        newItem.applyComponents(builder.build());
        return newItem;
    }

    public static ItemStack fromClientItem(ItemStack item) {
        if (
                item.item == null
                || !item.isBroken()
                || !(item.get(DataComponents.CUSTOM_DATA) instanceof CustomData customData)
                || !(customData.copyTag().get(CustomItem.IGNORED_DATA_ID.toString()) instanceof CompoundTag ignoredTag)
        )
            return item;

        var ignored = DataComponentMap.CODEC.parse(KoTweaks.registryLookup.createSerializationContext(NbtOps.INSTANCE), ignoredTag).getOrThrow();

        var builder = DataComponentPatch.builder();
        for (DataComponentType<?> type : BrokenItems.IGNORED_COMPONENTS) {
            var baseVal = item.item.components().get(type);
            var inputVal = ignored.get(type);

            if (inputVal != null) {
                if (baseVal == null || !ItemUtils.componentsEqual((DataComponentType<Object>) type, baseVal, inputVal)) {
                    builder.set((DataComponentType<Object>) type, inputVal);
                }
            }
        }

        var newCustomData = customData.copyTag();
        newCustomData.remove(CustomItem.IGNORED_DATA_ID.toString());
        if (!newCustomData.isEmpty())
            builder.set(DataComponents.CUSTOM_DATA, CustomData.of(newCustomData));

        var newItem = item.copy();
        newItem.applyComponents(builder.build());
        return newItem;
    }
}
