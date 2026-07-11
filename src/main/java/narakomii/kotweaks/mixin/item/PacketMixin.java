package narakomii.kotweaks.mixin.item;

import narakomii.kotweaks.types.PacketExtension;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Packet.class)
interface PacketMixin extends PacketExtension {
    //
}
