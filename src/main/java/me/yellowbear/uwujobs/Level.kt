package me.yellowbear.uwujobs

import co.aikar.idb.DB
import me.yellowbear.uwujobs.jobs.BlockBreak
import me.yellowbear.uwujobs.jobs.BlockPlace
import me.yellowbear.uwujobs.jobs.MobKill
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import java.sql.SQLException
import java.util.*

object Level {
    fun awardXp(player: Player?, amount: Int, job: BlockBreak) {
        val msg = MiniMessage.miniMessage()
        var xp: Int
        try {
            val row = DB.getFirstRow(
                String.format(
                    "select xp from %s where id = '%s'",
                    job.name.lowercase(Locale.getDefault()),
                    player!!.uniqueId
                )
            )
            xp = row.getInt("xp")
            xp += amount
            DB.executeUpdate(
                String.format(
                    "update %s set xp = %s where id = '%s'",
                    job.name.lowercase(Locale.getDefault()),
                    xp,
                    player.uniqueId
                )
            )
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

    fun awardXp(player: Player, amount: Int, job: BlockPlace) {
        val msg = MiniMessage.miniMessage()
        var xp: Int
        try {
            val row = DB.getFirstRow(
                String.format(
                    "select xp from %s where id = '%s'",
                    job.name.lowercase(Locale.getDefault()),
                    player.uniqueId
                )
            )
            xp = row.getInt("xp")
            xp += amount
            DB.executeUpdate(
                String.format(
                    "update %s set xp = %s where id = '%s'",
                    job.name.lowercase(Locale.getDefault()),
                    xp,
                    player.uniqueId
                )
            )
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
        val parsed = msg.deserialize(
            "<aqua><job>: <xp>XP",
            Placeholder.component("job", Component.text(job.name)),
            Placeholder.component("xp", Component.text(xp, NamedTextColor.LIGHT_PURPLE))
        )
        player.sendActionBar(parsed)
    }

    fun awardXp(player: Player?, amount: Int, job: MobKill) {
        val msg = MiniMessage.miniMessage()
        var xp: Int
        try {
            val row = DB.getFirstRow(
                String.format(
                    "select xp from %s where id = '%s'",
                    job.name.lowercase(Locale.getDefault()),
                    player!!.uniqueId
                )
            )
            xp = row.getInt("xp")
            xp += amount
            DB.executeUpdate(
                String.format(
                    "update %s set xp = %s where id = '%s'",
                    job.name.lowercase(Locale.getDefault()),
                    xp,
                    player.uniqueId
                )
            )
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
        val parsed = msg.deserialize(
            "<aqua><job>: <xp>XP",
            Placeholder.component("job", Component.text(job.name)),
            Placeholder.component("xp", Component.text(xp, NamedTextColor.LIGHT_PURPLE))
        )
        player.sendActionBar(parsed)
    }
}
