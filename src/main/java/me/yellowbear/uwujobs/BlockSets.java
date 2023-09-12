package me.yellowbear.uwujobs;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockSets {
    public Map<Material, Integer> minerBlocks = new HashMap<Material, Integer>();
    public Map<Material, Integer> lumberBlocks = new HashMap<>();
    public Map<Material, Integer> farmerBlocks = new HashMap<>();
    public BlockSets() throws NullPointerException {
        minerBlocks.put(Material.DIAMOND_ORE, 5);
        minerBlocks.put(Material.STONE, 1);
    }
}
