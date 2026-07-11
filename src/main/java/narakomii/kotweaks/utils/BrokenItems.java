package narakomii.kotweaks.utils;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public final class BrokenItems {
    private BrokenItems() {}

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

    public static ItemStack override(ItemStack item) {
        if (item.isBroken()) {
            final ItemStack newItem = item.copy();

            BrokenItems.IGNORED_COMPONENTS.forEach(component -> newItem.set(component, null));

            return newItem;
        }

        return item;
    }
}
