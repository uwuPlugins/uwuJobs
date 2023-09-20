package me.yellowbear.uwujobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import me.yellowbear.uwujobs.Jobs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static me.yellowbear.uwujobs.Level.getLevel;

@CommandAlias("jobs")
public class JobsCommand extends BaseCommand {
    MiniMessage msg = MiniMessage.miniMessage();

    @Default
    public void onDefault(Player player) {
        Component parsed = msg.deserialize(
                "<aqua>Running <pl> version <v> by <author1> & <author2>.",
                Placeholder.component("pl", Component.text("uwuJobs", NamedTextColor.LIGHT_PURPLE)),
                Placeholder.component("v", Component.text("1.0",NamedTextColor.LIGHT_PURPLE)),
                Placeholder.component("author1", Component.text("yellowbear",NamedTextColor.LIGHT_PURPLE)),
                Placeholder.component("author2", Component.text("mapetr",NamedTextColor.LIGHT_PURPLE))
        );
        player.sendMessage(parsed);
        try {
            for (Jobs job : Jobs.values()) {
                DbRow row = DB.getFirstRow(String.format("select xp from %s where id = '%s'", job.name().toLowerCase(), player.getUniqueId()));
                parsed = msg.deserialize(
                        "<aqua>You have level <level> in profession <job>",
                        Placeholder.component("level", Component.text(getLevel(row.getInt("xp")),NamedTextColor.LIGHT_PURPLE)),
                        Placeholder.component("job", Component.text(job.name(),NamedTextColor.LIGHT_PURPLE))
                );
                player.sendMessage(parsed);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Subcommand("top")
    @Syntax("<job>")
    @CommandCompletion("@jobs")
    @Description("Shows the top 5 players in a job")
    public void onTop(Player player, String job) {
        try {
            // Check if job exists
            if (Jobs.getJob(job) == null) {
                Component parsed = msg.deserialize(
                        "<aqua>Job <job> does not exist",
                        Placeholder.component("job", Component.text(job, NamedTextColor.LIGHT_PURPLE))
                );
                player.sendMessage(parsed);
                return;
            }

            List<DbRow> rows = DB.getResults(String.format("select id, xp from %s order by xp desc limit 5", job.toLowerCase()));
            player.sendMessage(msg.deserialize("<aqua>Top 5 players in <job>", Placeholder.component("job", Component.text(job, NamedTextColor.LIGHT_PURPLE))));
            int i = 1;
            for (DbRow row : rows) {
                Player playerLeaderboard = Bukkit.getPlayer(UUID.fromString(row.getString("id")));
                String playerName;
                if (playerLeaderboard == null) {
                    playerName = row.getString("id");
                } else {
                    playerName = playerLeaderboard.getName();
                }

                Component parsed = msg.deserialize(
                        "<aqua><rank>. <player>: xp <level>",
                        Placeholder.component("rank", Component.text(i, NamedTextColor.LIGHT_PURPLE)),
                        Placeholder.component("player", Component.text(playerName, NamedTextColor.AQUA)),
                        Placeholder.component("level", Component.text(row.getInt("xp"), NamedTextColor.LIGHT_PURPLE))
                );
                player.sendMessage(parsed);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
