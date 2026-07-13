package narakomii.kotweaks.types;

import narakomii.kotweaks.KoTweaks;
import narakomii.kotweaks.utils.CommandUtils;
import narakomii.kotweaks.utils.ItemUtils;
import narakomii.kotweaks.utils.TypeUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Optional;

public class CustomItem extends Item {
    private final String fallbackName;
    private final Identifier fallbackId;
    public final TypeUtils.LazySupplier<Patch> fakeComponents = TypeUtils.lazyValue(() -> fakeMap(this));

    public CustomItem(String fallbackName, String fallbackModel, Properties properties) {
        this.fallbackName = fallbackName;
        this.fallbackId = Identifier.parse(fallbackModel);
        super(properties);
    }

    public String getFallbackName() {
        return fallbackName;
    }
    public Identifier getFallbackModel() {
        return fallbackId;
    }

    public static final Identifier NBT_ID = KoTweaks.id("id");
    public static final Identifier IGNORED_DATA_ID = KoTweaks.id("ignored_data");

    private static final TypeUtils.LazySupplier<Holder<Item>> fakeItem = TypeUtils.lazyValue(() -> TypeUtils.holder(BuiltInRegistries.ITEM, "minecraft:ender_dragon_spawn_egg"));

    public static Identifier getId(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            Optional<String> id = data.copyTag().getString(NBT_ID.toString());
            if (id.isPresent())
                return Identifier.parse(id.get());
        }

        return BuiltInRegistries.ITEM.getKey(stack.getItem());
    }

    public static Holder<Item> getHolder(ItemStack stack) {
        return BuiltInRegistries.ITEM.get(getId(stack)).orElseThrow();
    }

    public static Patch fakeMap(Item item) {
        if (!(item instanceof CustomItem customItem)) throw new IllegalArgumentException("");
        var holder = BuiltInRegistries.ITEM.wrapAsHolder(item);
        DataComponentPatch.Builder builder = DataComponentPatch.builder();
        builder.set(fakeItem.get().components());
        ItemUtils.emptyPatch(builder);
        builder.set(customItem.components());
        builder.set(DataComponents.ITEM_MODEL, customItem.getFallbackModel());
        CompoundTag customDataComponent = new CompoundTag();
        customDataComponent.putString(CustomItem.NBT_ID.toString(), holder.unwrapKey().orElseThrow().identifier().toString());
        return new Patch(builder.build(), DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, CustomData.of(customDataComponent)).build());
    }

    public static ItemStack toFakeItem(ItemStack item) {
        if (item.isEmpty()) return ItemStack.EMPTY;

        if (!(item.item.value() instanceof CustomItem customItem))
            return item.copy();

        DataComponentPatch.Builder builder = DataComponentPatch.builder();
        var newItem = new ItemStack(fakeItem.get(), item.count, customItem.fakeComponents.get().apply(builder, item).build());
        newItem.setPopTime(item.getPopTime());
        return newItem;
    }

    public static ItemStack fromFakeItem(ItemStack item) {
        if (item.isEmpty()) return ItemStack.EMPTY;

        if (item.item != null && !(item.item.value() instanceof CustomItem)) {
            var holder = CustomItem.getHolder(item);
            if (!(holder.value() instanceof CustomItem customItem)) return item;

            var baseComponents = holder.value().components();
            var fake = PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, customItem.fakeComponents.get().first());
            fake.applyPatch(customItem.fakeComponents.get().second());

            DataComponentPatch.Builder builder = DataComponentPatch.builder();
            var patch = item.getComponentsPatch();

            for (DataComponentType type : ItemUtils.DATA_COMPONENT_TYPES) { //TODO iterate into inner object values (maybe use conversion to CompoundTag and back?)
                try {
                    put(builder, baseComponents.getTyped(type), fake.getTyped(type), TypedDataComponent.createUnchecked(type, patch.get(DataComponentMap.EMPTY, type)));
                } catch (Exception e) {
                    KoTweaks.LOGGER.error(CommandUtils.formatError("Error setting data from masked item", e));
                }
            }

            var newItem = new ItemStack(holder, item.count, builder.build());
            newItem.setPopTime(item.getPopTime());
            return newItem;
        }

        return item;
    }

    private static <T> void put(DataComponentPatch.Builder builder, TypedDataComponent<T> base, TypedDataComponent<T> fake, TypedDataComponent<T> input) {
        if (TypeUtils.isNull(input))
            return;

        if (ItemUtils.componentsNotEqual(fake, input, true)) {
            if (TypeUtils.isNull(base) || ItemUtils.componentsNotEqual(base, input, false))
                builder.set(input);
        }
    }

    public record Patch(DataComponentPatch first, DataComponentPatch second) {
        public DataComponentPatch.Builder apply(DataComponentPatch.Builder builder, ItemStack item) {
            ItemUtils.putPatch(builder, first);
            ItemUtils.putPatch(builder, item.getComponentsPatch());
            ItemUtils.putPatch(builder, second);
            return builder;
        }
    }

    public interface Stack {
        ItemStack kotweaks$toFakeItem();
        ItemStack kotweaks$fromFakeItem();

        public static Stack of(ItemStack stack) {
            return (Stack) (Object) stack;
        }
    }
}
