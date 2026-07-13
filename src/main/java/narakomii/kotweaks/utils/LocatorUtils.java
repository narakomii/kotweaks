package narakomii.kotweaks.utils;

import narakomii.kotweaks.KoTweaks;
import narakomii.kotweaks.storage.player.LocatorController;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.waypoints.ServerWaypointManager;

import java.util.Optional;

public final class LocatorUtils {
    private LocatorUtils() {}

    public static void updateWaypoint(ServerPlayer player) {
        if (player == null) {
            return;
        }

        LocatorController.LocatorOptionData options = KoTweaks.locatorController.get(player.getUUID());
        ServerLevel level = player.level();
        ServerWaypointManager waypointManager = level.getWaypointManager();
        if (options.locatorEnabled) {
            waypointManager.addPlayer(player);
            waypointManager.trackWaypoint(player);
        } else {
            waypointManager.removePlayer(player);
        }

        if (waypointManager.transmitters().contains(player)) {
            mutateIcon(player);
        }
    }

    public static void mutateIcon(ServerPlayer player) {
        LocatorController.LocatorOptionData options = KoTweaks.locatorController.get(player.getUUID());

        if (options.colorEnabled) {
            player.waypointIcon().color = Optional.of(options.color);
        } else {
            player.waypointIcon().color = Optional.empty();
        }
    }
}
