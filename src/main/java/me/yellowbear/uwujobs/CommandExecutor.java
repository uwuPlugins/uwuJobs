package me.yellowbear.uwujobs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.UUID;

import static me.yellowbear.uwujobs.Level.getLevel;

public class CommandExecutor {
    final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

    public CommandExecutor() throws NoSuchFieldException, IllegalAccessException {
        bukkitCommandMap.setAccessible(true);
        CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

        commandMap.register("jobs", new Command("jobs") {
            @Override
            public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    MiniMessage msg = MiniMessage.miniMessage();
                    Component parsed;
                    if (args.length == 0) {
                        parsed = msg.deserialize(
                                "<aqua>Running <pl> version <v> by <author1> & <author2>.",
                                Placeholder.component("pl", Component.text("uwuJobs", NamedTextColor.LIGHT_PURPLE)),
                                Placeholder.component("v", Component.text("1.0",NamedTextColor.LIGHT_PURPLE)),
                                Placeholder.component("author1", Component.text("yellowbear",NamedTextColor.LIGHT_PURPLE)),
                                Placeholder.component("author2", Component.text("mapetr",NamedTextColor.LIGHT_PURPLE))
                                );
                        commandSender.sendMessage(parsed);

                        try (Connection conn = DatabaseConnector.connect()) {
                            Statement statement = conn.createStatement();
                            for (Jobs job : Jobs.values()) {
                                ResultSet rs = statement.executeQuery(String.format("SELECT xp FROM %s WHERE id = '%s'", job.name().toLowerCase(), player.getUniqueId()));
                                parsed = msg.deserialize(
                                        "<aqua>You have level <level> in profession <job>",
                                        Placeholder.component("level", Component.text(getLevel(rs.getInt("xp")),NamedTextColor.LIGHT_PURPLE)),
                                        Placeholder.component("job", Component.text(job.name(),NamedTextColor.LIGHT_PURPLE))
                                );
                                commandSender.sendMessage(parsed);
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        //zapomnel jsem jak ziskat informace o pluginu, mohlo by se nekdy dodelat
                    } else if (args.length == 2){
                        if (args[0].equalsIgnoreCase("top") && Arrays.stream(Jobs.values()).anyMatch(job -> {
                            return job.name().equalsIgnoreCase(args[1]);
                        })) {
                            try (Connection conn = DatabaseConnector.connect()) {
                                Statement statement = conn.createStatement();
                                ResultSet rs = statement.executeQuery(String.format("SELECT id, xp FROM "+args[1].toLowerCase()+" ORDER BY xp DESC LIMIT 5;"));
                                int i = 1;
                                while (rs.next()) {
                                    parsed = msg.deserialize(
                                            "<aqua><rank>. <player>, xp <xp>",
                                            Placeholder.component("rank", Component.text(String.valueOf(i),NamedTextColor.LIGHT_PURPLE)),
                                            Placeholder.component("player", Component.text(Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("id"))).getName(),NamedTextColor.LIGHT_PURPLE)),
                                            Placeholder.component("xp", Component.text(String.valueOf(rs.getInt("xp")),NamedTextColor.LIGHT_PURPLE))
                                            );
                                    commandSender.sendMessage(parsed);
                                    i++;
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                return true;
            }
        });
    }
}
