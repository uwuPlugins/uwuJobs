package me.yellowbear.uwujobs

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot

class ScoreboardController(p: Player, contents: Array<String>) {
    init {
        val manager = Bukkit.getScoreboardManager()
        val board = manager.newScoreboard
        val objective = board.registerNewObjective("test", "dummy")
        objective.displaySlot = DisplaySlot.SIDEBAR
        objective.displayName = ChatColor.LIGHT_PURPLE.toString() + "uwuJobs"
        for (i in contents.indices) {
            println(contents[i] + " #" + i)
            val score = objective.getScore(contents[i])
            score.score = contents.size - i
        }
        p.scoreboard = board
    }
}
