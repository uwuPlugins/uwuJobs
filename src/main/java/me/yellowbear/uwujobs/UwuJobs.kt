package me.yellowbear.uwujobs

import co.aikar.commands.BukkitCommandCompletionContext
import co.aikar.commands.PaperCommandManager
import co.aikar.idb.DB
import co.aikar.idb.Database
import co.aikar.idb.DatabaseOptions
import co.aikar.idb.PooledDatabaseOptions
import me.yellowbear.uwujobs.commands.JobsCommand
import me.yellowbear.uwujobs.jobs.BlockBreak
import me.yellowbear.uwujobs.jobs.BlockPlace
import me.yellowbear.uwujobs.jobs.MobKill
import me.yellowbear.uwujobs.services.ConfigService
import me.yellowbear.uwujobs.services.UpdaterService
import me.yellowbear.uwujobs.services.UpdaterService.checkForUpdates
import org.bukkit.command.CommandExecutor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFertilizeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.sql.SQLException
import java.util.*

class UwuJobs : JavaPlugin(), Listener, CommandExecutor {
    override fun onEnable() {
        try {
            ConfigService.registerCustomConfig("blocks.yml")
            ConfigService.registerCustomConfig("default")
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        ConfigService.registerService(BlockSets(), "blocks.yml")
        ConfigService.registerService(UpdaterService, "default")
        ConfigService.loadConfigs()

        println("up to date?")
        println(this.checkForUpdates())

        try {
            server.pluginManager.registerEvents(UwuJobs(), this)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        // Setup ACF
        val manager = PaperCommandManager(this)

        manager.commandCompletions.registerCompletion("jobs") {
            val jobs: MutableSet<String> = HashSet()
            for (job in BlockBreak.entries) {
                jobs.add(job.name.lowercase(Locale.getDefault()))
            }
            jobs.add("all")
            jobs
        }

        manager.registerCommand(JobsCommand())

        // Setup database
        val options = DatabaseOptions.builder().sqlite("plugins/uwuJobs/uwu.db").build()
        val db: Database = PooledDatabaseOptions.builder().options(options).createHikariDatabase()
        DB.setGlobalDatabase(db)

        try {
            for (job in BlockBreak.entries) {
                DB.executeInsert(
                    String.format(
                        "CREATE TABLE IF NOT EXISTS %s (id TEXT UNIQUE, xp INT, next INT)",
                        job.name.lowercase(Locale.getDefault())
                    )
                )
            }
            for (job in BlockPlace.entries) {
                DB.executeInsert(
                    String.format(
                        "CREATE TABLE IF NOT EXISTS %s (id TEXT UNIQUE, xp INT, next INT)",
                        job.name.lowercase(Locale.getDefault())
                    )
                )
            }
            for (job in MobKill.entries) {
                DB.executeInsert(
                    String.format(
                        "CREATE TABLE IF NOT EXISTS %s (id TEXT UNIQUE, xp INT, next INT)",
                        job.name.lowercase(Locale.getDefault())
                    )
                )
            }
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    override fun onDisable() {
        DB.close()
        // Plugin shutdown logic
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        for (job in BlockBreak.entries) {
            try {
                DB.executeInsert(
                    String.format(
                        "INSERT INTO %s (id, xp) VALUES ('%s', %s)",
                        job.name.lowercase(Locale.getDefault()),
                        event.player.uniqueId,
                        1
                    )
                )
            } catch (e: SQLException) {
                // player already exists
            }
        }
        for (job in BlockPlace.entries) {
            try {
                DB.executeInsert(
                    String.format(
                        "INSERT INTO %s (id, xp) VALUES ('%s', %s)",
                        job.name.lowercase(Locale.getDefault()),
                        event.player.uniqueId,
                        1
                    )
                )
            } catch (e: SQLException) {
                // player already exists
            }
        }
        for (job in MobKill.entries) {
            try {
                DB.executeInsert(
                    String.format(
                        "INSERT INTO %s (id, xp) VALUES ('%s', %s)",
                        job.name.lowercase(Locale.getDefault()),
                        event.player.uniqueId,
                        1
                    )
                )
            } catch (e: SQLException) {
                // player already exists
            }
        }
    }

    @EventHandler
    @Throws(IOException::class)
    fun onBlockMined(event: BlockBreakEvent) {
        Jobs.handleJobEvent(event, BlockSets.breakJobsMap)
    }

    @EventHandler
    @Throws(IOException::class)
    fun onBlockPlace(event: BlockPlaceEvent) {
        Jobs.handleJobEvent(event, BlockSets.placeJobsMap)
    }

    @EventHandler
    @Throws(IOException::class)
    fun onEntityDeath(event: EntityDeathEvent) {
        Jobs.handleJobEvent(event, BlockSets.killJobsMap)
    }

    @EventHandler
    fun onFertilize(event: BlockFertilizeEvent) {
        Jobs.handleJobEvent(event)
    }
}
