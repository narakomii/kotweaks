package narakomii.kotweaks.mixin.item;

import narakomii.kotweaks.KoTweaks;
import narakomii.kotweaks.utils.ItemUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.item.ItemStackTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HoverEvent.ShowItem.class)
abstract class HoverItemMaskMixin {
    @ModifyVariable(method = "<init>", at = @At("CTOR_HEAD"), argsOnly = true, name = "item")
    private ItemStackTemplate editItem(ItemStackTemplate item) {
        if (item.item().unwrapKey().orElseThrow().identifier().getNamespace().equals(KoTweaks.MOD_ID)) {
            return ItemStackTemplate.fromNonEmptyStack(ItemUtils.toClientItem(item.create()));
        }

        return item;
    }
}
