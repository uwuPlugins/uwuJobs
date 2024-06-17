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

class UwuJobs : JavaPlugin(), Listener, CommandExecutor {
    override fun onEnable() {
        saveDefaultConfig()

        Config().loadJobs()

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
        val options = DatabaseOptions.builder().sqlite("plugins/uwuJobs/uwu.db").build()
        val db: Database = PooledDatabaseOptions.builder().options(options).createHikariDatabase()
        DB.setGlobalDatabase(db)

    }

    override fun onDisable() {
        DB.close()
        // Plugin shutdown logic
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        for (job in Config().jobs) {}
    }
}
