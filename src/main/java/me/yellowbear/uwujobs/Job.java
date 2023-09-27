package me.yellowbear.uwujobs;

import me.yellowbear.uwujobs.jobs.BlockBreak;
import me.yellowbear.uwujobs.jobs.BlockPlace;
import me.yellowbear.uwujobs.jobs.MobKill;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable; //Nezamenovat s Entity.Ageable!!!
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.IOException;
import java.util.Map;

import static me.yellowbear.uwujobs.Level.awardXp;

public class Job {
    public static void handleJobEvent(BlockBreakEvent event, Map<BlockBreak, Map<Material, Integer>> jobsMap) throws IOException {
        Ageable ageable;
        try {
            ageable = (Ageable) event.getBlock().getBlockData();
        } catch (Exception ex) { ageable = null; }
        if (!(ageable == null || ageable.getAge() == 7)) {
            return;
        }
        for (BlockBreak job : BlockBreak.values()) {
            if (jobsMap.get(job).get(event.getBlock().getType()) != null) {
                awardXp(event.getPlayer(), jobsMap.get(job).get(event.getBlock().getType()), job);
            }
        }
    }
    public static void handleJobEvent(BlockPlaceEvent event, Map<BlockPlace, Map<Material, Integer>> jobsMap) throws IOException {
        for (BlockPlace job : BlockPlace.values()) {
            if (jobsMap.get(job).get(event.getBlock().getType()) != null) {
                awardXp(event.getPlayer(), jobsMap.get(job).get(event.getBlock().getType()), job);
            }
        }
    }
    public static void handleJobEvent(EntityDeathEvent event, Map<MobKill, Map<EntityType, Integer>> jobsMap) throws IOException {
        for (MobKill job : MobKill.values()) {
            if (jobsMap.get(job).get(event.getEntity().getType()) != null) {
                awardXp(event.getEntity().getKiller(), jobsMap.get(job).get(event.getEntity().getType()), job);
            }
        }
    }
}
