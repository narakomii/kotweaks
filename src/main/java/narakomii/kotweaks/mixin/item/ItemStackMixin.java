package narakomii.kotweaks.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import narakomii.kotweaks.KoTweaks;
import narakomii.kotweaks.types.CustomItem;
import narakomii.kotweaks.utils.ItemUtils;
import narakomii.kotweaks.utils.TypeUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(ItemStack.class)
abstract class ItemStackMixin implements CustomItem.Stack {
    @Unique
    private static final TypeUtils.LazySupplier<Holder<Item>> fakeItem = TypeUtils.lazyValue(() -> TypeUtils.holder(BuiltInRegistries.ITEM, "minecraft:ender_dragon_spawn_egg"));

    @Shadow
    @Final
    private @Nullable Holder<Item> item;

    @Shadow
    public abstract boolean isEmpty();

    @Shadow
    public abstract ItemStack copy();

    @Shadow
    private int count;

    @Shadow
    public abstract int getPopTime();

    @Shadow
    public abstract Holder<Item> typeHolder();

    @Shadow
    public abstract DataComponentMap getComponents();

    @Shadow
    public abstract <T> T set(DataComponentType<T> type, @Nullable T value);

    @Shadow
    @Final
    private PatchedDataComponentMap components;

    @Shadow
    public abstract DataComponentPatch getComponentsPatch();

    @Shadow
    public abstract void setCount(int count);

    @Unique
    private ItemStack stack() {
        return (ItemStack) (Object) this;
    }

    // TODO check client-to-server packets for custom items
    public ItemStack kotweaks$toFakeItem() {
        if (isEmpty()) return ItemStack.EMPTY;

        if (this.item != null && this.item.value() instanceof CustomItem) {
            DataComponentPatch.Builder builder = DataComponentPatch.builder();
            builder.set(fakeItem.get().components());
            ItemUtils.empty(builder);
            builder.set(typeHolder().components());
            builder.set(getComponents());

            CompoundTag customDataComponent = new CompoundTag();
            item.unwrapKey().ifPresent(key -> customDataComponent.putString(CustomItem.NBT_ID, key.identifier().toString()));
            builder.set(DataComponents.CUSTOM_DATA, CustomData.of(customDataComponent));

            var copy = new ItemStack(fakeItem.get(), count, builder.build());
            copy.setPopTime(getPopTime());
            CustomItem.Stack.of(copy).kotweaks$fromFakeItem();
            return copy;
        }

        return this.copy();
    }

    public ItemStack kotweaks$fromFakeItem() {
        if (isEmpty()) return ItemStack.EMPTY;

        if (this.item != null) {
            var id = CustomItem.getId(stack());
            var holder = BuiltInRegistries.ITEM.get(id).orElseThrow();
            var baseComponents = holder.value().components();

            DataComponentPatch.Builder builder = DataComponentPatch.builder();
            var patch = getComponentsPatch();

            for (DataComponentType type : ItemUtils.DATA_COMPONENT_TYPES) {
                put(builder, type, baseComponents.get(type), getComponents().get(type));
            }

            var copy = new ItemStack(holder, count, builder.build());
            log(copy);
            log(stack());
            copy.setPopTime(getPopTime());
            //return copy;
        }

        return this.copy();
    }

    @Unique
    private static void log(ItemStack item) {
        KoTweaks.LOGGER.info("{}{}", item.typeHolder().unwrapKey().orElseThrow().identifier(), NbtUtils.structureToSnbt((CompoundTag) PatchedDataComponentMap.CODEC.encode(item.getComponents(), NbtOps.INSTANCE, new CompoundTag()).getOrThrow()));
    }

