package narakomii.kotweaks.mixin.accessors;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerLevel.class)
public interface ServerLevelAccessor {
    @Accessor("environmentAttributes")
    void kotweaks$setEnvironmentAttributes(EnvironmentAttributeSystem environmentAttributeSystem);
}
