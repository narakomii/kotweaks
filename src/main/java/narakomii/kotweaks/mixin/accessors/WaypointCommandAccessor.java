package narakomii.kotweaks.mixin.accessors;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.WaypointCommand;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Consumer;

@Mixin(WaypointCommand.class)
public interface WaypointCommandAccessor {
    @Invoker("mutateIcon")
    static void kotweaks$mutateIcon(final CommandSourceStack source, final WaypointTransmitter waypoint, final Consumer<Waypoint.Icon> iconConsumer) {
        throw new AssertionError();
    }
}
