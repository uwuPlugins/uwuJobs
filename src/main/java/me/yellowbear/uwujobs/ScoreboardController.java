package me.yellowbear.uwujobs;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.*;

import java.util.Set;

public class ScoreboardController {

    public ScoreboardController(Player p, String[] contents) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        final Objective objective = board.registerNewObjective("test", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.LIGHT_PURPLE + "uwuJobs");
        for (int i = 0; i < contents.length; i++) {
            Score score = objective.getScore(contents[i]);
            score.setScore(contents.length-i);
        }
        p.setScoreboard(board);
    }

}
