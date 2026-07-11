package narakomii.kotweaks.mixin.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;

@Mixin(ShapelessRecipe.class)
abstract class ShapelessRecipeMixin {
    @Shadow
    @Final
    private List<Ingredient> ingredients;

    /**
     * @author narakomii
     * @reason use edited Ingredient.test(ItemStack) instead of incomprehensible StackedItemContents/StackedContents<Holder<Item>>/StackedContents<Holder<Item>>.RecipePicker/Reference2IntOpenHashMap<Holder<Item>> matching
     */
    @Overwrite
    public boolean matches(final CraftingInput input, final Level level) {
        if (input.ingredientCount() != ingredients.size()) return false;

        if (input.size() == 1) {
            if (ingredients.size() == 1) {
                return ingredients.getFirst().test(input.getItem(0));
            }
        }

        Set<Ingredient> ingredientSet = new HashSet<>(ingredients);
        LinkedHashMap<Ingredient, Integer> ingredientMap = new LinkedHashMap<>();
        ingredients.forEach(i -> ingredientMap.put(i, 0));

        for (ItemStack inputItem : input.items()) {
            if (inputItem.isEmpty()) continue;

            Ingredient lowest = null;
            int lowestCount = Integer.MAX_VALUE;

            for (Ingredient ingredient : ingredientMap.keySet()) {
                if (ingredient.test(inputItem)) {
                    if (lowest == null || ingredientMap.get(ingredient) < lowestCount) {
                        lowest = ingredient;
                        lowestCount = ingredientMap.get(lowest);
                    }
                }
            }

            if (lowest == null) return false;
            ingredientMap.put(lowest, lowestCount + 1);
            ingredientSet.remove(lowest);
        }

        return ingredientSet.isEmpty();
    }


}
