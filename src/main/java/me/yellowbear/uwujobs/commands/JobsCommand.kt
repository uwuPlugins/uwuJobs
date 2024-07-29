package me.yellowbear.uwujobs.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.yellowbear.uwujobs.Config
import me.yellowbear.uwujobs.Database
import me.yellowbear.uwujobs.UwuJobs
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

/**
 * General command class for job related purposes.
 */
@CommandAlias("jobs") //Add "/jobs" alias to the command
class Jobs : BaseCommand() { //Inherit basic command properties
    /**
     * Object representing the final message returned to the command sender.
     */
    private var msg: MiniMessage = MiniMessage.miniMessage()

    /**
     * Invoked when the '/jobs' command is used without any arguments.
     *
     * @param player Represents the command sender
     */
    @Default
    fun onDefault(player: Player) {
        var parsed = msg.deserialize(
            "<green>Running <pl> version <v> by <author1> & <author2>",
            Placeholder.component("pl", Component.text("uwuJobs", NamedTextColor.GOLD)),
            Placeholder.component("v", Component.text(UwuJobs().pluginMeta.version, NamedTextColor.GOLD)),
            Placeholder.component("author1", Component.text("yellowbear", NamedTextColor.GOLD)),
            Placeholder.component("author2", Component.text("mapetr", NamedTextColor.GOLD))
        ) // Create a plugin info string and deserialize it into a text component.
        player.sendMessage(parsed) //Send the deserialized message (plugin info).

        try {
            val connection = Database.dataSource.connection
            val statement = connection.createStatement()
            for (job in Config.jobs) { // For every existing job
                val row =
                    statement.executeQuery("SELECT xp FROM ${job.name.lowercase()} WHERE id = '${player.uniqueId}'")
                if (row.next()) {
                    val xp = row.getInt("xp")
                    parsed = msg.deserialize(
                        "<gray>You have <xp> XP in proffesion <job>",
                        Placeholder.component("xp", Component.text(xp, NamedTextColor.GOLD)),
                        Placeholder.component("job", Component.text(job.name, NamedTextColor.GOLD))
                    ) //Insert the value into a string and deserialize it to a text component
                    player.sendMessage(parsed) //Send the deserialized message (job stats).
                } else {
                    player.sendMessage(
                        msg.deserialize(
                            "<gray>You have <xp> XP in proffesion <job>",
                            Placeholder.component("xp", Component.text(0, NamedTextColor.GOLD)),
                            Placeholder.component("job", Component.text(job.name, NamedTextColor.GOLD))
                        )
                    )
                }
            }
            statement.close()
            connection.close()
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Invoked when the '/jobs' command is used with 'top' as the first argument.
     *
     * @param player Represents the command sender
     * @param job String representation of the second argument
     */
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

            val rows: ResultSet

            val connection = Database.dataSource.connection
            val statement = connection.createStatement()

            // Check if the jobs argument is "all"
            if (job.equals("all", ignoreCase = true)) {
                val queryBuilder = StringBuilder()
                if (Config.config.use_mysql) {
                    queryBuilder.append("SELECT * FROM ( SELECT id, SUM(xp) AS xp FROM (")
                } else {
                    queryBuilder.append("SELECT id, SUM(xp) AS xp FROM (")
                }

                for (jobEnum in Config.jobs) {
                    queryBuilder.append("SELECT id, xp FROM ${jobEnum.name.lowercase()} WHERE NOT xp = 0 UNION ALL ")
                }

                queryBuilder.setLength(queryBuilder.length - " UNION ALL ".length)
                if (Config.config.use_mysql) {
                    queryBuilder.append(") AS all_jobs GROUP BY id) AS result_table ORDER BY xp DESC LIMIT 5")
                } else {
                    queryBuilder.append(") GROUP BY id ORDER BY xp DESC LIMIT 5")
                }

                rows = statement.executeQuery(queryBuilder.toString())
            } else {
                rows = statement.executeQuery("SELECT * FROM ${job.lowercase()} ORDER BY xp DESC LIMIT 5")
            }

            // Send message heading
            player.sendMessage(
                msg.deserialize(
                    "<white><bold>Top 5 players in <job>",
                    Placeholder.component("job", Component.text(job, NamedTextColor.GOLD))
                )
            )
            // Process all the values extracted from the database
            var i = 1
            while (rows.next()) {
                val playerLeaderboard = Bukkit.getOfflinePlayer(
                    UUID.fromString(rows.getString("id"))
                )
                val playerName = playerLeaderboard.name

                val parsed = msg.deserialize(
                    "<gray><rank>. <player>: xp <level>",
                    Placeholder.component("rank", Component.text(i, NamedTextColor.GOLD)),
                    Placeholder.component("player", Component.text(playerName!!, NamedTextColor.WHITE)),
                    Placeholder.component("level", Component.text(rows.getInt("xp"), NamedTextColor.GOLD))
                )
                player.sendMessage(parsed)
                i++
            }

            statement.close()
            connection.close()
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    //TODO: Add config reload
}
