package narakomii.kotweaks.mixin.item;

import narakomii.kotweaks.utils.ItemUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(Connection.class)
abstract class ConnectionMixin {
    private static Set<Class<? extends Packet<? extends PacketListener>>> CLIENTBOUND = new HashSet<>(List.of(
            ClientboundContainerSetContentPacket.class,
            ClientboundSetPlayerInventoryPacket.class,
            ClientboundContainerSetSlotPacket.class,
            //ServerboundSetCreativeModeSlotPacket.class,
            ClientboundSetCursorItemPacket.class
    ));

    // TODO you can't check for serverbound ones here because...they aren't sent by a server; fix them at the handler/listener thing ig
    @ModifyVariable(method = "sendPacket", at = @At("HEAD"), argsOnly = true, name = "packet")
    private Packet<?> hi(Packet<?> p) {
        if (p instanceof ClientboundContainerSetContentPacket packet) {
            return new ClientboundContainerSetContentPacket(packet.containerId(), packet.stateId(), packet.items().stream().map(ItemUtils::toFakeItem).toList(), ItemUtils.toFakeItem(packet.carriedItem()));
        } else if (p instanceof ClientboundSetPlayerInventoryPacket packet) {
            return new ClientboundSetPlayerInventoryPacket(packet.slot(), ItemUtils.toFakeItem(packet.contents()));
        } else if (p instanceof ClientboundSetCursorItemPacket packet) {
            return new ClientboundSetCursorItemPacket(ItemUtils.toFakeItem(packet.contents()));
        } else if (p instanceof ClientboundContainerSetSlotPacket packet) {
            return new ClientboundContainerSetSlotPacket(packet.getContainerId(), packet.getStateId(), packet.getSlot(), ItemUtils.toFakeItem(packet.getItem()));
        }

        /*String name = packet.getClass().getSimpleName();
        if (name.startsWith("Serverbound")) {
            //packet.type().id();
        } else if (name.startsWith("Clientbound")) {

        }*/

        return p;
    }
}
