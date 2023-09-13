package me.yellowbear.uwujobs;

import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockSets {
    public Map<Material, Integer> minerBlocks = new HashMap<>();
    public Map<Material, Integer> lumberBlocks = new HashMap<>();
    public Map<Material, Integer> farmerBlocks = new HashMap<>();

    public BlockSets(FileConfiguration file) {
        loadConfig(file);
    }

    private void loadConfig(FileConfiguration file) {
        ConfigurationSection miner = file.getConfigurationSection(UwuJobs.Job.MINER.name());
        ConfigurationSection lumber = file.getConfigurationSection(UwuJobs.Job.LUMBER.name());
        ConfigurationSection farmer = file.getConfigurationSection(UwuJobs.Job.FARMER.name());

        if (miner != null) {
            for (String key : miner.getKeys(true)) {
                minerBlocks.put(Material.getMaterial(key), file.getInt(UwuJobs.Job.MINER.name()+"." + key));
            }
        }

        if (lumber != null) {
            for (String key : lumber.getKeys(true)) {
                lumberBlocks.put(Material.getMaterial(key), file.getInt(UwuJobs.Job.LUMBER.name()+"." + key));
            }
        }

        if (farmer != null) {
            for (String key : farmer.getKeys(true)) {
                farmerBlocks.put(Material.getMaterial(key), file.getInt(UwuJobs.Job.FARMER.name()+"." + key));
            }
        }
    }
}
