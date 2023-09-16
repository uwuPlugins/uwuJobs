package me.yellowbear.uwujobs;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static me.yellowbear.uwujobs.Level.getNextXp;

public final class UwuJobs extends JavaPlugin implements Listener, CommandExecutor {
    private final BlockSets blockSets = new BlockSets(this.getConfig());
    public UwuJobs() throws NoSuchFieldException, IllegalAccessException {
        new me.yellowbear.uwujobs.CommandExecutor();
    }


    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Plugin startup logic
        try {
            getServer().getPluginManager().registerEvents(new UwuJobs(), this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (Connection conn = DatabaseConnector.connect()){
            Statement statement = conn.createStatement();
            for (Jobs job : Jobs.values()) {
                statement.execute(String.format("CREATE TABLE IF NOT EXISTS %s (id TEXT UNIQUE, xp INT, next INT)", job.name().toLowerCase()));
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try (Connection conn = DatabaseConnector.connect()) {
            Statement statement = conn.createStatement();
            for (Jobs job : Jobs.values()) {
                statement.execute(String.format("insert into %s (id, xp, next) values ('%s', %s, %s)", job.name().toLowerCase(), event.getPlayer().getUniqueId(), 1, getNextXp(1)));
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onBlockMined(BlockBreakEvent event) throws IOException {
        Job.handleBlockMined(event, blockSets);
    }
}
