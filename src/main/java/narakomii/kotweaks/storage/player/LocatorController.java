package narakomii.kotweaks.storage.player;

import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.codedsakura.blossom.lib.permissions.Permissions;
import narakomii.kotweaks.KoTweaks;
import narakomii.kotweaks.types.MapController;
import narakomii.kotweaks.utils.CommandUtils;
import narakomii.kotweaks.utils.LocatorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.HexColorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ColorRGBA;
import org.jspecify.annotations.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class LocatorController extends MapController<UUID, LocatorController.LocatorOptionData> {
    @Override
    public String fileName() {
        return "locator";
    }

    @Override
    public Map<UUID, LocatorOptionData> defaultMap() {
        return new HashMap<>();
    }

    @Override
    protected @NonNull LocatorOptionData defaultEntry() {
        return LocatorOptionData.DEFAULT();
    }

    @Override
    protected TypeToken<?> typeToken() {
        return new TypeToken<HashMap<UUID, LocatorOptionData>>() {};
    }

    @Override
    public void reload(MinecraftServer server) {
        super.reload(server);

        //noinspection ConstantValue
        if (server.getPlayerList() != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                LocatorUtils.updateWaypoint(player);
            }
        }
    }

    /*public record LocatorOptionData(boolean locatorDisabled, int color, boolean colorEnabled) {
        public static final LocatorOptionData DEFAULT = new LocatorOptionData(false, 0, false);
    }*/

    public static class LocatorOptionData implements Serializable {
        public static LocatorOptionData DEFAULT() { return new LocatorOptionData(true, 0, false); }

        public boolean locatorEnabled;
        public int color;
        public boolean colorEnabled;

        public LocatorOptionData(boolean locatorEnabled, int color, boolean colorEnabled) {
            this.locatorEnabled = locatorEnabled;
            this.color = color;
            this.colorEnabled = colorEnabled;
        }
    }



    static {
        CommandUtils.addCommand(_ -> Commands.literal("locator")
                .requires(Permissions.require("kotweaks.locator", true))
                .executes(context -> query(context, context.getSource().getPlayerOrException()))
                .then(Commands.literal("query")
                        .requires(Permissions.require("kotweaks.locator.query", true))
                        .executes(context -> query(context, context.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .requires(Permissions.require("kotweaks.locator.query.other", false))
                                .executes(context -> query(context, EntityArgument.getPlayer(context, "target")))
                        )
                )
                .then(Commands.literal("disable")
                        .requires(Permissions.require("kotweaks.locator.toggle", true))
                        .executes(context -> toggleLocator(context, context.getSource().getPlayerOrException(), false))
                        .then(Commands.argument("target", EntityArgument.player())
                                .requires(Permissions.require("kotweaks.locator.toggle.other", false))
                                .executes(context -> toggleLocator(context, EntityArgument.getPlayer(context, "target"), false))
                        )
                )
                .then(Commands.literal("enable")
                        .requires(Permissions.require("kotweaks.locator.toggle", true))
                        .executes(context -> toggleLocator(context, context.getSource().getPlayerOrException(), true))
                        .then(Commands.argument("target", EntityArgument.player())
                                .requires(Permissions.require("kotweaks.locator.toggle.other", false))
                                .executes(context -> toggleLocator(context, EntityArgument.getPlayer(context, "target"), true))
                        )
                )
                .then(Commands.literal("toggle")
                        .requires(Permissions.require("kotweaks.locator.toggle", true))
                        .executes(context -> toggleLocator(context, context.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .requires(Permissions.require("kotweaks.locator.toggle.other", false))
                                .executes(context -> toggleLocator(context, EntityArgument.getPlayer(context, "target")))
                        )
                )
                .then(Commands.literal("color")
                        .requires(Permissions.require("kotweaks.locator.color", true))
                        .then(Commands.literal("hex")
                                .then(Commands.argument("color", HexColorArgument.hexColor())
                                                .executes(context -> setColor(context, context.getSource().getPlayerOrException(), HexColorArgument.getHexColor(context, "color")))
                                                .then(Commands.argument("target", EntityArgument.player())
                                                                .requires(Permissions.require("kotweaks.locator.color.other", false))
                                                                .executes(context -> setColor(context, EntityArgument.getPlayer(context, "target"), HexColorArgument.getHexColor(context, "color")))
                                                )
                                )
                        )
                        .then(Commands.argument("color", ColorArgument.color())
                                .executes(context -> setColor(context, context.getSource().getPlayerOrException(), Objects.requireNonNull(ColorArgument.getColor(context, "color").getColor())))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .requires(Permissions.require("kotweaks.locator.color.other", false))
                                        .executes(context -> setColor(context, EntityArgument.getPlayer(context, "target"), Objects.requireNonNull(ColorArgument.getColor(context, "color").getColor())))
                                )
                        )
                        .then(Commands.literal("reset")
                                .executes(context -> resetColor(context, context.getSource().getPlayerOrException()))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .requires(Permissions.require("kotweaks.locator.color.other", false))
                                        .executes(context -> resetColor(context, EntityArgument.getPlayer(context, "target")))
                                )
                        )
                )
        );
    }

    private static int toggleLocator(CommandContext<CommandSourceStack> context, ServerPlayer player) throws CommandSyntaxException {
        LocatorOptionData options = KoTweaks.locatorController.get(player.getUUID());
        return toggleLocator(context, player, !options.locatorEnabled);
    }
    private static int toggleLocator(CommandContext<CommandSourceStack> context, ServerPlayer player, boolean state) {
        LocatorOptionData options = KoTweaks.locatorController.get(player.getUUID());
        options.locatorEnabled = state;
        KoTweaks.locatorController.write();
        LocatorUtils.updateWaypoint(player);
        msg(context, options);
        return Command.SINGLE_SUCCESS;
    }

    private static int setColor(CommandContext<CommandSourceStack> context, ServerPlayer player, int color) {
        LocatorOptionData options = KoTweaks.locatorController.get(player.getUUID());
        options.color = color;
        options.colorEnabled = true;
        KoTweaks.locatorController.write();
        LocatorUtils.updateWaypoint(player);
        msg(context, options);
        return Command.SINGLE_SUCCESS;
    }

    private static int resetColor(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        LocatorOptionData options = KoTweaks.locatorController.get(player.getUUID());
        options.colorEnabled = false;
        KoTweaks.locatorController.write();
        LocatorUtils.updateWaypoint(player);
        msg(context, options);
        return Command.SINGLE_SUCCESS;
    }

    private static int query(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        msg(context, KoTweaks.locatorController.get(player.getUUID()));
        return Command.SINGLE_SUCCESS;
    }

    private static void msg(CommandContext<CommandSourceStack> context, LocatorOptionData options) {
        CommandUtils.tell(
                context,
                Component.literal("Locator").withStyle(ChatFormatting.AQUA),
                Component.literal(": "),
                Component.literal(options.locatorEnabled ? "enabled" : "disabled").withStyle(ChatFormatting.GOLD),
                Component.literal(", "),
                Component.literal( "Color").withStyle(ChatFormatting.AQUA),
                Component.literal(": "),
                (options.colorEnabled
                        ? Component.literal(new ColorRGBA(options.color).toString()).withColor(options.color)
                        : Component.literal("default").withStyle(ChatFormatting.WHITE)
                )
        );
    }
}
