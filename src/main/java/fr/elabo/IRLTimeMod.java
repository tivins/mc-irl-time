package fr.elabo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;

public class IRLTimeMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("irl-time-mod");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "irl-time.json");
	private JsonObject config;
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("IRL Time Mod Initialized");
		LOGGER.info("Config dir is {}.", FabricLoader.getInstance().getConfigDir());

		this.initializeConfigFile();
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
	}

	private void initializeConfigFile() {
		if (!CONFIG_FILE.exists()) {
			createDefaultConfig();
		}
		// Read the config file
		config = readConfig();
		if (config != null) {
			// Process the config data as needed
            LOGGER.debug("Config loaded: {}", config);
		} else {
			LOGGER.error("Failed to load config.");
		}
	}

	private void createDefaultConfig() {
		JsonObject defaultConfig = new JsonObject();
		defaultConfig.addProperty("format", "hh:mm a");
		defaultConfig.addProperty("prefix", "It's ");
		defaultConfig.addProperty("overlay", false);
		writeConfig(defaultConfig);
	}

	private void writeConfig(JsonObject config) {
		try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
			GSON.toJson(config, writer);
		} catch (IOException e) {
			LOGGER.error("Failed to write config file", e);
		}
	}

	private JsonObject readConfig() {
		try (FileReader reader = new FileReader(CONFIG_FILE)) {
			return GSON.fromJson(reader, JsonObject.class);
		} catch (IOException e) {
			LOGGER.error("Failed to write config file", e);
			return null;
		}
	}

	private void onServerStarted(MinecraftServer server) {
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimeTask(server,
				config.get("format").getAsString(),
				config.get("prefix").getAsString(),
				config.get("overlay").getAsBoolean()
		), 0, 10000);
	}
}