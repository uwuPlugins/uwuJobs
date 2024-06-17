package me.yellowbear.uwujobs

import co.aikar.commands.PaperCommandManager
import co.aikar.idb.DB
import co.aikar.idb.Database
import co.aikar.idb.DatabaseOptions
import co.aikar.idb.PooledDatabaseOptions
import org.bukkit.command.CommandExecutor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
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

        manager.commandCompletions.registerCompletion("jobs") {
            val jobs: MutableSet<String> = HashSet()
            jobs.add("all")
            jobs
        }


        // Setup database
        val options = DatabaseOptions.builder().sqlite("${this.dataFolder}/uwu.db").build()
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
        val jobs = Config.jobs
        for (job in jobs) {
            for (reward in job.rewards) {
                if (reward.block != null && reward.block == event.block.type.name) {
                    Level.awardXp(event.player, reward.amount, job)
                }
            }
        }
    }
}
