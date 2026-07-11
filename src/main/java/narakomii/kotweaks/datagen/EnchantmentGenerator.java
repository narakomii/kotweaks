package narakomii.kotweaks.datagen;

import narakomii.kotweaks.enchantment.ModEnchantments;
import narakomii.kotweaks.types.EnchantmentBuilderExtension;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class EnchantmentGenerator extends FabricDynamicRegistryProvider {
    public EnchantmentGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.ENCHANTMENT));
    }

    @Override
    public @NonNull String getName() {
        return "Enchantments";
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.identifier()));
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        /*ArrayList<Holder<Item>> u = new ArrayList<>();
        u.addAll(context.lookup(Registries.ITEM).getOrThrow(ItemTags.WEAPON_ENCHANTABLE).stream().toList());
        u.addAll(context.lookup(Registries.ITEM).getOrThrow(ItemTags.MINING_ENCHANTABLE).stream().toList());
        u.addAll(context.lookup(Registries.ITEM).getOrThrow(ItemTags.EQUIPPABLE_ENCHANTABLE).stream().toList());
        u.addAll(context.lookup(Registries.ITEM).getOrThrow(ItemTags.BOW_ENCHANTABLE).stream().toList());
        u.addAll(context.lookup(Registries.ITEM).getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE).stream().toList());
        u.addAll(context.lookup(Registries.ITEM).getOrThrow(ItemTags.TRIDENT_ENCHANTABLE).stream().toList());*/

        register(
                context,
                ModEnchantments.MAGNETISM,
                ((EnchantmentBuilderExtension) Enchantment.enchantment(
                        Enchantment.definition(
                                context.lookup(Registries.ITEM).getOrThrow(ItemTags.VANISHING_ENCHANTABLE), // HolderSet.direct(u),
                                context.lookup(Registries.ITEM).getOrThrow(ItemTags.VANISHING_ENCHANTABLE),
                                1,
                                1,
                                Enchantment.dynamicCost(25, 9),
                                Enchantment.dynamicCost(50, 9),
                                8,
                                EquipmentSlotGroup.ANY
                        )
                )).kotweaks$setFallback("Magnetism")
        );
    }
}
