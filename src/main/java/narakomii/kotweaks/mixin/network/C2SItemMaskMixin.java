package narakomii.kotweaks.mixin.network;

import narakomii.kotweaks.KoTweaks;
import narakomii.kotweaks.utils.CommandUtils;
import narakomii.kotweaks.utils.ItemUtils;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerGamePacketListenerImpl.class)
abstract class C2SItemMaskMixin {
    @ModifyVariable(method = "handleSetCreativeModeSlot", at = @At("HEAD"), argsOnly = true, name = "packet")
    public ServerboundSetCreativeModeSlotPacket handleSetCreativeModeSlot(final ServerboundSetCreativeModeSlotPacket packet) {
        try {
            return new ServerboundSetCreativeModeSlotPacket(packet.slotNum(), ItemUtils.fromClientItem(packet.itemStack()));
        } catch (Exception e) {
            KoTweaks.LOGGER.error(CommandUtils.formatError("Error demasking item", e));
            return packet;
        }
    }
}
