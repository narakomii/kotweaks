package narakomii.kotweaks.mixin.item.broken;

import narakomii.kotweaks.utils.BrokenItems;
import narakomii.kotweaks.utils.ItemUtils;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(targets = "net.minecraft.server.level.ServerPlayer$1")
abstract class BrokenComponentSyncMixin {
    /*@Redirect(method = "sendInitialData", at = @At(value = "NEW", target = "Lnet/minecraft/network/protocol/game/ClientboundContainerSetContentPacket;"))
    private ClientboundContainerSetContentPacket sendInitialData(int containerId, int stateId, List<ItemStack> items, ItemStack carriedItem) {
        items = items.stream().map(ItemUtils::override).toList();

        return new ClientboundContainerSetContentPacket(containerId, stateId, items, ItemUtils.override(carriedItem));
    }

    @Redirect(method = "sendSlotChange", at = @At(value = "NEW", target = "Lnet/minecraft/network/protocol/game/ClientboundContainerSetSlotPacket;"))
    private ClientboundContainerSetSlotPacket sendSlotChange(int containerId, int stateId, int slot, ItemStack itemStack) {
        return new ClientboundContainerSetSlotPacket(containerId, stateId, slot, ItemUtils.override(itemStack));
    }

    @Redirect(method = "sendCarriedChange", at = @At(value = "NEW", target = "Lnet/minecraft/network/protocol/game/ClientboundSetCursorItemPacket;"))
    private ClientboundSetCursorItemPacket sendCarriedChange(ItemStack contents) {
        return new ClientboundSetCursorItemPacket(ItemUtils.override(contents));
    }*/
}
