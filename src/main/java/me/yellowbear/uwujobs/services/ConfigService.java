package me.yellowbear.uwujobs.services;

import me.yellowbear.uwujobs.BlockSets;
import me.yellowbear.uwujobs.UwuJobs;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigService {
    private static final Plugin plugin = UwuJobs.getPlugin(UwuJobs.class);
    private static FileConfiguration configuration;
    public static FileConfiguration getConfiguration() {
        return configuration;
    }
    public static void loadConfig() {
        plugin.reloadConfig();
        plugin.saveDefaultConfig();
        configuration = plugin.getConfig();
        plugin.saveConfig();
        BlockSets.loadConfig(configuration);
    }
}
