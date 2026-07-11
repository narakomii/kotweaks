package narakomii.kotweaks.mixin.item.broken;

import narakomii.kotweaks.utils.BrokenItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Inventory.class)
abstract class BrokenInventoryMixin {
    /*@Redirect(method = "createInventoryUpdatePacket", at = @At(value = "NEW", target = "Lnet/minecraft/network/protocol/game/ClientboundSetPlayerInventoryPacket;"))
    private ClientboundSetPlayerInventoryPacket createInventoryUpdatePacket(int slot, ItemStack contents) {
        return new ClientboundSetPlayerInventoryPacket(slot, BrokenItems.override(contents));
    }*/
}
