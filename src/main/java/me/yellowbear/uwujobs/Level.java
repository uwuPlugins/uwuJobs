package me.yellowbear.uwujobs;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Level {
    public static void awardXp(Player player, int amount, Job job) {
        int xp, next;
        try (Connection conn = DatabaseConnector.connect()) {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(String.format("select xp, next from %s where id = '%s'", job.name().toLowerCase(), player.getUniqueId()));
            xp = rs.getInt("xp");
            next = rs.getInt("next");
            statement.execute(String.format("update %s set xp = %s where id = '%s'", job.name().toLowerCase(), xp + amount, player.getUniqueId()));
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        recalculateLevels(player, job);
        player.sendMessage(
                ChatMessageType.ACTION_BAR,
                new TextComponent(job.name() + " " + (xp + amount) + "/" + next + "XP")
        );
    }

    // TODO: Bylo by lepsi tohle zlepsit
    public static int calculateLevelXp(int n) {
        return (int) Math.round(100 * (Math.pow(1.05, n)) - 50);
    }

    public static void recalculateLevels(Player player, Job job) {
        try (Connection conn = DatabaseConnector.connect()) {
            int xp, level, next;
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(String.format("select xp, level, next from %s where id = '%s'", job.name().toLowerCase(), player.getUniqueId()));
            xp = rs.getInt("xp");
            level = rs.getInt("level");
            next = rs.getInt("next");
            level++;
            if (xp >= next) {
                statement.executeUpdate(String.format("update %s set level = %s, next = %s where id = '%s'", job.name().toLowerCase(), level, xp + calculateLevelXp(level), player.getUniqueId()));
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
