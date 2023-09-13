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
                                ResultSet rs = statement.executeQuery(String.format("SELECT level FROM %s WHERE id = '%s'", job.name().toLowerCase(), player.getUniqueId().toString()));
                                commandSender.sendMessage(ChatColor.AQUA + "You have level "
                                        + ChatColor.LIGHT_PURPLE + rs.getInt("level")
                                        + ChatColor.AQUA + " in profession "
                                        + ChatColor.LIGHT_PURPLE + job.name()
                                );
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        //zapomnel jsem jak ziskat informace o pluginu, mohlo by se nekdy dodelat
                    } else {
                        if (args[0].equalsIgnoreCase("top")) {
                            try (Connection conn = DatabaseConnector.connect()) {
                                Statement statement = conn.createStatement();
                                ResultSet rs = statement.executeQuery(String.format("SELECT id, level FROM miner ORDER BY level DESC LIMIT 5;"));
                                int i = 1;
                                while (rs.next()) {
                                    commandSender.sendMessage(
                                                        ChatColor.AQUA + String.valueOf(i) + ". "
                                                        + ChatColor.LIGHT_PURPLE + rs.getString("id")
                                                                + ChatColor.AQUA + ", level "
                                                        + ChatColor.LIGHT_PURPLE + String.valueOf(rs.getInt("level"))
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
