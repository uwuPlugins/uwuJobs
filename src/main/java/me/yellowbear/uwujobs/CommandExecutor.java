package me.yellowbear.uwujobs;

import org.bukkit.Bukkit;
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

        commandMap.register("test", new Command("test") {
            @Override
            public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
                commandSender.sendMessage("kys");
                return true;
            }
        });
    }
}
