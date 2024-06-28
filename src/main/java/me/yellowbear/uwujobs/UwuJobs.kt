package me.yellowbear.uwujobs

import co.aikar.commands.PaperCommandManager
import me.yellowbear.uwujobs.commands.Jobs
import me.yellowbear.uwujobs.jobs.JobPlayer
import org.bukkit.command.CommandExecutor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.sql.SQLException
import me.yellowbear.uwujobs.jobs.Jobs.Companion.jobs as jobsList

class UwuJobs : JavaPlugin(), Listener, CommandExecutor {
    override fun onEnable() {
        Config.loadJobs()
        Config.loadConfig()

        logger.info(jobsList.toString())

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
        Database.connect()

        try {
            val statement = Database.dataSource.connection.createStatement()
            for (job in Config.jobs) {
                statement.execute("CREATE TABLE IF NOT EXISTS ${job.name.lowercase()} (id VARCHAR(36) PRIMARY KEY, xp INT)")
            }
            statement.close()
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    override fun onDisable() {
        Database.dataSource.close()
        // Plugin shutdown logic
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        try {
            val statement = Database.dataSource.connection.createStatement()
            for (job in Config.jobs) {
                val row =
                    statement.executeQuery("SELECT xp FROM ${job.name.lowercase()} WHERE id = '${event.player.uniqueId}'")
                if (!row.next()) {
                    statement.execute("INSERT INTO ${job.name.lowercase()} (id, xp) VALUES ('${event.player.uniqueId}', 0)")
                    jobsList[job.name.lowercase()]?.set(
                        event.player.uniqueId.toString(),
                        JobPlayer(job.name, event.player.uniqueId.toString(), 0)
                    )
                } else {
                    jobsList[job.name.lowercase()]?.set(
                        event.player.uniqueId.toString(),
                        JobPlayer(job.name, event.player.uniqueId.toString(), row.getInt("xp"))
                    )
                }

            }
            statement.close()

            logger.info(jobsList.toString())
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

//    @EventHandler
//    fun onBlockBreak(event: BlockBreakEvent) {
//        //TODO: Check for crop age
//        for (job in Config.jobs) {
//            for (reward in job.rewards) {
//                if (reward.brokenBlocks == null) continue
//                for (block in reward.brokenBlocks) {
//                    if (block == event.block.type.name) {
//                        Level.awardXp(event.player, reward.amount, job)
//                    }
//                }
//            }
//        }
//    }
//
//    @EventHandler
//    fun onBlockPlace(event: BlockPlaceEvent) {
//        for (job in Config.jobs) {
//            for (reward in job.rewards) {
//                if (reward.placedBlocks == null) continue
//                for (block in reward.placedBlocks) {
//                    if (block == event.block.type.name) {
//                        Level.awardXp(event.player, reward.amount, job)
//                    }
//                }
//            }
//        }
//    }
//
//    @EventHandler
//    fun onEntityDeath(event: EntityDeathEvent) {
//        for (job in Config.jobs) {
//            for (reward in job.rewards) {
//                if (reward.killedEntities == null) continue
//                for (entity in reward.killedEntities) {
//                    if (entity == event.entity.type.name) {
//                        Level.awardXp(event.entity.killer, reward.amount, job)
//                    }
//                }
//            }
//        }
//    }
//
//    @EventHandler
//    fun onFertilize(event: BlockFertilizeEvent) {
//        if (event.player == null) return
//        for (job in Config.jobs) {
//            for (reward in job.rewards) {
//                if (reward.fertilizedBlocks == null) continue
//                for (block in reward.fertilizedBlocks) {
//                    if (block == event.block.type.name) {
//                        Level.awardXp(event.player, reward.amount, job)
//                    }
//                }
//            }
//        }
//    }
}
