package me.yellowbear.uwujobs;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class BlockSets {
    public Map<Jobs, Map<Material, Integer>> jobsMap = new HashMap<>();
    public BlockSets(FileConfiguration file) {
        loadConfig(file);
    }
    private void loadConfig(FileConfiguration file) {
        for (Jobs job : Jobs.values()) {
            ConfigurationSection section = file.getConfigurationSection(job.name());
            Map<Material, Integer> tempMap = new HashMap<>();
            if (section != null) {
                for (String key : section.getKeys(true)) {
                    tempMap.put(Material.getMaterial(key), file.getInt(job.name()+"." + key));
                }
                jobsMap.put(job,tempMap);
            }
        }
    }
}
