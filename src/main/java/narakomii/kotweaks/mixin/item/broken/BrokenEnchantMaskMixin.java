package narakomii.kotweaks.mixin.item.broken;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
abstract class BrokenEnchantMaskMixin {
    @Inject(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentVisitor;)V", at = @At("HEAD"), cancellable = true)
    private static void runIterationOnItem(ItemStack piece, EnchantmentHelper.EnchantmentVisitor method, CallbackInfo ci) {
        if (piece != null && piece.isBroken())
            ci.cancel();
    }

    @Inject(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;)V", at = @At("HEAD"), cancellable = true)
    private static void runIterationOnItem(ItemStack piece, EquipmentSlot slot, LivingEntity owner, EnchantmentHelper.EnchantmentInSlotVisitor method, CallbackInfo ci) {
        if (piece != null && piece.isBroken())
            ci.cancel();
    }

    @Inject(method = "getItemEnchantmentLevel", at = @At("HEAD"), cancellable = true)
    private static void getItemEnchantmentLevel(Holder<Enchantment> enchantment, ItemInstance piece, CallbackInfoReturnable<Integer> cir) {
        if (piece instanceof ItemStack item) {
            if (item.isBroken()) {
                cir.setReturnValue(ItemEnchantments.EMPTY.getLevel(enchantment));
            }
        }
    }
}
