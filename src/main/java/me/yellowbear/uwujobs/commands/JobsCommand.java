package me.yellowbear.uwujobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import me.yellowbear.uwujobs.Jobs;
import me.yellowbear.uwujobs.UwuJobs;
import me.yellowbear.uwujobs.services.ConfigService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@CommandAlias("jobs")
public class JobsCommand extends BaseCommand {
    MiniMessage msg = MiniMessage.miniMessage();

    @Default
    public void onDefault(Player player) {
        Component parsed = msg.deserialize(
                "<green>Running <pl> version <v> by <author1> & <author2>.",
                Placeholder.component("pl", Component.text("uwuJobs", NamedTextColor.GOLD)),
                Placeholder.component("v", Component.text(UwuJobs.getPlugin(UwuJobs.class).getPluginMeta().getVersion(),NamedTextColor.GOLD)),
                Placeholder.component("author1", Component.text("yellowbear",NamedTextColor.GOLD)),
                Placeholder.component("author2", Component.text("mapetr",NamedTextColor.GOLD))
        );
        player.sendMessage(parsed);
        try {
            for (Jobs job : Jobs.values()) {
                DbRow row = DB.getFirstRow(String.format("select xp from %s where id = '%s'", job.name().toLowerCase(), player.getUniqueId()));
                parsed = msg.deserialize(
                        "<gray>You have <xp> XP in profession <job>",
                        Placeholder.component("xp", Component.text(row.getInt("xp"),NamedTextColor.GOLD)),
                        Placeholder.component("job", Component.text(job.name(),NamedTextColor.GOLD))
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
            if (Jobs.getJob(job) == null && !job.equalsIgnoreCase("all")) {
                Component parsed = msg.deserialize(
                        "<gray>Job <job> does not exist",
                        Placeholder.component("job", Component.text(job, NamedTextColor.GOLD))
                );
                player.sendMessage(parsed);
                return;
            }

            List<DbRow> rows;

            if (job.equalsIgnoreCase("all")) {
                StringBuilder queryBuilder = new StringBuilder();
                queryBuilder.append("SELECT id, SUM(xp) AS xp FROM (");

                for (Jobs jobEnum : Jobs.values()) {
                    queryBuilder.append(String.format("SELECT id, xp FROM %s UNION ALL ", jobEnum.name().toLowerCase()));
                }

                queryBuilder.setLength(queryBuilder.length() - " UNION ALL ".length());
                queryBuilder.append(") GROUP BY id ORDER BY xp DESC LIMIT 5");

                rows = DB.getResults(queryBuilder.toString());
            } else {
                rows = DB.getResults(String.format("select id, xp from %s order by xp desc limit 5", job.toLowerCase()));
            }

            player.sendMessage(msg.deserialize("<white><bold>Top 5 players in <job>", Placeholder.component("job", Component.text(job, NamedTextColor.GOLD))));
            int i = 1;
            for (DbRow row : rows) {
                OfflinePlayer playerLeaderboard = Bukkit.getOfflinePlayer(UUID.fromString(row.getString("id")));
                String playerName;
                playerName = playerLeaderboard.getName();

                Component parsed = msg.deserialize(
                        "<gray><rank>. <player>: xp <level>",
                        Placeholder.component("rank", Component.text(i, NamedTextColor.GOLD)),
                        Placeholder.component("player", Component.text(playerName, NamedTextColor.WHITE)),
                        Placeholder.component("level", Component.text(row.getInt("xp"), NamedTextColor.GOLD))
                );
                player.sendMessage(parsed);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Subcommand("reload")
    @Description("Reloads config")
    public void onReload(Player player){
        ConfigService.loadConfigs();
        player.sendMessage("config reloaded");
    }
}
