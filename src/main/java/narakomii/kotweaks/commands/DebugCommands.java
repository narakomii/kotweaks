package narakomii.kotweaks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.codedsakura.blossom.lib.BlossomLib;
import dev.codedsakura.blossom.lib.permissions.Permissions;
import narakomii.kotweaks.KoTweaks;
import narakomii.kotweaks.utils.CommandUtils;
import narakomii.kotweaks.utils.ItemUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.commands.LookAt;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.util.Collections;

public class DebugCommands {
    public static void init() {
        CommandUtils.addCommand(registry -> Commands.literal(";")
                .then(Commands.literal("item")
                        .then(Commands.literal("add")
                                .requires(CommandUtils.predicate("item.give", PermissionLevel.MODERATORS))
                                .then(Commands.argument("item", ItemArgument.item(registry))
                                        .executes(wrap(ctx -> ItemUtils.give(ctx.getSource().getPlayerOrException().getInventory(), ItemArgument.getItem(ctx, "item").createItemStack(1), 1)))
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                                .executes(wrap(ctx -> ItemUtils.give(ctx.getSource().getPlayerOrException().getInventory(), ItemArgument.getItem(ctx, "item").createItemStack(1), IntegerArgumentType.getInteger(ctx, "count"))))
                                                .then(Commands.argument("target", EntityArgument.players())
                                                        .executes(wrap(ctx -> {
                                                            ItemStack item = ItemArgument.getItem(ctx, "item").createItemStack(1);
                                                            EntityArgument.getPlayers(ctx, "target").forEach(target ->
                                                                    ItemUtils.give(target.getInventory(), item, IntegerArgumentType.getInteger(ctx, "count"))
                                                            );
                                                        }))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("delete")
                                .requires(CommandUtils.predicate("item.give", PermissionLevel.MODERATORS))
                                .executes(wrap(ctx -> {
                                    Inventory inv = ctx.getSource().getPlayerOrException().getInventory();
                                    int slot = inv.getSelectedSlot();
                                    inv.removeItemNoUpdate(slot);
                                }))
                        )
                        .then(Commands.literal("dupe")
                                .requires(CommandUtils.predicate("item.give", PermissionLevel.MODERATORS))
                                .executes(wrap(ctx -> {
                                    Inventory inv = ctx.getSource().getPlayerOrException().getInventory();
                                    ItemStack stack = CommandUtils.getSelectedItem(inv);
                                    ItemUtils.give(inv, stack, stack.getCount());
                                }))
                        )
                        .then(Commands.literal("max")
                                .requires(CommandUtils.predicate("item.give", PermissionLevel.MODERATORS))
                                .executes(wrap(ctx -> CommandUtils.getSelectedItem(ctx.getSource().getPlayerOrException().getInventory()).setCount(99)))
                        )
                        .then(Commands.literal("count")
                                .requires(CommandUtils.predicate("item.give", PermissionLevel.MODERATORS))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1, 99))
                                        .executes(wrap(ctx -> CommandUtils.getSelectedItem(ctx.getSource().getPlayerOrException().getInventory()).setCount(IntegerArgumentType.getInteger(ctx, "count"))))
                                )
                        )
                        .then(Commands.literal("modify")
                                        .requires(CommandUtils.predicate("item.give", PermissionLevel.MODERATORS))
                                //.then(Commands.argument("nbt", DataComponentPatch))
                                //TODO custom format with syntax like:
                                // base item:     [enchantments={mending:1}]
                                // command input: [~enchantments={sharpness:5}]
                                // output item:   [enchantments={mending:1,sharpness:5}]
                        )
                        .then(Commands.literal("info")
                                .requires(CommandUtils.predicate("item.info", PermissionLevel.ALL))
                                .executes(wrap(ctx -> {
                                    var item = CommandUtils.getSelectedItem(ctx.getSource().getPlayerOrException().getInventory());
                                    CommandUtils.tell(ctx, CommandUtils.formatItem(item, true));
                                }))
                        )
                        .then(Commands.literal("info-long")
                                .requires(CommandUtils.predicate("item.info", PermissionLevel.ALL))
                                .executes(wrap(ctx -> {
                                    var item = CommandUtils.getSelectedItem(ctx.getSource().getPlayerOrException().getInventory());
                                    CommandUtils.tell(ctx, CommandUtils.formatItem(item, false));
                                }))
                        )
                )
                .then(Commands.literal("gm")
                        .requires(CommandUtils.predicate("gm", PermissionLevel.MODERATORS))
                        .then(Commands.literal("s")
                                .executes(wrap(ctx -> ctx.getSource().getPlayerOrException().setGameMode(GameType.SURVIVAL)))
                        )
                        .then(Commands.literal("c")
                                .executes(wrap(ctx -> ctx.getSource().getPlayerOrException().setGameMode(GameType.CREATIVE)))
                        )
                        .then(Commands.literal("a")
                                .executes(wrap(ctx -> ctx.getSource().getPlayerOrException().setGameMode(GameType.ADVENTURE)))
                        )
                        .then(Commands.literal("sp")
                                .executes(wrap(ctx -> ctx.getSource().getPlayerOrException().setGameMode(GameType.SPECTATOR)))
                        )
                )
                .then(Commands.literal("throw")
                        .requires(CommandUtils.predicate("debug", PermissionLevel.ALL))
                        .executes(wrap(ctx -> {
                            throw new SimpleCommandExceptionType(new LiteralMessage("uaua")).create();
                        }))
                        .then(Commands.literal("unwrapped")
                                .executes(ctx -> {
                                    throw new SimpleCommandExceptionType(new LiteralMessage("uaua")).create();
                                })
                        )
                )
                .then(Commands.literal("ping")
                        .requires(CommandUtils.predicate("debug", PermissionLevel.ALL))
                        .executes(wrap(ctx -> CommandUtils.tellFormat(ctx, "Ping: %dms", ctx.getSource().getPlayerOrException().connection.latency())))
                )
                .then(Commands.literal("tickrate")
                        .requires(CommandUtils.predicate("debug", PermissionLevel.ALL))
                        .executes(wrap(ctx -> CommandUtils.tellFormat(ctx, "Immediate tickrate: %#.1f", ctx.getSource().getServer().tickRateManager().tickrate())))
                )
                // ripped straight from minecraft source o:
                .then(Commands.literal("tp")
                        .requires(CommandUtils.predicate("tp", PermissionLevel.MODERATORS))
                        .then(
                                Commands.argument("location", Vec3Argument.vec3())
                                        .executes(
                                                c -> TeleportCommand.teleportToPos(
                                                        c.getSource(),
                                                        Collections.singleton(c.getSource().getEntityOrException()),
                                                        c.getSource().getLevel(),
                                                        Vec3Argument.getCoordinates(c, "location"),
                                                        null,
                                                        null
                                                )
                                        )
                        )
                        .then(
                                Commands.argument("destination", EntityArgument.entity())
                                        .executes(
                                                c -> TeleportCommand.teleportToEntity(
                                                        c.getSource(),
                                                        Collections.singleton(c.getSource().getEntityOrException()),
                                                        EntityArgument.getEntity(c, "destination")
                                                )
                                        )
                        )
                        .then(
                                Commands.argument("targets", EntityArgument.entities())
                                        .then(
                                                Commands.argument("location", Vec3Argument.vec3())
                                                        .executes(
                                                                c -> TeleportCommand.teleportToPos(
                                                                        c.getSource(),
                                                                        EntityArgument.getEntities(c, "targets"),
                                                                        c.getSource().getLevel(),
                                                                        Vec3Argument.getCoordinates(c, "location"),
                                                                        null,
                                                                        null
                                                                )
                                                        )
                                                        .then(
                                                                Commands.argument("rotation", RotationArgument.rotation())
                                                                        .executes(
                                                                                c -> TeleportCommand.teleportToPos(
                                                                                        c.getSource(),
                                                                                        EntityArgument.getEntities(c, "targets"),
                                                                                        c.getSource().getLevel(),
                                                                                        Vec3Argument.getCoordinates(c, "location"),
                                                                                        RotationArgument.getRotation(c, "rotation"),
                                                                                        null
                                                                                )
                                                                        )
                                                        )
                                                        .then(
                                                                Commands.literal("facing")
                                                                        .then(
                                                                                Commands.literal("entity")
                                                                                        .then(
                                                                                                Commands.argument("facingEntity", EntityArgument.entity())
                                                                                                        .executes(
                                                                                                                c -> TeleportCommand.teleportToPos(
                                                                                                                        c.getSource(),
                                                                                                                        EntityArgument.getEntities(c, "targets"),
                                                                                                                        c.getSource().getLevel(),
                                                                                                                        Vec3Argument.getCoordinates(c, "location"),
                                                                                                                        null,
                                                                                                                        new LookAt.LookAtEntity(EntityArgument.getEntity(c, "facingEntity"), EntityAnchorArgument.Anchor.FEET)
                                                                                                                )
                                                                                                        )
                                                                                                        .then(
                                                                                                                Commands.argument("facingAnchor", EntityAnchorArgument.anchor())
                                                                                                                        .executes(
                                                                                                                                c -> TeleportCommand.teleportToPos(
                                                                                                                                        c.getSource(),
                                                                                                                                        EntityArgument.getEntities(c, "targets"),
                                                                                                                                        c.getSource().getLevel(),
                                                                                                                                        Vec3Argument.getCoordinates(c, "location"),
                                                                                                                                        null,
                                                                                                                                        new LookAt.LookAtEntity(
                                                                                                                                                EntityArgument.getEntity(c, "facingEntity"), EntityAnchorArgument.getAnchor(c, "facingAnchor")
                                                                                                                                        )
                                                                                                                                )
                                                                                                                        )
                                                                                                        )
                                                                                        )
                                                                        )
                                                                        .then(
                                                                                Commands.argument("facingLocation", Vec3Argument.vec3())
                                                                                        .executes(
                                                                                                c -> TeleportCommand.teleportToPos(
                                                                                                        c.getSource(),
                                                                                                        EntityArgument.getEntities(c, "targets"),
                                                                                                        c.getSource().getLevel(),
                                                                                                        Vec3Argument.getCoordinates(c, "location"),
                                                                                                        null,
                                                                                                        new LookAt.LookAtPosition(Vec3Argument.getVec3(c, "facingLocation"))
                                                                                                )
                                                                                        )
                                                                        )
                                                        )
                                        )
                                        .then(
                                                Commands.argument("destination", EntityArgument.entity())
                                                        .executes(
                                                                c -> TeleportCommand.teleportToEntity(
                                                                        c.getSource(), EntityArgument.getEntities(c, "targets"), EntityArgument.getEntity(c, "destination")
                                                                )
                                                        )
                                        )
                        )
                )
        );
    }

    private static Command<CommandSourceStack> wrap(Callback command) {
        return (context) -> {
            try {
                command.run(context);
            } catch (Exception e) {
                CommandUtils.tellError(context, e);
            }

            return Command.SINGLE_SUCCESS;
        };
    }

    @FunctionalInterface
    public interface Callback {
        void run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;
    }
}
