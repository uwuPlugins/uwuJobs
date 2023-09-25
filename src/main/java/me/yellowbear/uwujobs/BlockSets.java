package me.yellowbear.uwujobs;

import me.yellowbear.uwujobs.interfaces.IConfigurableService;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class BlockSets implements IConfigurableService {
    @Override
    public void reloadConfig(FileConfiguration file) {
        loadConfig(file);
    }
    public static  Map<Jobs, Map<Material, Integer>> jobsMap = new HashMap<>();
    public static void loadConfig(FileConfiguration file) {
        Map<Jobs, Map<Material, Integer>> newMap = new HashMap<>();
        for (Jobs job : Jobs.values()) {
            ConfigurationSection section = file.getConfigurationSection(job.name());
            Map<Material, Integer> tempMap = new HashMap<>();
            if (section != null) {
                for (String key : section.getKeys(true)) {
                    tempMap.put(Material.getMaterial(key), file.getInt(job.name()+"." + key));
                }
                newMap.put(job,tempMap);
            }
        }
        jobsMap = newMap;
    }
}
