package narakomii.kotweaks.mixin.accessors;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WaypointTransmitter.EntityAzimuthConnection.class)
public interface EntityAzimuthConnectionAccessor {
    @Accessor("source")
    LivingEntity kotweaks$getSource();

    @Accessor("receiver")
    ServerPlayer kotweaks$getReceiver();
}
