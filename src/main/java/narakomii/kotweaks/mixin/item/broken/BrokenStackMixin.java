package narakomii.kotweaks.mixin.item.broken;

import narakomii.kotweaks.KoTweaks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
abstract class BrokenStackMixin {
    @Shadow
    public abstract void setDamageValue(int value);

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract int getDamageValue();

    @Shadow
    public abstract boolean isDamageableItem();

    @Shadow
    public abstract int getMaxDamage();

    /**
     * @author narakomii
     * @reason limit min durability 1 instead of 0; don't delete item when broken
     */
    @Overwrite
    private void applyDamage(int newDamage, final @Nullable ServerPlayer player, final Consumer<Item> onBreak) {
        ItemStack stack = (ItemStack) (Object) this;
        boolean wasBroken = isBroken();
        boolean didBreak = false;
        if (newDamage >= kotweaks$getMaxDamage()) {
            newDamage = kotweaks$getMaxDamage();
            didBreak = true;
        }

        if (player != null) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(player, stack, newDamage);
        }

        setDamageValue(newDamage);
        //KoTweaks.LOGGER.info("{} {}", didBreak, wasBroken);
        if (didBreak && !wasBroken) {
            //KoTweaks.LOGGER.info("break");
            Item item = getItem();
            onBreak.accept(item);
        }
    }

    /**
     * @author narakomii
     * @reason include durability = 1 as broken
     */
    @Overwrite
    public boolean isBroken() {
        return isDamageableItem() && getDamageValue() >= kotweaks$getMaxDamage();
    }

    /**
     * @author narakomii
     * @reason include durability = 1 as broken
     */
    /*@Overwrite
    public boolean nextDamageWillBreak() {
        return isDamageableItem() && getDamageValue() >= kotweaks$getMaxDamage() - 1;
    }*/

    @Unique
    private int kotweaks$getMaxDamage() {
        return getMaxDamage() - 1;
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult use(Item instance, Level level, Player player, InteractionHand hand) {
        if (isBroken()) {
            return InteractionResult.FAIL;
        }

        return instance.use(level, player, hand);
    }
}
