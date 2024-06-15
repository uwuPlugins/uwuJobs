package me.yellowbear.uwujobs.interfaces;

import org.bukkit.configuration.file.FileConfiguration;

public interface IConfigurableService {
    void reloadConfig(FileConfiguration file);
}
