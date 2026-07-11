package narakomii.kotweaks.item;

import narakomii.kotweaks.KoTweaks;
import narakomii.kotweaks.types.CustomItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
    public static final CustomItem TEST = register("test");
    public static final CustomItem METEOR = register("meteor");
    public static final CustomItem ROCK = register("rock");

    public static void init() {}

    private static CustomItem register(String name) {
        return register(name, new Item.Properties());
    }
    private static CustomItem register(String name, Item.Properties properties) {
        return register(name, CustomItem::new, properties);
    }
    private static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, KoTweaks.id(name));
        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }
}
