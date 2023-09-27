package me.yellowbear.uwujobs;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import me.yellowbear.uwujobs.jobs.BlockBreak;
import me.yellowbear.uwujobs.jobs.BlockPlace;
import me.yellowbear.uwujobs.jobs.MobKill;
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
    public static void awardXp(Player player, int amount, BlockBreak job) {
        MiniMessage msg = MiniMessage.miniMessage();
        int xp;
        try {
            DbRow row = DB.getFirstRow(String.format("select xp from %s where id = '%s'", job.name().toLowerCase(), player.getUniqueId()));
            xp = row.getInt("xp");
            xp += amount;
            DB.executeUpdate(String.format("update %s set xp = %s where id = '%s'", job.name().toLowerCase(), xp, player.getUniqueId()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Component parsed = msg.deserialize(
                "<aqua><job>: <xp>XP",
                Placeholder.component("job", Component.text(job.name())),
                Placeholder.component("xp", Component.text(xp, NamedTextColor.LIGHT_PURPLE)));
        player.sendActionBar(parsed);
    }
    public static void awardXp(Player player, int amount, BlockPlace job) {
        MiniMessage msg = MiniMessage.miniMessage();
        int xp;
        try {
            DbRow row = DB.getFirstRow(String.format("select xp from %s where id = '%s'", job.name().toLowerCase(), player.getUniqueId()));
            xp = row.getInt("xp");
            xp += amount;
            DB.executeUpdate(String.format("update %s set xp = %s where id = '%s'", job.name().toLowerCase(), xp, player.getUniqueId()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Component parsed = msg.deserialize(
                "<aqua><job>: <xp>XP",
                Placeholder.component("job", Component.text(job.name())),
                Placeholder.component("xp", Component.text(xp, NamedTextColor.LIGHT_PURPLE)));
        player.sendActionBar(parsed);
    }
    public static void awardXp(Player player, int amount, MobKill job) {
        MiniMessage msg = MiniMessage.miniMessage();
        int xp;
        try {
            DbRow row = DB.getFirstRow(String.format("select xp from %s where id = '%s'", job.name().toLowerCase(), player.getUniqueId()));
            xp = row.getInt("xp");
            xp += amount;
            DB.executeUpdate(String.format("update %s set xp = %s where id = '%s'", job.name().toLowerCase(), xp, player.getUniqueId()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Component parsed = msg.deserialize(
                "<aqua><job>: <xp>XP",
                Placeholder.component("job", Component.text(job.name())),
                Placeholder.component("xp", Component.text(xp, NamedTextColor.LIGHT_PURPLE)));
        player.sendActionBar(parsed);
    }

}
