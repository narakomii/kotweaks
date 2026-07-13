package narakomii.kotweaks.game;

import narakomii.kotweaks.KoTweaks;
import narakomii.kotweaks.types.CustomItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public class ModItems {
    public static final CustomItem TEST = register("test");
    public static final CustomItem METEOR = register("meteor", "minecraft:magma_block");
    public static final CustomItem ROCK = register("rock", "minecraft:stone");

    public static void init() {}

    private static CustomItem register(String id) {
        return register(id, "minecraft:barrier");
    }
    private static CustomItem register(String id, String model) {
        return register(id, model, StringUtils.capitalize(id), new Item.Properties());
    }
    private static CustomItem register(String id, String model, String name, Item.Properties properties) {
        return register(id, p -> new CustomItem(name, model, p), properties);
    }
    private static <T extends Item> T register(String id, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, KoTweaks.id(id));
        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }
}
