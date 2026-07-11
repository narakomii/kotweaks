package narakomii.kotweaks.types;

import narakomii.kotweaks.KoTweaks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Optional;

public class CustomItem extends Item {
    public CustomItem(Properties properties) {
        super(properties);
    }

    public static final String NBT_ID = KoTweaks.idString("id");

    public static Identifier getId(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            Optional<String> id = data.copyTag().getString(NBT_ID);
            if (id.isPresent())
                return Identifier.parse(id.get());
        }

        return BuiltInRegistries.ITEM.getKey(stack.getItem());
    }

    public interface Stack {
        ItemStack kotweaks$toFakeItem();
        ItemStack kotweaks$fromFakeItem();

        public static Stack of(ItemStack stack) {
            return (Stack) (Object) stack;
        }
    }
}
