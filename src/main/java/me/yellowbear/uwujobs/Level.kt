package me.yellowbear.uwujobs

import me.yellowbear.uwujobs.jobs.Job
import me.yellowbear.uwujobs.jobs.JobPlayer
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
    fun awardXp(player: Player, jobPlayer: JobPlayer, amount: Int, job: Job) {
        val msg = MiniMessage.miniMessage()
        val xp = jobPlayer.addXp(amount)
        val parsed = msg.deserialize(
            "<gray><job>: <xp> XP",
            Placeholder.component("job", Component.text(job.name)),
            Placeholder.component("xp", Component.text(xp, NamedTextColor.GOLD, TextDecoration.BOLD))
        )
        player.sendActionBar(parsed)
    }
}
