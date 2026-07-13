package narakomii.kotweaks.mixin.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class MobBrokenItemMaskMixin {
    @Unique
    private ItemStack using = null;
    @Inject(method = "interact", at = @At("HEAD"))
    private void startInteract(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.getItemInHand(hand).isBroken()) {
            using = player.getItemInHand(hand);
            player.setItemInHand(hand, ItemStack.EMPTY);
        }
    }

    @Inject(method = "interact", at = @At("RETURN"))
    private void endInteract(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        if (using != null) {
            player.setItemInHand(hand, using);
            using = null;
        }
    }
}
