package me.yellowbear.uwujobs;

import co.aikar.commands.PaperCommandManager;
import co.aikar.idb.*;
import me.yellowbear.uwujobs.commands.JobsCommand;
import me.yellowbear.uwujobs.jobs.BlockBreak;
import me.yellowbear.uwujobs.jobs.BlockPlace;
import me.yellowbear.uwujobs.jobs.MobKill;
import me.yellowbear.uwujobs.services.ConfigService;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public final class UwuJobs extends JavaPlugin implements Listener, CommandExecutor {
    private BlockSets blockSets = new BlockSets();
    @Override
    public void onEnable() {
        try {
            ConfigService.registerCustomConfig("blocks.yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ConfigService.registerService(blockSets, "blocks.yml");
        ConfigService.loadConfigs();
        try {
            getServer().getPluginManager().registerEvents(new UwuJobs(), this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Setup ACF
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.getCommandCompletions().registerCompletion("jobs", c -> {
            Set<String> jobs = new HashSet<>();
            for (BlockBreak job : BlockBreak.values()) {
                jobs.add(job.name().toLowerCase());
            }
            return jobs;
        });

        manager.registerCommand(new JobsCommand());

        // Setup database
        DatabaseOptions options = DatabaseOptions.builder().sqlite("plugins/uwuJobs/uwu.db").build();
        Database db = PooledDatabaseOptions.builder().options(options).createHikariDatabase();
        DB.setGlobalDatabase(db);

        try {
            for (BlockBreak job : BlockBreak.values()) {
                DB.executeInsert(String.format("CREATE TABLE IF NOT EXISTS %s (id TEXT UNIQUE, xp INT, next INT)", job.name().toLowerCase()));
            }
            for (BlockPlace job : BlockPlace.values()) {
                DB.executeInsert(String.format("CREATE TABLE IF NOT EXISTS %s (id TEXT UNIQUE, xp INT, next INT)", job.name().toLowerCase()));
            }
            for (MobKill job : MobKill.values()) {
                DB.executeInsert(String.format("CREATE TABLE IF NOT EXISTS %s (id TEXT UNIQUE, xp INT, next INT)", job.name().toLowerCase()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        DB.close();
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (BlockBreak job : BlockBreak.values()) {
            try {
                DB.executeInsert(String.format("INSERT INTO %s (id, xp) VALUES ('%s', %s)", job.name().toLowerCase(), event.getPlayer().getUniqueId(), 1));
            } catch (SQLException e) {
                // player already exists
            }
        }
        for (BlockPlace job : BlockPlace.values()) {
            try {
                DB.executeInsert(String.format("INSERT INTO %s (id, xp) VALUES ('%s', %s)", job.name().toLowerCase(), event.getPlayer().getUniqueId(), 1));
            } catch (SQLException e) {
                // player already exists
            }
        }
        for (MobKill job : MobKill.values()) {
            try {
                DB.executeInsert(String.format("INSERT INTO %s (id, xp) VALUES ('%s', %s)", job.name().toLowerCase(), event.getPlayer().getUniqueId(), 1));
            } catch (SQLException e) {
                // player already exists
            }
        }

        /*String[] board = new String[] {
          "my",
          "awesome" ,
          "scoreboard",
          ":3"
        };
        ScoreboardController controller = new ScoreboardController(event.getPlayer(), board);*/
    }

    @EventHandler
    public void onBlockMined(BlockBreakEvent event) throws IOException {
        Job.handleJobEvent(event, BlockSets.breakJobsMap);
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) throws IOException {
        Job.handleJobEvent(event, BlockSets.placeJobsMap);
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) throws IOException {
        Job.handleJobEvent(event, BlockSets.killJobsMap);
    }
}
