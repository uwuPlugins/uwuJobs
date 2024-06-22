package me.yellowbear.uwujobs

import co.aikar.idb.DB
import me.yellowbear.uwujobs.jobs.Job
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import java.sql.SQLException
import java.util.*

/**
 * Service responsible for actions regarding levels.
 *
 * @property awardXp Awards a player with given amount of XP in a given job.
 */
object Level {
    /**
     * Awards a player with given amount of XP in a given job.
     *
     * @param player Target player.
     * @param amount Amount of XP the target player is to receive.
     * @param job The target job to which the XP will be awarded.
     */
    fun awardXp(player: Player?, amount: Int, job: Job) {
        if (player == null) return;
        val msg = MiniMessage.miniMessage()
        var xp: Int
        try {
            val row = DB.getFirstRow("SELECT xp FROM ${job.name.lowercase()} WHERE id = '${player.uniqueId}'")
            xp = row.getInt("xp")
            xp += amount
            DB.executeUpdate("UPDATE ${job.name.lowercase()} SET xp = ${xp} WHERE id = '${player.uniqueId}'")
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
        val parsed = msg.deserialize(
            "<gray><job>: <xp> XP",
            Placeholder.component("job", Component.text(job.name)),
            Placeholder.component("xp", Component.text(xp, NamedTextColor.GOLD, TextDecoration.BOLD))
        )
        player.sendActionBar(parsed)
    }
}
