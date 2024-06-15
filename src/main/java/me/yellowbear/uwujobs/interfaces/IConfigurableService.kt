package me.yellowbear.uwujobs.interfaces

import org.bukkit.configuration.file.FileConfiguration

interface IConfigurableService {
    fun reloadConfig(file: FileConfiguration?)
}
