package me.yellowbear.uwujobs;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class BlockSets {
    public Map<Jobs, Map<Material, Integer>> jobsMap = new HashMap<>();
    private Map<Material, Integer> minerBlocks = new HashMap<>();
    private Map<Material, Integer> lumberBlocks = new HashMap<>();
    private Map<Material, Integer> farmerBlocks = new HashMap<>();

    public BlockSets(FileConfiguration file) {
        loadConfig(file);
    }

    private void loadConfig(FileConfiguration file) {
        ConfigurationSection miner = file.getConfigurationSection(Jobs.MINER.name());
        ConfigurationSection lumber = file.getConfigurationSection(Jobs.LUMBER.name());
        ConfigurationSection farmer = file.getConfigurationSection(Jobs.FARMER.name());

        if (miner != null) {
            for (String key : miner.getKeys(true)) {
                minerBlocks.put(Material.getMaterial(key), file.getInt(Jobs.MINER.name()+"." + key));
            }
        }

        if (lumber != null) {
            for (String key : lumber.getKeys(true)) {
                lumberBlocks.put(Material.getMaterial(key), file.getInt(Jobs.LUMBER.name()+"." + key));
            }
        }

        if (farmer != null) {
            for (String key : farmer.getKeys(true)) {
                farmerBlocks.put(Material.getMaterial(key), file.getInt(Jobs.FARMER.name()+"." + key));
            }
        }

        jobsMap.put(Jobs.MINER, minerBlocks);
        jobsMap.put(Jobs.LUMBER, lumberBlocks);
        jobsMap.put(Jobs.FARMER, farmerBlocks);

    }
}
