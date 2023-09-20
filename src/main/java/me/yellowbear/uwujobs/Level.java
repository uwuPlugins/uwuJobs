package me.yellowbear.uwujobs;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Level {
    public static void awardXp(Player player, int amount, Jobs job) {
        MiniMessage msg = MiniMessage.miniMessage();
        int xp, next;
        try {
            DbRow row = DB.getFirstRow(String.format("select xp, next from %s where id = '%s'", job.name().toLowerCase(), player.getUniqueId()));
            xp = row.getInt("xp");
            next = row.getInt("next");
            xp += amount;
            if (xp >= next) next = getNextXp(xp);
            DB.executeUpdate(String.format("update %s set xp = %s, next = %s where id = '%s'", job.name().toLowerCase(), xp, next, player.getUniqueId()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Component parsed = msg.deserialize(
                "<aqua><job>: <xp>/<next>XP",
                Placeholder.component("job", Component.text(job.name())),
                Placeholder.component("xp", Component.text(xp, NamedTextColor.LIGHT_PURPLE)),
                Placeholder.component("next", Component.text(next, NamedTextColor.LIGHT_PURPLE)));
        //player.sendMessage(parsed); TODO: Find an way to make this a action bar
    }

    public static int getNextXp(int xp) {
        int level = getLevel(xp)+1;
        return (int) ((level*level)/0.1);
    }

    public static int getLevel(int xp) {
        return (int) Math.sqrt(xp*0.1);
    }
}
