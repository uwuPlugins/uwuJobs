package me.yellowbear.uwujobs;

import me.yellowbear.uwujobs.interfaces.IConfigurableService;
import me.yellowbear.uwujobs.jobs.BlockBreak;
import me.yellowbear.uwujobs.jobs.BlockPlace;
import me.yellowbear.uwujobs.jobs.MobKill;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class BlockSets implements IConfigurableService {
    @Override
    public void reloadConfig(FileConfiguration file) {
        loadConfig(file);
    }
    public static  Map<BlockBreak, Map<Material, Integer>> breakJobsMap = new HashMap<>();
    public static  Map<BlockPlace, Map<Material, Integer>> placeJobsMap = new HashMap<>();
    public static  Map<MobKill, Map<EntityType, Integer>> killJobsMap = new HashMap<>();
    public static void loadConfig(FileConfiguration file) {
        Map<BlockBreak, Map<Material, Integer>> breakMap = new HashMap<>();
        for (BlockBreak job : BlockBreak.values()) {
            ConfigurationSection section = file.getConfigurationSection(job.name());
            Map<Material, Integer> tempMap = new HashMap<>();
            if (section != null) {
                for (String key : section.getKeys(true)) {
                    tempMap.put(Material.getMaterial(key), file.getInt(job.name()+"." + key));
                }
                breakMap.put(job,tempMap);
            }
        }
        breakJobsMap = breakMap;

        Map<BlockPlace, Map<Material, Integer>> placeMap = new HashMap<>();
        for (BlockPlace job : BlockPlace.values()) {
            ConfigurationSection section = file.getConfigurationSection(job.name());
            Map<Material, Integer> tempMap = new HashMap<>();
            if (section != null) {
                for (String key : section.getKeys(true)) {
                    tempMap.put(Material.getMaterial(key), file.getInt(job.name()+"." + key));
                }
                placeMap.put(job,tempMap);
            }
        }
        placeJobsMap = placeMap;

        Map<MobKill, Map<EntityType, Integer>> killMap = new HashMap<>();
        for (MobKill job : MobKill.values()) {
            ConfigurationSection section = file.getConfigurationSection(job.name());
            Map<EntityType, Integer> tempMap = new HashMap<>();
            if (section != null) {
                for (String key : section.getKeys(true)) {
                    tempMap.put(EntityType.fromName(key), file.getInt(job.name()+"." + key));
                }
                killMap.put(job,tempMap);
            }
        }
        killJobsMap = killMap;
    }
}
