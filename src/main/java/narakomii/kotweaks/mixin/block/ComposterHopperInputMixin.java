package narakomii.kotweaks.mixin.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComposterBlock;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ComposterBlock.InputContainer.class)
abstract class ComposterHopperInputMixin {
    @Shadow
    private boolean changed;

    /**
     * @author narakomii
     * @reason Allows hoppers to access slots to input items from any side
     */
    @Overwrite
    public int[] getSlotsForFace(final Direction side) {
        return new int[]{0};
    }

    /**
     * @author narakomii
     * @reason Allows hoppers to input items through any side
     */
    @Overwrite
    public boolean canPlaceItemThroughFace(final int slot, final ItemStack stack, final @Nullable Direction direction) {
        return !changed && ComposterBlock.COMPOSTABLES.containsKey(stack.getItem());
    }
}
