package me.yellowbear.uwujobs;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Level {
    public static void awardXp(Player player, int amount, Jobs job) {
        int xp, next;
        try (Connection conn = DatabaseConnector.connect()) {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(String.format("select xp, next from %s where id = '%s'", job.name().toLowerCase(), player.getUniqueId()));
            xp = rs.getInt("xp");
            next = rs.getInt("next");
            xp += amount;
            if (xp >= next) {
                next = getNextXp(xp);
            }
            statement.execute(String.format("update %s set xp = %s, next = %s where id = '%s'", job.name().toLowerCase(), xp, next, player.getUniqueId()));
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        player.sendMessage(
                ChatMessageType.ACTION_BAR,
                new TextComponent(job.name() + " " + xp + "/" + next + "XP")
        );
    }

    public static int getNextXp(int xp) {
        int level = getLevel(xp)+1;
        return (int) ((level*level)/0.1);
    }

    public static int getLevel(int xp) {
        return (int) Math.sqrt(xp*0.1);
    }
}
