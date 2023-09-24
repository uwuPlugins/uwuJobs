package me.yellowbear.uwujobs.services;

import me.yellowbear.uwujobs.BlockSets;
import me.yellowbear.uwujobs.UwuJobs;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigService {
    private static final Plugin plugin = UwuJobs.getPlugin(UwuJobs.class);
    private static FileConfiguration configuration;
    private static FileConfiguration blockConfiguration;
    public static FileConfiguration getConfiguration() {
        return configuration;
    }
    public static FileConfiguration getBlockConfiguration() {
        return blockConfiguration;
    }

    public static void loadConfig() {
        plugin.reloadConfig();
        plugin.saveDefaultConfig();
        blockConfiguration = createCustomConfig("blocks.yml");
        configuration = plugin.getConfig();
        plugin.saveConfig();
        BlockSets.loadConfig(blockConfiguration);
    }
    private static FileConfiguration createCustomConfig(String fileName) {
        FileConfiguration customConfig = new YamlConfiguration();
        try {
            customConfig.load(createCustomConfigFile(fileName));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return customConfig;
    }
    private static File createCustomConfigFile(String fileName) {
        File customConfigFile = new File(plugin.getDataFolder(), fileName);
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            plugin.saveResource(fileName, false);
        }
        return customConfigFile;
    }
}
