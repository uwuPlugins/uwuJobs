package me.yellowbear.uwujobs.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import co.aikar.idb.DB
import co.aikar.idb.DbRow
import me.yellowbear.uwujobs.UwuJobs
import me.yellowbear.uwujobs.jobs.BlockBreak
import me.yellowbear.uwujobs.jobs.BlockPlace
import me.yellowbear.uwujobs.jobs.Job
import me.yellowbear.uwujobs.jobs.MobKill
import me.yellowbear.uwujobs.services.ConfigService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.sql.SQLException
import java.util.*

@CommandAlias("jobs")
class JobsCommand : BaseCommand() {
    var msg: MiniMessage = MiniMessage.miniMessage()

    @Default
    fun onDefault(player: Player) {
        var parsed = msg.deserialize(
            "<green>Running <pl> version <v> by <author1> & <author2>.",
            Placeholder.component("pl", Component.text("uwuJobs", NamedTextColor.GOLD)),
            Placeholder.component(
                "v",
                Component.text(UwuJobs().pluginMeta.version, NamedTextColor.GOLD)
            ),
            Placeholder.component("author1", Component.text("yellowbear", NamedTextColor.GOLD)),
            Placeholder.component("author2", Component.text("mapetr", NamedTextColor.GOLD))
        )
        player.sendMessage(parsed)
        try {
            for (job in Job.entries) {
                val row = DB.getFirstRow(
                    String.format(
                        "select xp from %s where id = '%s'",
                        job.name.lowercase(Locale.getDefault()),
                        player.uniqueId
                    )
                )
                parsed = msg.deserialize(
                    "<gray>You have <xp> XP in profession <job>",
                    Placeholder.component("xp", Component.text(row.getInt("xp"), NamedTextColor.GOLD)),
                    Placeholder.component("job", Component.text(job.name, NamedTextColor.GOLD))
                )
                player.sendMessage(parsed)
            }
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }


    @Subcommand("top")
    @Syntax("<job>")
    @CommandCompletion("@jobs")
    @Description("Shows the top 5 players in a job")
    fun onTop(player: Player, job: String) {
        try {
            // Check if job exists
            if (BlockBreak.getJob(job) == null && BlockPlace.getJob(job) == null && MobKill.getJob(job) == null && !job.equals(
                    "all",
                    ignoreCase = true
                )
            ) {
                val parsed = msg.deserialize(
                    "<gray>Job <job> does not exist",
                    Placeholder.component("job", Component.text(job, NamedTextColor.GOLD))
                )
                player.sendMessage(parsed)
                return
            }

            val rows: List<DbRow>

            if (job.equals("all", ignoreCase = true)) {
                val queryBuilder = StringBuilder()
                queryBuilder.append("SELECT id, SUM(xp) AS xp FROM (")

                for (jobEnum in BlockBreak.entries) {
                    queryBuilder.append(
                        String.format(
                            "SELECT id, xp FROM %s UNION ALL ",
                            jobEnum.name.lowercase(Locale.getDefault())
                        )
                    )
                }
                for (jobEnum in BlockPlace.entries) {
                    queryBuilder.append(
                        String.format(
                            "SELECT id, xp FROM %s UNION ALL ",
                            jobEnum.name.lowercase(Locale.getDefault())
                        )
                    )
                }
                for (jobEnum in MobKill.entries) {
                    queryBuilder.append(
                        String.format(
                            "SELECT id, xp FROM %s UNION ALL ",
                            jobEnum.name.lowercase(Locale.getDefault())
                        )
                    )
                }

                queryBuilder.setLength(queryBuilder.length - " UNION ALL ".length)
                queryBuilder.append(") GROUP BY id ORDER BY xp DESC LIMIT 5")

                rows = DB.getResults(queryBuilder.toString())
            } else {
                rows = DB.getResults(
                    String.format(
                        "select id, xp from %s order by xp desc limit 5",
                        job.lowercase(Locale.getDefault())
                    )
                )
            }

            player.sendMessage(
                msg.deserialize(
                    "<white><bold>Top 5 players in <job>",
                    Placeholder.component("job", Component.text(job, NamedTextColor.GOLD))
                )
            )
            var i = 1
            for (row in rows) {
                val playerLeaderboard = Bukkit.getOfflinePlayer(UUID.fromString(row.getString("id")))
                var playerName = playerLeaderboard.name

                val parsed = msg.deserialize(
                    "<gray><rank>. <player>: xp <level>",
                    Placeholder.component("rank", Component.text(i, NamedTextColor.GOLD)),
                    Placeholder.component("player", Component.text(playerName!!, NamedTextColor.WHITE)),
                    Placeholder.component("level", Component.text(row.getInt("xp"), NamedTextColor.GOLD))
                )
                player.sendMessage(parsed)
                i++
            }
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    @Subcommand("reload")
    @Description("Reloads config")
    fun onReload(player: Player) {
        if (ConfigService.loadConfigs()) {
            player.sendMessage("config reloaded")
        }
    }
}
