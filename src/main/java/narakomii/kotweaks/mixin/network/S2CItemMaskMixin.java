package narakomii.kotweaks.mixin.network;

import narakomii.kotweaks.utils.ItemUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Connection.class)
abstract class S2CItemMaskMixin {
    //TODO make sure attributes like reach are prevented at the server, not just the client (test by equipping broken item with extra reach attributes, enabling meteor range module, and using the item)
    @ModifyVariable(method = "sendPacket", at = @At("HEAD"), argsOnly = true, name = "packet")
    private Packet<?> hi(Packet<?> p) {
        if (p instanceof ClientboundContainerSetContentPacket packet) {
            return new ClientboundContainerSetContentPacket(packet.containerId(), packet.stateId(), packet.items().stream().map(ItemUtils::toClientItem).toList(), ItemUtils.toClientItem(packet.carriedItem()));
        } else if (p instanceof ClientboundSetPlayerInventoryPacket packet) {
            return new ClientboundSetPlayerInventoryPacket(packet.slot(), ItemUtils.toClientItem(packet.contents()));
        } else if (p instanceof ClientboundSetCursorItemPacket packet) {
            return new ClientboundSetCursorItemPacket(ItemUtils.toClientItem(packet.contents()));
        } else if (p instanceof ClientboundContainerSetSlotPacket packet) {
            return new ClientboundContainerSetSlotPacket(packet.getContainerId(), packet.getStateId(), packet.getSlot(), ItemUtils.toClientItem(packet.getItem()));
        } else if (p instanceof ClientboundSetEquipmentPacket packet) {
            return new ClientboundSetEquipmentPacket(packet.getEntity(), packet.getSlots().stream().map(i -> i.mapSecond(ItemUtils::toClientItem)).toList());
        }

        return p;
    }
}
