package loch.midnight;

import loch.midnight.Items.MidnightItems;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MidnightEngine implements ModInitializer {

	public static final String MOD_ID = "midnight-engine";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static MinecraftServer serverInstance = null;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			serverInstance = server;
		});

		LOGGER.info("starting chaos fabric mod...");

		MidnightItems.initialize();
		loch.midnight.entities.bosses.boss_creation.BossInitializer.initialize();

	}

	// tell every player online something, and log it
	public static void announce(String message) {

		if (serverInstance != null) {
			var msg = Text.literal(message);
			for (ServerPlayerEntity player : serverInstance.getPlayerManager().getPlayerList())
				player.sendMessage(msg);
			LOGGER.info(message);
		}

	}

	public static Identifier minecraftItem(String item_name) {
		return Identifier.of("minecraft", item_name);
	}
}