package narakomii.kotweaks.utils;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import narakomii.kotweaks.KoTweaks;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
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

    public static String formatError(Exception e) {
        ArrayList<String> li = new ArrayList<>();
        for (StackTraceElement el : e.getStackTrace()) {
            li.add(el.toString());
        }
        return String.format("%s: %s\n\n%s", e.getClass().getSimpleName(), e.getMessage(), String.join("\n", li));
    }

    public static void tellError(CommandContext<CommandSourceStack> ctx, Exception e) {
        tell(ctx, formatError(e));
    }

    public static void tellFormat(CommandContext<CommandSourceStack> ctx, @NonNull String format, Object... args) {
        try {
            Method formatMethod = String.class.getMethod("format", String.class, Object[].class);
            tell(ctx, Component.literal((String) formatMethod.invoke(null, format, args)));
        } catch (Exception e) {
            KoTweaks.LOGGER.error("Internal Error (tellFormat): {}", formatError(e));
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
            KoTweaks.LOGGER.error("Internal Error (tell): {}", formatError(e));
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
