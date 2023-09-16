package me.yellowbear.uwujobs;

import org.bukkit.block.data.Ageable; //Nezamenovat s Entity.Ageable!!!
import org.bukkit.event.block.BlockBreakEvent;

import java.io.IOException;

import static me.yellowbear.uwujobs.Level.awardXp;

public class Job {
    public static void handleBlockMined(BlockBreakEvent event, BlockSets blockSets) throws IOException {
        Ageable ageable;
        try {
            ageable = (Ageable) event.getBlock().getBlockData();
        } catch (Exception ex) { ageable = null; }
        if (!(ageable == null || ageable.getAge() == 7)) {
            return;
        }
        for (Jobs job : Jobs.values()) {
            if (blockSets.jobsMap.get(job).get(event.getBlock().getType()) != null) {
                awardXp(event.getPlayer(), blockSets.jobsMap.get(job).get(event.getBlock().getType()), job);
            }
        }
    }
}
