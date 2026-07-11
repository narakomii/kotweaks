package narakomii.kotweaks.mixin.item;

import narakomii.kotweaks.utils.ItemUtils;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerGamePacketListenerImpl.class)
abstract class ServerPacketHandlerMixin {
    @ModifyVariable(method = "handleSetCreativeModeSlot", at = @At("HEAD"), argsOnly = true, name = "packet")
    public ServerboundSetCreativeModeSlotPacket handleSetCreativeModeSlot(final ServerboundSetCreativeModeSlotPacket packet) {
        return new ServerboundSetCreativeModeSlotPacket(packet.slotNum(), ItemUtils.fromFakeItem(packet.itemStack()));
    }
}
