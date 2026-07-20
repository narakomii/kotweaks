package narakomii.kotweaks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.codedsakura.blossom.lib.permissions.Permissions;
import narakomii.kotweaks.KoTweaks;
import narakomii.kotweaks.storage.entity.CustomEntityDataAccess;
import narakomii.kotweaks.game.ModItems;
import narakomii.kotweaks.utils.CommandUtils;
import narakomii.kotweaks.utils.ItemUtils;
import narakomii.kotweaks.utils.TypeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class MiscCommands {
    private static final SimpleCommandExceptionType NONLIVING_ENTITY = new SimpleCommandExceptionType(Component.literal("Targets must be living"));
    private static final SimpleCommandExceptionType OUTOFREACH_ENTITY = new SimpleCommandExceptionType(Component.literal("Target is too far away"));
    private static final SimpleCommandExceptionType EMPTY_ITEM = new SimpleCommandExceptionType(Component.literal("Can't transfer nothing"));
    private static final SimpleCommandExceptionType NOSPACE_ENTITY = new SimpleCommandExceptionType(Component.literal("Target's inventory is too full"));

    private static final double MAX_RANGE = 10;

    //TODO /transfer, moves the selected item to another person's inventory (fail if they're out of range or if their inventory is full)
    public static void init() {
        CommandUtils.addCommand(registry -> Commands.literal("maul")
                .requires(Permissions.require("kotweaks.maul", PermissionLevel.GAMEMASTERS.id()).or(CommandUtils::enabled))
                .then(
                        Commands.argument("target", EntityArgument.entities())
                                .executes(context -> {
                                    try {
                                        for (Entity target : EntityArgument.getEntities(context, "target")) {
                                            if (!(target instanceof LivingEntity))
                                                throw NONLIVING_ENTITY.create();
                                        }

                                        for (Entity target : EntityArgument.getEntities(context, "target")) {
                                            maul((LivingEntity) target);
                                        }

                                    } catch (Exception e) {
                                        KoTweaks.LOGGER.error(CommandUtils.formatError("Internal error during command", e));
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                )
        );
        CommandUtils.addCommand(registry -> Commands.literal("rock")
                .requires(Permissions.require("kotweaks.rock", PermissionLevel.GAMEMASTERS.id()).or(CommandUtils::enabled))
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    CommandUtils.tell(context, Component.literal("You have thrown a rock, but you have also summoned a meteor!").withStyle(ChatFormatting.BLUE));
                    List<ServerPlayer> players = source.getServer().getPlayerList().getPlayers();
                    int meteorIndex = new Random().nextInt(Math.min(players.size(), 2));

                    for (int i = 0; i < players.size(); i++) {
                        ServerPlayer player = players.get(i);
                        if (player == null) continue;
                        if (i == meteorIndex) {
                            ItemUtils.giveAsDrop(player, ModItems.METEOR.getDefaultInstance());
                        } else {
                            ItemUtils.giveAsDrop(player, ModItems.ROCK.getDefaultInstance());
                        }
                    }

                    return Command.SINGLE_SUCCESS;
                })
        );
        CommandUtils.addCommand(registry -> Commands.literal("handto")
                .requires(Permissions.require("kotweaks.transfer", PermissionLevel.ALL.id()).or(CommandUtils::enabled))
                .then(
                        Commands.argument("target", EntityArgument.player())
                                .executes(context -> transfer(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "target"), false))
                                .then(
                                        Commands.literal("force")
                                                .executes(context -> transfer(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "target"), true))
                                )
                )
        );
    }

    private static int transfer(ServerPlayer source, ServerPlayer target, boolean force) throws CommandSyntaxException {
        if (!source.level().equals(target.level()) || source.distanceToSqr(target) > MAX_RANGE * MAX_RANGE)
            throw OUTOFREACH_ENTITY.create();

        var item = source.getInventory().getItem(source.getInventory().getSelectedSlot());
        if (item == null || item.isEmpty())
            throw EMPTY_ITEM.create();
        item = item.copy();

        if (!force && ItemUtils.totalSpaceForItem(target.getInventory(), item) < item.getCount())
            throw NOSPACE_ENTITY.create();

        source.getInventory().removeItem(source.getInventory().getSelectedSlot(), item.count());
        ItemUtils.giveAsDrop(target, item);

        return Command.SINGLE_SUCCESS;
    }

    private static void maul(LivingEntity target) throws CommandSyntaxException {
        for (int i = 0; i < 4; i++) {
            Entity mauler = SummonCommand.createEntity(
                    target.createCommandSourceStackForNameResolution((ServerLevel) target.level()),
                    TypeUtils.holder(target.level().registryAccess(), Registries.ENTITY_TYPE, "minecraft:zombie"),
                    target.position().add(new Vec3(
                            i == 0 || i == 1 ? 1 : -1,
                            0,
                            i == 0 || i == 2 ? 1 : -1
                    ).scale(0.3)),
                    maulerTag,
                    false
            );

            CustomEntityDataAccess.set(mauler, CustomEntityDataAccess.Key.Type, "mauler");
            CustomEntityDataAccess.set(mauler, CustomEntityDataAccess.Key.ForcedTarget, target.getStringUUID());
        }

        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 30, 0, true, false));
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 2, true, false));
    }
    private static final CompoundTag maulerTag;
    static {
        try {
            maulerTag = TagParser.parseCompoundFully("{PersistenceRequired:1b,DeathLootTable:\"minecraft:empty\",CanPickUpLoot:0b,equipment:{feet:{id:\"minecraft:diamond_boots\",count:1,components:{\"minecraft:enchantments\":{\"feather_falling\":4,\"protection\":5,\"thorns\":5,\"depth_strider\":3,\"soul_speed\":3,\"mending\":1,\"unbreaking\":3},\"minecraft:unbreakable\":{}}},legs:{id:\"minecraft:diamond_leggings\",count:1,components:{\"minecraft:enchantments\":{\"protection\":5,\"thorns\":5,\"swift_sneak\":3,\"mending\":1,\"unbreaking\":3},\"minecraft:unbreakable\":{}}},chest:{id:\"minecraft:diamond_chestplate\",count:1,components:{\"minecraft:enchantments\":{\"protection\":5,\"thorns\":5,\"mending\":1,\"unbreaking\":3},\"minecraft:unbreakable\":{}}},head:{id:\"minecraft:diamond_helmet\",count:1,components:{\"minecraft:enchantments\":{\"protection\":5,\"thorns\":5,\"aqua_affinity\":1,\"respiration\":3,\"mending\":1,\"unbreaking\":3},\"minecraft:unbreakable\":{}}},mainhand:{id:\"minecraft:diamond_sword\",count:1,components:{\"minecraft:enchantments\":{\"fire_aspect\":2,\"looting\":3,\"sharpness\":7,\"knockback\":1,\"sweeping_edge\":3,\"mending\":1,\"unbreaking\":3},\"minecraft:unbreakable\":{}}}},drop_chances:{feet:0.000,legs:0.000,chest:0.000,head:0.000,mainhand:0.000},attributes:[{id:\"minecraft:spawn_reinforcements\",base:0},{id:\"minecraft:movement_speed\",base:0.35},{id:\"minecraft:scale\",base:1.2}]}");
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
