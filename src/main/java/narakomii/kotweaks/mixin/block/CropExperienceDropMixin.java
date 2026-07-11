package narakomii.kotweaks.mixin.block;

import narakomii.kotweaks.KoTweaks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;

@Mixin(BlockBehaviour.BlockStateBase.class)
abstract class CropExperienceDropMixin {
    @Shadow
    public Block getBlock() {
        throw new AssertionError();
    }

    @Shadow
    protected abstract BlockState asState();

    @Inject(method = "spawnAfterBreak", at = @At("HEAD"))
    public void spawnAfterBreak(ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience, CallbackInfo ci) {
        Block block = getBlock();
        String id = Objects.requireNonNull(block.properties().blockId()).identifier().toString();
        if (dropExperience && block instanceof CropBlock crop && level.getGameRules().get(GameRules.BLOCK_DROPS)) {
            Optional<IntProvider> expProvider = KoTweaks.cropExpController.getProvider(id);
            if (expProvider.isPresent() && crop.getAge(this.asState()) >= crop.getMaxAge()) {
                int exp = EnchantmentHelper.processBlockExperience(level, tool, expProvider.get().sample(level.getRandom()));
                if (exp > 0) {
                    ExperienceOrb.award(level, Vec3.atCenterOf(pos), exp);
                }
            }
        }
    }
}
