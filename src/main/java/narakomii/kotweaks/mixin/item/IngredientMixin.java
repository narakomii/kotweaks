package narakomii.kotweaks.mixin.item;

import narakomii.kotweaks.utils.ItemUtils;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Ingredient.class)
abstract class IngredientMixin {
    @Shadow
    @Final
    private HolderSet<Item> values;

    /**
     * @author narakomii
     * @reason take custom item identifiers into account in recipe matching
     */
    @Overwrite
    public boolean test(ItemStack stack) {
        Identifier id = ItemUtils.getId(stack);
        return this.values.stream().anyMatch(item -> item.is(id));
    }
}
