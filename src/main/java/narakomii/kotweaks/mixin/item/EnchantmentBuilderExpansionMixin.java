package narakomii.kotweaks.mixin.item;

import narakomii.kotweaks.types.EnchantmentBuilderExtension;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.*;

@Mixin(Enchantment.Builder.class)
abstract class EnchantmentBuilderExpansionMixin implements EnchantmentBuilderExtension {
    @Shadow
    @Final
    private Enchantment.EnchantmentDefinition definition;

    @Shadow
    private HolderSet<Enchantment> exclusiveSet;

    @Shadow
    @Final
    private DataComponentMap.Builder effectMapBuilder;

    @Unique
    private String kotweaks$fallback = null;

    @Unique
    public Enchantment.Builder kotweaks$setFallback(String fallback) {
        kotweaks$fallback = fallback;
        return (Enchantment.Builder) (Object) this;
    }

    /**
     * @author narakomii
     * @reason add fallback description to translatable component
     */
    @Overwrite
    public Enchantment build(final Identifier descriptionKey) {
        return new Enchantment(Component.translatableWithFallback(Util.makeDescriptionId("enchantment", descriptionKey), kotweaks$fallback), this.definition, this.exclusiveSet, this.effectMapBuilder.build());
    }
}