    @Unique
    private static <T> void put(DataComponentPatch.@NonNull Builder builder, @NonNull DataComponentType<T> type, @Nullable T baseVal, @Nullable T inputVal) {
        switch (Objects.requireNonNull(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type)).toString()) {case "attack_range" -> {}
            case "minecraft:attribute_modifiers" -> {
                if (baseVal != null) {
                    assert baseVal.getClass() == ItemAttributeModifiers.class;
                    if (inputVal != null) {
                        builder.set(type, inputVal);
                    }
                }
            }
            case "minecraft:banner_patterns" -> {}
            case "minecraft:base_color" -> {}
            case "minecraft:bees" -> {}
            case "minecraft:block_entity_data" -> {}
            case "minecraft:block_state" -> {}
            case "minecraft:block_transformer" -> {}
            case "minecraft:blocks_attacks" -> {}
            case "minecraft:break_sound" -> {}
            case "minecraft:bucket_entity_data" -> {}
            case "minecraft:bundle_contents" -> {}
            case "minecraft:can_break" -> {}
            case "minecraft:can_place_on" -> {}
            case "minecraft:charged_projectiles" -> {}
            case "minecraft:compostable" -> {}
            case "minecraft:consumable" -> {}
            case "minecraft:container" -> {}
            case "minecraft:container_loot" -> {}
            case "minecraft:custom_data" -> {}
            case "minecraft:custom_model_data" -> {}
            case "minecraft:custom_name" -> {}
            case "minecraft:damage" -> {}
            case "minecraft:damage_resistant" -> {}
            case "minecraft:damage_type" -> {}
            case "minecraft:death_protection" -> {}
            case "minecraft:debug_stick_state" -> {}
            case "minecraft:dye" -> {}
            case "minecraft:dyed_color" -> {}
            case "minecraft:enchantable" -> {}
            case "minecraft:enchantment_glint_override" -> {}
            case "minecraft:enchantments" -> {}
            case "minecraft:entity_data" -> {}
            case "minecraft:equippable" -> {}
            case "minecraft:firework_explosion" -> {}
            case "minecraft:fireworks" -> {}
            case "minecraft:food" -> {}
            case "minecraft:glider" -> {}
            case "minecraft:instrument" -> {}
            case "minecraft:intangible_projectile" -> {}
            case "minecraft:item_model" -> {}
            case "minecraft:item_name" -> {}
            case "minecraft:jukebox_playable" -> {}
            case "minecraft:kinetic_weapon" -> {}
            case "minecraft:lock" -> {}
            case "minecraft:lodestone_tracker" -> {}
            case "minecraft:lore" -> {}
            case "minecraft:map_color" -> {}
            case "minecraft:map_decorations" -> {}
            case "minecraft:map_id" -> {}
            case "minecraft:max_damage" -> {}
            case "minecraft:max_stack_size" -> {}
            case "minecraft:minimum_attack_charge" -> {}
            case "minecraft:note_block_sound" -> {}
            case "minecraft:ominous_bottle_amplifier" -> {}
            case "minecraft:piercing_weapon" -> {}
            case "minecraft:pot_decorations" -> {}
            case "minecraft:potion_contents" -> {}
            case "minecraft:potion_duration_scale" -> {}
            case "minecraft:profile" -> {}
            case "minecraft:provides_banner_patterns" -> {}
            case "minecraft:provides_pottery_pattern" -> {}
            case "minecraft:provides_trim_material" -> {}
            case "minecraft:rarity" -> {}
            case "minecraft:recipes" -> {}
            case "minecraft:repair_cost" -> {}
            case "minecraft:repairable" -> {}
            case "minecraft:stored_enchantments" -> {}
            case "minecraft:sulfur_cube_content" -> {}
            case "minecraft:suspicious_stew_effects" -> {}
            case "minecraft:swing_animation" -> {}
            case "minecraft:tool" -> {}
            case "minecraft:tooltip_display" -> {}
            case "minecraft:tooltip_style" -> {}
            case "minecraft:trim" -> {}
            case "minecraft:unbreakable" -> {}
            case "minecraft:use_cooldown" -> {}
            case "minecraft:use_effects" -> {}
            case "minecraft:use_remainder" -> {}
            case "minecraft:weapon" -> {}
            case "minecraft:writable_book_content" -> {}
            case "minecraft:written_book_content" -> {}
            default -> {
            }
        }
    }

    @ModifyReturnValue(method = "typeHolder", at = @At("RETURN"))
    public Holder<Item> typeHolder(Holder<Item> original) {
        /*
        if (original.value() instanceof CustomItem custom) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            boolean flag1 = true;

            for (StackTraceElement element : trace) {
                var c = element.getClassName();
                if (c.contains("Component")) {
                    flag1 = false;
                    break;
                }
            }

            if (flag1) return fakeItem.get();
        }*/

        return original;
    }

    private boolean z() {
        return false;
    }
}
