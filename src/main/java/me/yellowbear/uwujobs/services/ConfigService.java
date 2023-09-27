package me.yellowbear.uwujobs.services;

import me.yellowbear.uwujobs.BlockSets;
import me.yellowbear.uwujobs.UwuJobs;
import me.yellowbear.uwujobs.interfaces.IConfigurableService;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigService {
    private static final Plugin plugin = UwuJobs.getPlugin(UwuJobs.class);
    private static Map<String,FileConfiguration> configurations = new HashMap<String,FileConfiguration>();
    private static Map<IConfigurableService, String> registeredServices = new HashMap<IConfigurableService,String>();
    public static FileConfiguration getConfiguration(String s) {
        return configurations.get(s);
    }


    public static boolean loadConfigs() {
        plugin.reloadConfig();
        plugin.saveDefaultConfig();
        plugin.saveConfig();
        reloadConfigs();
        reloadServices();
        return true; //Success
    }

    public static void registerCustomConfig(String fileName) throws IOException {
        configurations.put(fileName, createCustomConfig(fileName));
    }

    private static void reloadConfigs() {
        configurations.put("default", plugin.getConfig());
        configurations.replaceAll((s, v) -> {
            try {
                return createCustomConfig(s);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public static void registerService(IConfigurableService service, String configName) {
        registeredServices.put(service, configName);
        service.reloadConfig(configurations.get(configName));
    }
    public static void reloadServices() {
        System.out.println(configurations.get("blocks.yml").getValues(true));

        for (IConfigurableService service : registeredServices.keySet()) {
            service.reloadConfig(configurations.get(registeredServices.get(service)));
        }
    }
    private static FileConfiguration createCustomConfig(String fileName) throws IOException {
        if (fileName.equals("default")) { return plugin.getConfig(); }
        FileConfiguration customConfig = new YamlConfiguration();
        File file = createCustomConfigFile(fileName);
        try {
            customConfig.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        customConfig.save(file);
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
