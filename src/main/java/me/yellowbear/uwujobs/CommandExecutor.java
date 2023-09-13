package me.yellowbear.uwujobs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class CommandExecutor {
    final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

    public CommandExecutor() throws NoSuchFieldException, IllegalAccessException {
        bukkitCommandMap.setAccessible(true);
        CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

        commandMap.register("jobs", new Command("jobs") {
            @Override
            public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
                commandSender.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "uwuJobs v1.0" + ChatColor.AQUA + " by " + ChatColor.LIGHT_PURPLE + "yellowbear" + ChatColor.AQUA + " & " + ChatColor.LIGHT_PURPLE + "mapetr");
                //zapomnel jsem jak ziskat informace o pluginu, mohlo by se nekdy dodelat
                return true;
            }
        });
    }
}
