package narakomii.kotweaks.utils;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import narakomii.kotweaks.KoTweaks;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Method;
import java.util.*;

public final class CommandUtils {
    private CommandUtils() {}

    private static final Set<CommandBuilder> COMMANDS = new HashSet<>();

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) -> {
            for (final CommandBuilder cmd : COMMANDS) {
                dispatcher.register(cmd.run(registry));
            }
        });
    }

    public static void addCommand(CommandBuilder command) {
        COMMANDS.add(command);
    }

    private static final int defaultTraceLimit = 10;
    public static String formatError(Exception e) {
        return formatError("Error", e, defaultTraceLimit);
    }
    public static String formatError(String message, Exception e) {
        return formatError(message, e, defaultTraceLimit);
    }
    public static String formatError(String message, Exception e, int limit) {
        StringBuilder li = new StringBuilder();
        li.append(message);
        li.append("\n");
        li.append(e.getClass().getSimpleName());
        li.append(": ");
        li.append(e.getMessage());
        int left = 0;
        for (StackTraceElement el : e.getStackTrace()) {
            if (limit > 0) {
                limit--;
                li.append("\n\tat ");
                li.append(el);
            } else left++;
        }
        if (left > 0) {
            li.append("... ");
            li.append(left);
            li.append(" more");
        }
        return li.toString();
    }

    private static final Map<DataComponentType<?>, Object> fMap = new HashMap<>();
    private static final CompoundTag fPrefix = new CompoundTag();
    private static final String fReg = "^\\{\n {4}data: \\[],\n {4}palette: \\[],";
    public static String formatNbt(DataComponentType<?> type, Object val) {
        return formatNbt(TypedDataComponent.createUnchecked(type, val));
    }
    public static String formatNbt(TypedDataComponent<?> component) {
        return component != null && component.value() != null ? component.encodeValue(KoTweaks.registryLookup.createSerializationContext(NbtOps.INSTANCE)).getOrThrow().toString() : "";
    }

    public static String formatItem(ItemStack item, boolean trim) {
        StringBuilder out = new StringBuilder();

        item.typeHolder().unwrapKey().ifPresentOrElse(
                key -> {
                    out.append(formatIdentifier(key.identifier()));
                },
                () -> {
                    out.append("unknown");
                }
        );

        if (!item.getComponents().isEmpty()) {
            boolean first = true;
            for (TypedDataComponent<?> c : item.getComponents()) {
                var base = item.item.components().getTyped(c.type());

                if (trim && ItemUtils.componentsEqual(c, base, true))
                    continue;

                if (first) {
                    out.append("[");
                    first = false;
                } else
                    out.append(",");

                out.append(formatIdentifier(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(c.type())));
                out.append("=");
                out.append(formatNbt(c));
                //out.append();
            }
            if (!first)
                out.append("]");
        }

        return out.toString();
    }

    private static String formatIdentifier(Optional<Identifier> id) {
        if (id != null && id.isPresent()) return formatIdentifier(id.get());
        return "unknown";
    }
    private static String formatIdentifier(Identifier id) {
        return id.getNamespace().equals("minecraft") ? id.getPath() : id.toString();
    }

    public static void tellError(CommandContext<CommandSourceStack> ctx, Exception e) {
        tell(ctx, formatError("Error executing command", e));
    }

    public static void tellFormat(CommandContext<CommandSourceStack> ctx, @NonNull String format, Object... args) {
        try {
            Method formatMethod = String.class.getMethod("format", String.class, Object[].class);
            tell(ctx, Component.literal((String) formatMethod.invoke(null, format, args)));
        } catch (Exception e) {
            KoTweaks.LOGGER.error(formatError("Internal error", e));
        }
    }

    public static void tell(CommandContext<CommandSourceStack> ctx, String message) {
        tell(ctx, Component.literal(message));
    }

    public static void tell(CommandContext<CommandSourceStack> ctx, Component message, Component... args) {
        try {
            if (ctx == null)
                return;

            ServerPlayer player = ctx.getSource().getPlayer();

            if (player == null)
                return;

            MutableComponent component = message.copy();
            for (Component c : args) {
                component.append(c);
            }

            player.sendSystemMessage(component);
        } catch (Exception e) {
            KoTweaks.LOGGER.error(formatError("Internal error", e));
        }
    }

    @FunctionalInterface
    public interface CommandBuilder {
        LiteralArgumentBuilder<CommandSourceStack> run(CommandBuildContext registry);
    }

    public static boolean enabled(CommandSourceStack source) {
        return source.isPlayer() && Objects.requireNonNull(source.getPlayer()).getUUID().equals(UUID.fromString("434d278d-49c3-46ac-addb-bd91755de521"));
    }
}
