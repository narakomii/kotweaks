package narakomii.kotweaks.game;

import narakomii.kotweaks.KoTweaks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ModEnchantments {
    private ModEnchantments() {}
    public static void init() {}

    public static final ResourceKey<Enchantment> MAGNETISM = key("magnetism");

    private static ResourceKey<Enchantment> key(String path) {
        Identifier id = Identifier.fromNamespaceAndPath(KoTweaks.MOD_ID, path);
        return ResourceKey.create(Registries.ENCHANTMENT, id);
    }
}
