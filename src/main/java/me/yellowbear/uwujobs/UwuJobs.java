package me.yellowbear.uwujobs;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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
import org.jetbrains.annotations.NotNull;

import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public final class UwuJobs extends JavaPlugin implements Listener, CommandExecutor {

    private static File playerDataFile;
    private static FileConfiguration playerData;
    private BlockSets blockSets = new BlockSets();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new UwuJobs(), this);
//        createHistory();

        // Create database folder
        try {
            File f = new File("plugins/uwuJobs");
            f.mkdir();
        } catch(Exception e){
            e.printStackTrace();
        }

        try (Connection conn = this.connect()){
            Statement statement = conn.createStatement();
            for (Job job : Job.values()) {
                statement.execute(String.format("CREATE TABLE IF NOT EXISTS %s (id TEXT UNIQUE, xp INT, level INT, next INT)", job.name().toLowerCase()));
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(this,() -> {
            try {
                recalculateLevels();
            } catch (IOException e) {
                e.printStackTrace();
            }
        },100,200);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private Connection connect() {
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:plugins/uwuJobs/uwu.db");
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException, SQLException {
        try (Connection conn = this.connect()) {
            Statement statement = conn.createStatement();
            for (Job job : Job.values()) {
                statement.execute(String.format("insert into %s (id, xp, level, next) values ('%s', %s, %s, %s)", job.name().toLowerCase(), event.getPlayer().getUniqueId(), 0, 1, calculateLevelXp(1)));
            }
            statement.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    @EventHandler
    public void onBlockMined(BlockBreakEvent event) throws IOException {
        handleBlockMined(event);
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
        int xp, next = 0;
        try (Connection conn = this.connect()) {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(String.format("select xp, next from %s where id = '%s'", job.name().toLowerCase(), player.getUniqueId()));
            xp = rs.getInt("xp");
            next = rs.getInt("next");
            statement.execute(String.format("update %s set xp = %s where id = '%s'", job.name().toLowerCase(), xp + amount, player.getUniqueId()));
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        player.sendMessage(
                ChatMessageType.ACTION_BAR,
                new TextComponent(job.name() + " " + String.valueOf(xp+amount) + "/" + next + "XP")
        );
    }

    // TODO: Bylo by lepsi tohle zlepsit
    private int calculateLevelXp(int n) {
        return (int) Math.round(100 * (Math.pow(1.05, n)) - 50);
    }

    private void recalculateLevels() throws IOException {
        for (Player player : getServer().getOnlinePlayers()) {
            for (Job job : Job.values()) {
                try (Connection conn = this.connect()) {
                    int xp, level, next = 0;
                    Statement statement = conn.createStatement();
                    ResultSet rs = statement.executeQuery(String.format("select xp, level, next from %s where id = '%s'", job.name().toLowerCase(), player.getUniqueId()));
                    xp = rs.getInt("xp");
                    level = rs.getInt("level");
                    next = rs.getInt("next");
                    level++;
                    if (xp >= next) {
                        statement.executeUpdate(String.format("update %s set level = %s, next = %s where id = '%s'", job.name().toLowerCase(), level, xp + calculateLevelXp(level), player.getUniqueId()));
                    }
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
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
