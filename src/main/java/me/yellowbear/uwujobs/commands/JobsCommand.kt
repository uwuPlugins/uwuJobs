package me.yellowbear.uwujobs.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import co.aikar.idb.DB
import co.aikar.idb.DbRow
import me.yellowbear.uwujobs.Config
import me.yellowbear.uwujobs.UwuJobs
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.awt.print.Paper
import java.sql.SQLException
import java.util.*

@CommandAlias("jobs")
class Jobs : BaseCommand() {
    var msg: MiniMessage = MiniMessage.miniMessage()

    @Default
    fun onDefault(player: Player) {
        var parsed = msg.deserialize(
            "<green>Running <pl> version <v> by <author1> & <author2>",
            Placeholder.component("pl", Component.text("uwuJobs", NamedTextColor.GOLD)),
            Placeholder.component("v", Component.text(UwuJobs().pluginMeta.version, NamedTextColor.GOLD)),
            Placeholder.component("author1", Component.text("yellowbear", NamedTextColor.GOLD)),
            Placeholder.component("author2", Component.text("mapetr", NamedTextColor.GOLD))
        )
        player.sendMessage(parsed)

        for (job in Config.jobs) {
            val row = DB.getFirstRow("SELECT xp FROM ${job.name.lowercase()} WHERE id = '${player.uniqueId}'")
            parsed = msg.deserialize(
                "<gray>You have <xp> XP in proffesion <job>",
                Placeholder.component("xp", Component.text(row.getInt("xp"), NamedTextColor.GOLD)),
                Placeholder.component("job", Component.text(job.name, NamedTextColor.GOLD))
            )
            player.sendMessage(parsed)
        }
    }

    @Subcommand("top")
    @Syntax("<job>")
    @CommandCompletion("@jobs")
    @Description("Shows the top 5 players in a job")
    fun onTop(player: Player, job: String) {
        try {
            // Check if job exists
            if (Config.getJob(job) == null && !job.equals("all", ignoreCase = true)) {
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

                for (jobEnum in Config.jobs) {
                    queryBuilder.append("SELECT id, xp FROM ${jobEnum.name.lowercase()} WHERE NOT xp = 0 UNION ALL ")
                }

                queryBuilder.setLength(queryBuilder.length - " UNION ALL ".length)
                queryBuilder.append(") GROUP BY id ORDER BY xp DESC LIMIT 5")

                UwuJobs().logger.info(queryBuilder.toString())

                rows = DB.getResults(queryBuilder.toString())
            } else {
                rows = DB.getResults("SELECT id, xp FROM ${job.lowercase()} WHERE NOT xp = 0 ORDER BY xp DESC LIMIT 5")
            }

            player.sendMessage(
                msg.deserialize(
                    "<white><bold>Top 5 players in <job>",
                    Placeholder.component("job", Component.text(job, NamedTextColor.GOLD))
                )
            )
            var i = 1
            for (row in rows) {
                val playerLeaderboard = Bukkit.getOfflinePlayer(
                    UUID.fromString(row.getString("id"))
                )
                val playerName = playerLeaderboard.name

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

    //TODO: Add config reload
}
