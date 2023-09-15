package me.yellowbear.uwujobs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
                    if (args.length == 0) {
                        commandSender.sendMessage(
                                ChatColor.AQUA + "Running "
                                + ChatColor.LIGHT_PURPLE + "uwuJobs"
                                + ChatColor.AQUA + " version "
                                + ChatColor.LIGHT_PURPLE + "1.0"
                                + ChatColor.AQUA + " by "
                                + ChatColor.LIGHT_PURPLE + "yellowbear"
                                + ChatColor.AQUA + " & "
                                + ChatColor.LIGHT_PURPLE + "mapetr"
                                + "\n"
                        );


                        try (Connection conn = DatabaseConnector.connect()) {
                            Statement statement = conn.createStatement();
                            for (Jobs job : Jobs.values()) {
                                ResultSet rs = statement.executeQuery(String.format("SELECT xp FROM %s WHERE id = '%s'", job.name().toLowerCase(), player.getUniqueId()));
                                commandSender.sendMessage(ChatColor.AQUA + "You have level "
                                        + ChatColor.LIGHT_PURPLE + getLevel(rs.getInt("xp"))
                                        + ChatColor.AQUA + " in profession "
                                        + ChatColor.LIGHT_PURPLE + job.name()
                                );
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
                                    commandSender.sendMessage(
                                                        ChatColor.AQUA + String.valueOf(i) + ". "
                                                        + ChatColor.LIGHT_PURPLE + Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("id"))).getName()
                                                                + ChatColor.AQUA + ", xp "
                                                        + ChatColor.LIGHT_PURPLE + String.valueOf(rs.getInt("xp"))
                                    );
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
