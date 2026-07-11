package narakomii.kotweaks.mixin.accessors;

import com.google.common.collect.Table;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.waypoints.ServerWaypointManager;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ServerWaypointManager.class)
public interface ServerWaypointManagerAccessor {
    @Accessor("waypoints")
    Set<WaypointTransmitter> kotweaks$getWaypoints();

    @Accessor("players")
    Set<ServerPlayer> kotweaks$getPlayers();

    @Accessor("connections")
    Table<ServerPlayer, WaypointTransmitter, WaypointTransmitter.Connection> kotweaks$getConnections();
}
