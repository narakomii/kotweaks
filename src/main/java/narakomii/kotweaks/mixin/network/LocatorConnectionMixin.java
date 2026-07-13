package narakomii.kotweaks.mixin.network;

import narakomii.kotweaks.KoTweaks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.waypoints.ServerWaypointManager;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWaypointManager.class)
abstract class LocatorConnectionMixin {
    @Inject(method = "createConnection", at = @At("HEAD"), cancellable = true)
    private void createConnection(ServerPlayer player, WaypointTransmitter waypoint, CallbackInfo ci) {
        if (waypoint instanceof ServerPlayer otherPlayer) {
            if (
                    !KoTweaks.locatorController.get(player.getUUID()).locatorEnabled
                            || !KoTweaks.locatorController.get(otherPlayer.getUUID()).locatorEnabled
            ) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "updateConnection", at = @At("HEAD"), cancellable = true)
    private void updateConnection(ServerPlayer player, WaypointTransmitter waypoint, WaypointTransmitter.Connection connection, CallbackInfo ci) {
        if (waypoint instanceof ServerPlayer otherPlayer) {
            if (
                    !KoTweaks.locatorController.get(player.getUUID()).locatorEnabled
                            || !KoTweaks.locatorController.get(otherPlayer.getUUID()).locatorEnabled
            ) {
                connection.disconnect();
                ci.cancel();
            }
        }
    }
}
