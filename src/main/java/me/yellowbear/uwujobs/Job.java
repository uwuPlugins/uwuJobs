package me.yellowbear.uwujobs;

import org.bukkit.Material;
import org.bukkit.block.data.Ageable; //Nezamenovat s Entity.Ageable!!!
import org.bukkit.event.block.BlockBreakEvent;

import java.io.IOException;

import static me.yellowbear.uwujobs.Level.awardXp;

public class Job {
    public static void handleBlockMined(BlockBreakEvent event, BlockSets blockSets) throws IOException {
        if (blockSets.minerBlocks.get(event.getBlock().getType()) != null) {
            awardXp(event.getPlayer(), blockSets.minerBlocks.get(event.getBlock().getType()), Jobs.MINER);
        }
        if (blockSets.lumberBlocks.get(event.getBlock().getType()) != null) {
            awardXp(event.getPlayer(), blockSets.lumberBlocks.get(event.getBlock().getType()), Jobs.LUMBER);
        }
        if (blockSets.farmerBlocks.get(event.getBlock().getType()) != null) {
            try {
                Ageable ageable = (Ageable) event.getBlock().getBlockData();
                System.err.println(ageable.getAge());
                if (ageable.getAge() == 7) {
                    awardXp(event.getPlayer(), blockSets.farmerBlocks.get(event.getBlock().getType()), Jobs.FARMER);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
