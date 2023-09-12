package me.yellowbear.uwujobs;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.parser.Parser;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public final class UwuJobs extends JavaPlugin implements Listener, CommandExecutor {

    private static File playerDataFile;
    private static FileConfiguration playerData;
    private BlockSets blockSets = new BlockSets();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new UwuJobs(), this);
        createHistory();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this,() -> {
            try {
                recalculateLevels();
            } catch (IOException e) {

            }
        },100,200);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        if (playerData.getKeys(false).contains(event.getPlayer().getName())) {
            System.out.println("Player data loaded");
        } else {
            for (Job job : Job.values()) {
                playerData.set(event.getPlayer().getName()+"."+job.name()+".level", 0);
                playerData.set(event.getPlayer().getName()+"."+job.name()+".xp", 0);
                playerData.set(event.getPlayer().getName()+"."+job.name()+".next", calculateLevelXp(100,1.05,1));
            }
        }
        playerData.save(playerDataFile);
    }

    @EventHandler
    public void onBlockMined(BlockBreakEvent event) throws IOException {
        handleBlockMined(event);
    }

    private void createHistory() {
        playerDataFile = new File(getDataFolder(), "data.yml");
        if (!playerDataFile.exists()) {
            playerDataFile.getParentFile().mkdirs();
            saveResource("data.yml", false);
        }

        playerData = new YamlConfiguration();
        try {
            playerData.load(playerDataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void handleBlockMined(BlockBreakEvent event) throws IOException {
        if (blockSets.minerBlocks.get(event.getBlock().getType()) != null) {
            awardXp(event.getPlayer(), blockSets.minerBlocks.get(event.getBlock().getType()), Job.MINER);
        }
        if (blockSets.lumberBlocks.get(event.getBlock().getType()) != null) {
            awardXp(event.getPlayer(), blockSets.minerBlocks.get(event.getBlock().getType()), Job.LUMBER);
        }
        if (blockSets.farmerBlocks.get(event.getBlock().getType()) != null) {
            awardXp(event.getPlayer(), blockSets.minerBlocks.get(event.getBlock().getType()), Job.FARMER);
        }
    }

    private void awardXp(Player player, int amount, Job job) throws IOException {
        int xp = playerData.getInt(player.getName()+"."+job.name()+".xp");
        String pathString = "."+job.name();
        String completePath = player.getName()+pathString+".xp";
        playerData.set(completePath, playerData.getInt(completePath)+amount);
        playerData.save(playerDataFile);
        player.sendMessage(
                ChatMessageType.ACTION_BAR,
                new TextComponent(job.name() + " " + String.valueOf(xp+amount) + "/" + playerData.getInt(player.getName()+"."+job.name()+".next") + "XP")
        );
    }

    private int calculateLevelXp(int a, double b, int n) {
        return ((int) ((a * (Math.pow(b, n))) - 50));
    } //TODO: trochu to nevychazi

    private void recalculateLevels() throws IOException {
        for (Player player : getServer().getOnlinePlayers()) {
            for (Job job : Job.values()) {
                int xp = playerData.getInt(player.getName()+"."+job.name()+".xp");
                int sum = 0;
                for (int i = 0; i < 201; i++) {
                    int next = calculateLevelXp(100, 1.05, i);
                    sum += next;
                    if (sum > xp) {
                        playerData.set(player.getName()+"."+job.name()+".level", i);
                        playerData.set(player.getName()+"."+job.name()+".next", sum);
                        break;
                    }

                }
            }
            playerData.save(playerDataFile);
        }
    }

    private enum Job {
        MINER,
        LUMBER,
        FARMER
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        return super.onCommand(sender, command, label, args);
    }
}
