package narakomii.kotweaks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.util.Collections;

public class DebugCommands {
    private static final boolean ENABLED = true;

    public static void init() {
        //TODO there should be a way to enable this only for debug builds...
        if (ENABLED) {
            CommandUtils.addCommand(registry -> Commands.literal(";")
                    .requires(source -> {
                        ServerPlayer player = source.getPlayer();
                        if (player == null) return false;
                        return (source.getServer().isSingleplayer() && !source.getServer().isPublished()) || CommandUtils.enabled(source);
                    })
                    .then(Commands.literal("item")
                            .then(Commands.literal("add")
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
                                    .executes(wrap(ctx -> {
                                        Inventory inv = ctx.getSource().getPlayerOrException().getInventory();
                                        int slot = inv.getSelectedSlot();
                                        inv.removeItemNoUpdate(slot);
                                    }))
                            )
                            .then(Commands.literal("dupe")
                                    .executes(wrap(ctx -> {
                                        Inventory inv = ctx.getSource().getPlayerOrException().getInventory();
                                        ItemStack stack = getSelectedItem(inv);
                                        ItemUtils.give(inv, stack, stack.getCount());
                                    }))
                            )
                            .then(Commands.literal("max")
                                    .executes(wrap(ctx -> getSelectedItem(ctx.getSource().getPlayerOrException().getInventory()).setCount(99)))
                            )
                            .then(Commands.literal("count")
                                    .then(Commands.argument("count", IntegerArgumentType.integer(1, 99))
                                            .executes(wrap(ctx -> getSelectedItem(ctx.getSource().getPlayerOrException().getInventory()).setCount(IntegerArgumentType.getInteger(ctx, "count"))))
                                    )
                            )
                            .then(Commands.literal("modify")
                                    //.then(Commands.argument("nbt", DataComponentPatch))
                            )
                            .then(Commands.literal("info")
                                    .executes(wrap(ctx -> {
                                        var item = getSelectedItem(ctx.getSource().getPlayerOrException().getInventory());
                                        //CommandUtils.tellFormat(ctx, "%s", BuiltInRegistries.ITEM.getKey(item.getItem()));
                                        CommandUtils.tell(ctx, CommandUtils.formatItem(item, true));
                                    }))
                            )
                            .then(Commands.literal("info-long")
                                    .executes(wrap(ctx -> {
                                        var item = getSelectedItem(ctx.getSource().getPlayerOrException().getInventory());
                                        CommandUtils.tell(ctx, CommandUtils.formatItem(item, false));
                                    }))
                            )
                    )
                    .then(Commands.literal("gm")
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
                            .executes(wrap(ctx -> CommandUtils.tellFormat(ctx, "Ping: %dms", ctx.getSource().getPlayerOrException().connection.latency())))
                    )
                    .then(Commands.literal("tickrate")
                            .executes(wrap(ctx -> CommandUtils.tellFormat(ctx, "Immediate tickrate: %#.1f", ctx.getSource().getServer().tickRateManager().tickrate())))
                    )
                    // ripped straight from minecraft source o:
                    .then(Commands.literal("tp")
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
    }

    private static ItemStack getSelectedItem(Inventory inventory) {
        return inventory.getItem(inventory.getSelectedSlot());
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
