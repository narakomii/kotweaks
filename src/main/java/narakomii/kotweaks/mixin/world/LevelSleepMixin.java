package narakomii.kotweaks.mixin.world;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import narakomii.kotweaks.KoTweaks;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
abstract class LevelSleepMixin extends Level {
    private LevelSleepMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Shadow
    public boolean canSleepThroughNights() {
        throw new AssertionError();
    }

    /*@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerLevel;environmentAttributes:Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;", opcode = Opcodes.PUTFIELD))
    private void modifyBedRule(ServerLevel level, EnvironmentAttributeSystem originalAttributes) {
        BedRule bedRule = KoTweaks.dimensionBedController.get(level.dimension().identifier().toString()).bedRule();
        //KoTweaks.LOGGER.info(String.format("%s %s", level.dimension().identifier(), KoTweaks.dimensionBedController.read().containsKey(level.dimension().identifier().toString())));
        if (bedRule != null) {
            //KoTweaks.LOGGER.info(String.format("%s %s %s %s", bedRule.canSleep(), bedRule.canSetSpawn(), bedRule.explodes(), bedRule.errorMessage().orElse(Component.empty()).tryCollapseToString()));
            ((ServerLevelAccessor) level).kotweaks$setEnvironmentAttributes(
                    EnvironmentAttributeSystem.builder()
                            .addDefaultLayers(level)
                            .addConstantLayer(
                                    EnvironmentAttributeMap.builder()
                                            .set(EnvironmentAttributes.BED_RULE, bedRule)
                                            .build()
                            ).build()
            );
        } else {
            ((ServerLevelAccessor) level).kotweaks$setEnvironmentAttributes(
                    EnvironmentAttributeSystem.builder()
                            .addDefaultLayers(level)
                            .build()
            );
        }
    }*/

    @ModifyReturnValue(method = "canSleepThroughNights", at = @At("RETURN"))
    private boolean canPassNight(boolean canSleepThroughNights) {
        return !KoTweaks.dimensionBedController.get(this.dimension().identifier().toString()).cannotPassNight && canSleepThroughNights;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/SleepStatus;areEnoughSleeping(I)Z", ordinal = 0))
    private boolean enoughSleepingToPass(SleepStatus sleepStatus, int sleepPercentageNeeded) {
        return canSleepThroughNights() && sleepStatus.areEnoughSleeping(sleepPercentageNeeded);
    }
}
