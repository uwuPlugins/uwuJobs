package me.yellowbear.uwujobs

import co.aikar.commands.PaperCommandManager
import co.aikar.idb.DB
import co.aikar.idb.Database
import co.aikar.idb.DatabaseOptions
import co.aikar.idb.PooledDatabaseOptions
import me.yellowbear.uwujobs.commands.Jobs
import org.bukkit.command.CommandExecutor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.sql.SQLException

class UwuJobs : JavaPlugin(), Listener, CommandExecutor {
    override fun onEnable() {
        saveDefaultConfig()

        Config.loadJobs()

        try {
            server.pluginManager.registerEvents(UwuJobs(), this)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        // Setup ACF
        val manager = PaperCommandManager(this)

        manager.registerCommand(Jobs())

        manager.commandCompletions.registerCompletion("jobs") {
            val jobs: MutableSet<String> = HashSet()
            for (job in Config.jobs) {
                jobs.add(job.name.lowercase())
            }
            jobs.add("all")
            jobs
        }

        // Setup database
        val options = DatabaseOptions.builder().sqlite("${this.dataFolder}/uwu.db").logger(UwuJobs().logger).build()
        val db: Database = PooledDatabaseOptions.builder().options(options).createHikariDatabase()
        DB.setGlobalDatabase(db)

        try {
            for (job in Config.jobs) {
                DB.executeInsert("CREATE TABLE IF NOT EXISTS ${job.name.lowercase()} (id TEXT UNIQUE, xp INT)")
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
        for (job in Config.jobs) {
            try {
                DB.executeInsert("INSERT OR IGNORE INTO ${job.name.lowercase()} (id, xp) VALUES ('${event.player.uniqueId}', 0)")
            } catch (e: SQLException) {
                throw RuntimeException(e)
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        for (job in Config.jobs) {
            for (reward in job.rewards) {
                if (reward.brokenBlocks == null) continue
                for (block in reward.brokenBlocks) {
                    if (block == event.block.type.name) {
                        Level.awardXp(event.player, reward.amount, job)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        for (job in Config.jobs) {
            for (reward in job.rewards) {
                if (reward.placedBlocks == null) continue
                for (block in reward.placedBlocks) {
                    if (block == event.block.type.name) {
                        Level.awardXp(event.player, reward.amount, job)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        for (job in Config.jobs) {
            for (reward in job.rewards) {
                if (reward.killedEntities == null) continue
                for (entity in reward.killedEntities) {
                    if (entity == event.entity.type.name) {
                        Level.awardXp(event.entity.killer, reward.amount, job)
                    }
                }
            }
        }
    }
}
