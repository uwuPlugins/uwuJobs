package me.yellowbear.uwujobs;

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
        // TODO: Implement age of the crop check
        if (blockSets.farmerBlocks.get(event.getBlock().getType()) != null) {
            awardXp(event.getPlayer(), blockSets.farmerBlocks.get(event.getBlock().getType()), Jobs.FARMER);
        }
    }
}
