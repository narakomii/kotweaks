package narakomii.kotweaks;

import narakomii.kotweaks.commands.DebugCommands;
import narakomii.kotweaks.commands.MiscCommands;
import narakomii.kotweaks.storage.world.CropExpController;
import narakomii.kotweaks.storage.world.DimensionBedController;
import narakomii.kotweaks.storage.player.LocatorController;
import narakomii.kotweaks.game.ModEnchantments;
import narakomii.kotweaks.game.ModItems;
import narakomii.kotweaks.utils.CommandUtils;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KoTweaks implements ModInitializer {
	public static final boolean DEBUG = false;

	public static final String MOD_ID = "kotweaks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static String idString(String path) {
		return MOD_ID + ":" + path;
	}
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static final DimensionBedController dimensionBedController = new DimensionBedController();
	public static final CropExpController cropExpController = new CropExpController();
	public static final LocatorController locatorController = new LocatorController();

	public static final File FOLDER = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).toFile();

	//TODO add /locator option to change settings of another player

	//TODO what class/method did i find in another mod and copy? i need to credit it

	public static HolderLookup.Provider registryLookup;

	@Override
	public void onInitialize() {
		CommandUtils.init();
		DebugCommands.init();
		MiscCommands.init();

		ModEnchantments.init();
		ModItems.init();

		if (!FOLDER.isDirectory())
			if (!FOLDER.mkdirs())
				throw new RuntimeException("Unable to create folder: " + FOLDER.getAbsolutePath());

		ServerLifecycleEvents.SERVER_STARTING.register(this::reload);
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> reload(server));
	}

	//TODO add custom command for this, or just watch the config files?
	private void reload(MinecraftServer server) {
		dimensionBedController.reload(server);
		cropExpController.reload(server);
		locatorController.reload(server);

		registryLookup = server.registryAccess();
	}

	private static int flag1 = 0;
	static {
		Reflections reflections = new Reflections("net.minecraft.network.protocol");
		reflections.getSubTypesOf(Packet.class).forEach(packetClass -> {
			List<Field> fields = new ArrayList<>();
			boolean flag1 = false;
			if (packetClass.isRecord()) {
				for (RecordComponent field : packetClass.getRecordComponents()) {
					Class<?> clazz = field.getType();
					if (
							clazz.equals(ItemStack.class)
									|| clazz.equals(Item.class)
									|| (clazz.equals(Holder.class) && field.getName().contains("item"))
									|| (clazz.equals(List.class) && field.getName().contains("item"))
									|| clazz.equals(Inventory.class)
					) {
						flag1 = true;
						break;
					}
				}
				if (flag1 && DEBUG)
					LOGGER.info("{}: {}", packetClass.getSimpleName(), Arrays.stream(packetClass.getRecordComponents()).map(co -> co.getType().getSimpleName() + " " + co.getName()).collect(Collectors.joining(", ")));
			} else {
				var constructors = packetClass.getConstructors();
				if (constructors.length > 0) {
					for (Parameter field : constructors[0].getParameters()) {
						Class<?> clazz = field.getType();
						if (
								clazz.equals(ItemStack.class)
										|| clazz.equals(Item.class)
										|| (clazz.equals(Holder.class) && field.getName().contains("item"))
										|| (clazz.equals(List.class) && field.getName().contains("item"))
										|| clazz.equals(Inventory.class)
						) {
							if (field.accessFlags().stream().allMatch(f -> f.equals(AccessFlag.PRIVATE) || f.equals(AccessFlag.FINAL))) {
								flag1 = true;
								break;
							}
						}
					}
				}
				if (flag1 && DEBUG)
					LOGGER.info("{}: {}", packetClass.getSimpleName(), Arrays.stream(constructors[0].getParameters()).map(co -> co.getType().getSimpleName() + " " + co.getName()).collect(Collectors.joining(", ")));
			}
		});
	}
}